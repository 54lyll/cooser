package com.wonderzh.cooser.sever;

import com.wonderzh.cooser.exception.InitializeException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: wonderzh
 * @Date: 2020/6/2
 * @Version: 1.0
 */
@Slf4j
public class NetClient {

    /**
     * 远程服务端地址
     */
    private InetSocketAddress remoteAddress;
    /**
     * Boot导航
     */
    private Bootstrap bootstrap;
    /**
     * 工作线程组
     */
    private EventLoopGroup worker;
    /**
     * 连接通道
     */
    private Channel channel;
    private ChannelFuture future;
    /**
     * handler初始化类
     */
    private ChannelInitializer initializer;
    /**
     * 连接重试次数
     */
    private int retries = 2;
    private long reconnectInterval = 1 * 1000L;
    /**
     * 关闭状态
     */
    private volatile boolean close = false;
    /**
     * 连接同步
     */
    private volatile boolean connected = false;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock rLock = readWriteLock.readLock();
    private final Lock wLock = readWriteLock.writeLock();


    public NetClient() {

    }

    public NetClient(String host, int port) {
        this.remoteAddress = new InetSocketAddress(host, port);
    }

    /**
     * 注册channelInitializer ，addFirstHandler与addLastHandler 将无效
     * handler注册优先使用方式
     *
     * @param channelInitializer
     * @return
     */
    public NetClient registerChannelInitializer(ChannelInitializer channelInitializer) {
        this.initializer = channelInitializer;
        return this;
    }

    /**
     * 重连接
     *
     * @param retries       重试次数
     * @param intervalMills 重试间隔时间，毫秒
     * @return
     */
    public NetClient setReconnect(int retries, long intervalMills) {
        if (retries < 0) {
            throw new IllegalArgumentException("retries can't be negative");
        }
        if (intervalMills < 0) {
            throw new IllegalArgumentException("interval can't be negative");
        }
        if (intervalMills < 1) {
            intervalMills = 1;
        }
        this.retries = retries;
        this.reconnectInterval = intervalMills;
        return this;
    }

    /**
     * 建立连接
     *
     * @param host
     * @param port
     * @return
     */
    public ChannelFuture connect(String host, int port) throws ConnectException {
        remoteAddress = new InetSocketAddress(host, port);
        return connect();
    }

    /**
     * 建立连接
     *
     * @return
     */
    public ChannelFuture connect() throws ConnectException {
        if (remoteAddress == null) {
            throw new InitializeException("please configuration remote address");
        }
        return init();
    }

    /**
     * 初始化bootstrap
     *
     * @return
     */
    private ChannelFuture init() throws ConnectException {
        worker = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.TCP_NODELAY, true);
            if (initializer != null) {
                bootstrap.handler(initializer);
            }
            //客户端断线重连逻辑
            doRetrialConnect();
        } catch (ConnectException e) {
            log.error("client initialize failed", e);
            close();
            throw e;
        }
        return future;
    }

    /**
     * 连接服务端 ，含重连机制
     * Netty初次启动客户端，如果无法连接到服务端，将尝试重连
     * 重连机制为异步执行（释放了锁，不能使用notify机制），需要锁条件变量机制与send()同步
     */
    public void doRetrialConnect() throws ConnectException {
        wLock.lock();
        try {
            if (channel != null && channel.isActive()) {
                return;
            }
            connected = false;
            retryConnect();
            if (!connected) {
                throw new ConnectException("connection retries failure");
            }
        } finally {
            wLock.unlock();
        }
    }

    private void retryConnect() {
        try {
            future = bootstrap.connect(remoteAddress).sync();
            channel = future.channel();
            connected = true;
            this.close = false;
        } catch (Exception e) {
            if (retries > 0) {
                retries--;
                log.info("Retry connection after {} mills", reconnectInterval);
                try {
                    Thread.sleep(reconnectInterval);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                retryConnect();
            }
        }
    }

    /**
     * 在客户端与服务端保持长连接的过程中，如果连接断开，可调用此方法一直重连。
     * 此时重连接执行过程中，不阻塞send()
     */
    public void reconnectForever() {
        this.future = bootstrap.connect(remoteAddress);
        this.future.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("client has connected to {} ", remoteAddress.getHostString());
                this.channel = future.channel();
                this.connected = true;
                this.close = false;
            } else {
                log.info("Retry connection after {} mills",reconnectInterval*2);
                final EventLoop loop = future.channel().eventLoop();
                loop.schedule(new Runnable() {
                    @Override
                    public void run() {
                        reconnectForever();
                    }
                }, reconnectInterval*2, TimeUnit.MILLISECONDS);
            }
        });
    }


    /**
     * 异步发送
     *
     * @param message
     * @param <T>
     * @return
     */
    public <T> ChannelFuture send(T message) throws IOException {
        rLock.lock();
        try {
            //关闭状态
            if (close) {
                throw new IllegalStateException("send error cause: connection has closed");
            }
            if (!connected) {
                throw new IOException("send error cause: Connection is refused");
            }
            if (channel.isActive()) {
                return channel.writeAndFlush(message);
            } else {
                throw new IOException("send error cause: channel is inactive");
            }
        } finally {
            rLock.unlock();
        }

    }

    /**
     * 关闭
     */
    public void close() {
        wLock.lock();
        try {
            if (future != null) {
                //future.channel().closeFuture().sync();
                future.channel().close();
            }
            if (worker != null) {
                worker.shutdownGracefully();
            }
            log.info("client closed");
            this.connected = false;
            this.close = true;
        } finally {
            wLock.unlock();
        }

    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public boolean isConnected() {
        return connected;
    }


}

