package com.wonderzh.cooser.client;

import com.wonderzh.cooser.ExceptionHandler;
import com.wonderzh.cooser.common.constarnt.StatusCode;
import com.wonderzh.cooser.exception.ExecutionException;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import com.wonderzh.cooser.protocol.ProtocolUrl;
import com.wonderzh.cooser.protocol.Response;
import com.wonderzh.cooser.tool.ProtoUrlResolver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端处理服务端响应类
 *
 * @Author: wonderzh
 * @Date: 2020/7/10
 * @Version: 1.0
 */
@Slf4j
public class ResponseHandler extends ChannelInboundHandlerAdapter {

    /**
     * 异常监听类，处理客服端异常
     */
    private ExceptionHandler exceptionHandler;

    private RequestHandler requestHandler;

    public ResponseHandler(ExceptionHandler exceptionHandler, RequestHandler requestHandler) {
        this.exceptionHandler = exceptionHandler;
        this.requestHandler = requestHandler;
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        FutureContext.holdException(new ExecutionException(StatusCode.CONNECT_REFUSE.code(),"服务器主动断开Channel"));
        super.channelInactive(ctx);
    }


    /**
     * 读取响应与请求
     * 将Response放入对应Future，FutureContext上下文释放Future
     * 保障一条message 消费一个response
     *
     * 优先保障客户端请求-响应机制，故客户端处理服务端发送的反向请求将被 try catch
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Long uuid=null;
        try {
            ProtocolMessage protocol = (ProtocolMessage) msg;
            if (ProtocolUrl.RESPONSE_MODE.equals(protocol.getMode())) {
                Response response = (Response) protocol.getBody();
                uuid = protocol.getUuid();
                if (ProtoUrlResolver.isResponse(protocol.getMode())) {
                    CooFuture future = FutureContext.get(uuid);
                    if (future != null) {
                        future.onResponse(response);
                    }
                }
            } else if (ProtocolUrl.REQUEST_MODE.equals(protocol.getMode())) {
                if (this.requestHandler != null) {
                    try {
                        this.requestHandler.ChannelRead(ctx, protocol.getBody());
                    } catch (Exception e) {
                        this.exceptionHandler.caught(e);
                    }
                }
            } else {
                throw new ExecutionException(StatusCode.EXECUTION_EXCEPTION.code(), "illegal protocol mode :" + protocol.getMode());
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 处理异常,关闭通道
     * 当通道抛出异常，应该触发通道当前所有CooFuture(监听request对应的response)的异常处理事件
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (FutureContext.hasFuture()) {
            FutureContext.holdException(cause);
        } else if (exceptionHandler != null) {
            exceptionHandler.caught(cause);
        }
        ctx.close();
    }
}
