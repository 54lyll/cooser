package com.wonderzh.cooser.client;

import com.wonderzh.cooser.protocol.ProtocolMessage;
import com.wonderzh.cooser.protocol.ProtocolUrl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 心跳与断线重连
 *
 * @Author: wonderzh
 * @Date: 2020/7/14
 * @Version: 1.0
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private long time=60*1000L;

    private ProtocolMessage protocol;


    public HeartBeatHandler(ProtocolMessage protocol) {
        this.protocol = protocol;
    }

    /**
     * 当客户端向服务端超时未发送消息，立即发送心跳
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                //定时发送心跳
                ProtocolMessage protoIns = protocol.newInstance();
                protoIns.setMode(ProtocolUrl.HEART_MODE);
                //直接用ctx writeAndFlush会从当前channel发信息，
                ctx.writeAndFlush(protoIns);
            }
            if (state == IdleState.READER_IDLE) {
                //超时未收到服务器响应，重新连接
                //client.doRetrialConnect();
                log.warn("心跳监测：服务端响应超时");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    /**
     * 异步调度任务，发送心跳
     * @param channel
     */
    private void pingSchedule(Channel channel) {
        ProtocolMessage protoIns = protocol.newInstance();
        protoIns.setProtocol(ProtocolUrl.PATH_PING);
        ScheduledFuture<?> future = channel.eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                //channel close 但eventloop没有关闭，该任务还会一直执行下去，需要break
                if (channel.isActive()) {
                    log.debug("sending heart beat to the server...");
                    channel.writeAndFlush(protoIns);
                } else {
                    log.debug("The connection had broken, cancel the task that will send a heart beat.");
                    channel.closeFuture();
                    throw new RuntimeException();
                }
            }
        }, time, TimeUnit.MILLISECONDS);

        future.addListener(new GenericFutureListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                if (future.isSuccess()) {
                    pingSchedule(channel);
                }
            }
        });
    }


}
