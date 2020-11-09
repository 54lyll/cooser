package com.wonderzh.cooser.client;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.wonderzh.cooser.common.constarnt.StatusCode;
import com.wonderzh.cooser.exception.ExecutionException;
import com.wonderzh.cooser.protocol.Response;
import com.wonderzh.cooser.tool.ThreadPool;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 同步Future
 *
 * @Author: wonderzh
 * @Date: 2020/7/10
 * @Version: 1.0
 */
@Slf4j
public class CooFuture<T extends Message> {

    /**
     * 执行listener的线程池
     */
    private static final ExecutorService pool = ThreadPool.newFixedThreadPool("future-listener", 5, 8, 5, 128);

    /**
     * message  id
     */
    private long uuid;
    /**
     * Response 协议
     */
    private long readTimeout;
    /**
     * 响应体实例
     */
    private volatile Object entity;
    /**
     * 响应体类型
     */
    private Class<T> responseType;
    /**
     * 线程同步，
     */
    private CountDownLatch latch = new CountDownLatch(1);

    private volatile boolean isDone = false;

    private volatile boolean doResponse = false;

    private LinkedBlockingQueue<Runnable> waitListeners;


    public CooFuture(long uuid, Class<T> responseType, long readTimeout) {
        this.uuid = uuid;
        this.responseType = responseType;
        this.readTimeout = readTimeout;
    }

    /**
     * 阻塞获取response
     *
     * @return
     */
    public T get() throws ExecutionException {
        T value = getDone(this.entity);
        if (value == null) {
            try {
                latch.await();
            } catch (InterruptedException ignore) {
                ignore.printStackTrace();
            }
        }
        return getDone(this.entity);
    }


    /**
     * 阻塞获取response
     * 超时等待，避免永久休眠
     *
     * @param timeout
     * @param unit
     * @return
     * @throws TimeoutException
     */
    public T get(long timeout, TimeUnit unit) throws ExecutionException, com.wonderzh.cooser.exception.TimeoutException {
        T value = getDone(this.entity);
        if (value == null) {
            try {
                if (!latch.await(timeout, unit)) {
                    throw new com.wonderzh.cooser.exception.TimeoutException("read time out");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return getDone(this.entity);
    }

    private T getDone(Object entity) throws ExecutionException {
        if (entity instanceof FailureResult) {
            FailureResult failure = (FailureResult) entity;
            if (failure.getException() != null) {
                throw new ExecutionException(failure.getStatus(), failure.getMessage(), failure.getException());
            } else {
                throw new ExecutionException(failure.getStatus(), failure.getMessage());
            }
        }
        T value = null;
        try {
            value = (T) entity;
        } catch (ClassCastException e) {
            throw new ExecutionException(StatusCode.EXECUTION_EXCEPTION.code(), e);
        }
        return value;
    }

    /**
     * 注入Response,基本由Netty EventLoop线程调用
     * 如果服务端返回2000  Ok，解码响应实体Entity，唤醒get线程，标志结束符
     * 否则处理异常，唤醒get线程，标志结束符
     *
     * @param response
     */
    protected void onResponse(Response response) {
        if (response == null) {
            return;
        }
        try {
            this.doResponse = true;
            Any any = response.getData();
            if (response.getStatus() != StatusCode.OK.code()) {
                //异常响应
                this.entity = new FailureResult(response.getStatus(), response.getMessage());
            } else {
                //正常响应
                this.entity = any.unpack(responseType);
            }
        } catch (InvalidProtocolBufferException e) {
            this.entity = new FailureResult(StatusCode.EXECUTION_EXCEPTION.code(), e.getMessage(), e);
        } finally {
            //唤醒get()线程，释放FutureContext资源
            this.latch.countDown();
            //执行等待的listener
            synchronized (this) {
                this.isDone = true;
                executeWaitListener();
            }
            FutureContext.release(this.uuid);
        }
    }

    /**
     * 注册监听
     * 监听事件由独立线程池执行
     *
     * @param futureCallback
     */
    public void addListener(FutureCallback<T> futureCallback) {
        if (futureCallback == null) {
            throw new NullPointerException();
        }
        Runnable callbackListener = new Runnable() {
            @Override
            public void run() {
                final T value;
                try {
                    try {
                        //获取响应
                        value = get(readTimeout, TimeUnit.MILLISECONDS);
                    } catch (ExecutionException e1) {
                        //异常触发onError事件
                        futureCallback.onError(e1);
                        return;
                    } catch (Throwable e2) {
                        //异常触发onError事件
                        futureCallback.onError(e2);
                        return;
                    }
                    try {
                        //成功触发onSuccess事件
                        futureCallback.onSuccess(value);
                    } catch (Exception e) {
                        futureCallback.onError(e);
                    }
                } catch (Exception onError) {
                    //捕获callback.onError方法抛出的异常
                    log.error("FutureCallback onError exception", onError);
                }
            }
        };

        //没有响应结果，放入等待队列
        synchronized (this) {
            if (!this.isDone) {
                waitInQueue(callbackListener);
                return;
            }
        }
        pool.execute(callbackListener);
    }


    /**
     * 捕获ChannelHandler 通道异常
     * 捕获ChannelInactive 通道事件
     * 捕获NettyClient send() throw IOException异常
     * <p>
     * holdException() 与 onResponse() 方法存在竞争
     * 竞争1：对entity赋值
     * 竞争2：对Future的释放FutureContext.release(),
     * 以 onResponse()为优先
     *
     * @param e
     */
    protected void holdException(Throwable e) {
        if (!doResponse) {
            int status;
            if (e instanceof IOException) {
                status = StatusCode.CONNECT_IO_EXCEPTION.code();
            } else {
                status = StatusCode.EXECUTION_EXCEPTION.code();
            }
            this.entity = new FailureResult(status, e.getMessage(), e);
            this.latch.countDown();
            synchronized (this) {
                this.isDone = true;
                executeWaitListener();
            }
            FutureContext.release(this.uuid);
        }
    }

    private void waitInQueue(Runnable callbackListener) {
        if (waitListeners == null) {
            waitListeners = new LinkedBlockingQueue<>(128);
        }
        waitListeners.offer(callbackListener);
    }

    private void executeWaitListener() {
        if (this.waitListeners != null) {
            Runnable callbackListener = this.waitListeners.poll();
            while (callbackListener != null) {
                pool.execute(callbackListener);
                callbackListener = this.waitListeners.poll();
            }
        }
    }

    /**
     * 将ChannelHandler  exceptionCaugth与 onError事件关联
     *
     * @param channelFuture
     */
    protected void registerChannelFuture(ChannelFuture channelFuture) {
        channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (!future.isSuccess()) {
                    Throwable cause = future.cause();
                    entity = new FailureResult(StatusCode.EXECUTION_EXCEPTION.code(), cause.getMessage(), cause);
                    isDone = true;
                    executeWaitListener();
                    latch.countDown();
                    FutureContext.release(uuid);
                }
            }
        });
    }

    /**
     * 一次请求-响应周期完成
     *
     * @return
     */
    public boolean isDone() {
        return isDone;
    }

    public long getUuid() {
        return uuid;
    }

    /**
     * 失败响应
     */
    protected static class FailureResult {

        private int status;
        private String message;
        private Throwable exception;

        public FailureResult() {

        }

        public FailureResult(int status, String message, Throwable cause) {
            this.status = status;
            this.message = message;
            this.exception = cause;
        }

        public FailureResult(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Throwable getException() {
            return exception;
        }

        public void setException(Throwable exception) {
            this.exception = exception;
        }
    }

}
