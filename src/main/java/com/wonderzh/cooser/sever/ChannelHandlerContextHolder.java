package com.wonderzh.cooser.sever;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: wonderzh
 * @Date: 2020/10/16
 * @Version: 1.0
 */

public class ChannelHandlerContextHolder {

    private String id;
    private ChannelHandlerContext context;

    public ChannelHandlerContextHolder(String id, ChannelHandlerContext context) {
        this.id = id;
        this.context = context;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }
}
