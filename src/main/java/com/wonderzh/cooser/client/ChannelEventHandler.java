package com.wonderzh.cooser.client;

import com.wonderzh.cooser.common.constarnt.StatusCode;
import com.wonderzh.cooser.exception.ExecutionException;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import com.wonderzh.cooser.protocol.ProtocolUrl;
import com.wonderzh.cooser.protocol.Response;
import com.wonderzh.cooser.tool.ProtoUrlResolver;
import com.wonderzh.cooser.exception.InitializeException;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: wonderzh
 * @Date: 2020/11/4
 * @Version: 1.0
 */
@Slf4j
public class ChannelEventHandler extends ChannelDuplexHandler {

    private CooClient client;
    private ClientEventListener eventListener;


    public ChannelEventHandler(CooClient client, ClientEventListener eventListener) {
        if (client == null || eventListener == null) {
            throw new InitializeException("param can not be null");
        }
        this.client = client;
        this.eventListener = eventListener;
    }

    /**
     * 建立连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            eventListener.channelActive(new ClientHolder(this.client, ctx));
        } finally {
            super.channelActive(ctx);
        }
    }

    /**
     * 如果是服务器宕机，或网络断连，首先触发ChanenlEventHandler.exceptionCaught，再触发此方法，但FutureContext中 future已释放完毕
     * 如果是服务器监测心跳异常，主动断开连接，直接触发该方法
     * 如果client.doReconnectInCommunication()不是异步方法，需要用线程异步调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            this.client.doReconnectInCommunication();
            if (FutureContext.hasFuture()) {
                FutureContext.holdException(new ExecutionException(StatusCode.CONNECT_REFUSE.code(), "与服务器断开连接"));
            }
            eventListener.channelInactive(new ClientHolder(this.client, ctx));
        } finally {
            super.channelInactive(ctx);
        }
    }


    /**
     * 读取响应与请求
     * 将Response放入对应Future，FutureContext上下文释放Future
     * 保障一条message 消费一个response
     * <p>
     * 优先保障客户端请求-响应机制，故客户端处理服务端发送的反向请求将被 try catch
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ProtocolMessage protocol = (ProtocolMessage) msg;
            if (ProtocolUrl.RESPONSE_MODE.equals(protocol.getMode())) {
                Response response = (Response) protocol.getBody();
                Long uuid = protocol.getUuid();
                if (ProtoUrlResolver.isResponse(protocol.getMode())) {
                    CooFuture future = FutureContext.get(uuid);
                    if (future != null) {
                        future.onResponse(response);
                    }
                }
            } else if (ProtocolUrl.REQUEST_MODE.equals(protocol.getMode())) {
                try {
                    eventListener.channelRead(new ClientHolder(this.client, ctx), protocol.getBody());
                } catch (Throwable cause) {
                    //eventListener.channelRead异常事件由自己处理
                    eventListener.channelException(new ClientHolder(this.client, ctx), cause);
                }
            } else {
                throw new ExecutionException(StatusCode.EXECUTION_EXCEPTION.code(), "illegal protocol mode :" + protocol.getMode());
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


    /**
     * 处理异常
     * 当通道抛出异常，触发顺序为：
     * 1触发当前所有CooFuture(监听request对应的response)的异常处理事件
     * 2触发ClientEventListener异常捕捉事件
     * <p>
     * CooFuture不处理ClientEventListener.channelRead抛出的异常事件
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            if (FutureContext.hasFuture()) {
                FutureContext.holdException(cause);
            }
            eventListener.channelException(new ClientHolder(this.client, ctx), cause);
        } catch (Throwable thr) {
            log.error("ChannelEventHandler.exceptionCaught execute error",thr);
        }

    }


}
