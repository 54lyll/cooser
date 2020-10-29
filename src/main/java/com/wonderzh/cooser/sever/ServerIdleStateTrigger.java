package com.wonderzh.cooser.sever;

import com.wonderzh.cooser.CooServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: wonderzh
 * @Date: 2020/7/14
 * @Version: 1.0
 */
@Slf4j
public class ServerIdleStateTrigger extends ChannelInboundHandlerAdapter {

    /**
     * 断连次数
     */
    private int disconnectCount = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.warn("channel [{}] read out of heart time",ctx.channel().remoteAddress().toString());
                disconnectCount++;
                if (disconnectCount >= CooServer.MAX_DISCONNECT_PERIOD) {
                    // 在规定时间内没有收到客户端的上行数据, 主动断开连接
                    if (log.isErrorEnabled()) {
                        log.error("channel [{}] idle out of time, will be closed",ctx.channel().remoteAddress().toString());
                    }
                    ctx.disconnect();
                    //ctx.close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


}
