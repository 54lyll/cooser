package com.wonderzh.cooser.sever;

import io.netty.channel.ChannelHandlerContext;

/**
 * 通道生命周期监听
 * @Author: wonderzh
 * @Date: 2020/10/16
 * @Version: 1.0
 */

public interface ChannelLifecycleListener {

    /**
     * 通道活跃
     *
     * @param ctx
     */
    void channelActive(ChannelHandlerContext ctx);

    /**
     * 通道结束
     * @param ctxHolder
     */
    void channelInactive(ChannelHandlerContextHolder ctxHolder);

}
