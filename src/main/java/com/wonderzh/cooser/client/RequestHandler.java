package com.wonderzh.cooser.client;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: wonderzh
 * @Date: 2020/10/15
 * @Version: 1.0
 */

public interface RequestHandler {

    /**
     * 处理服务端发送的请求
     * @param ctx
     * @param msg ? extends  google.proto.Message
     * @throws Exception
     */
    void ChannelRead(ChannelHandlerContext ctx, Object msg) throws Exception;

}
