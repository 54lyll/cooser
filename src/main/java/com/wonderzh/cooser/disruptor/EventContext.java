package com.wonderzh.cooser.disruptor;

import com.wonderzh.cooser.protocol.ProtocolMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: wonderzh
 * @Date: 2020/6/2
 * @Version: 1.0
 */

public class EventContext {

    /**
     * 是否真正初始化，用于区别Disruptor预先初始化
     */
    private boolean hasInitialized;
    /**
     * 通道id
     */
    private String channelId;
    /**
     * Request url path
     */
    private String path;

    /**
     * 请求的完整协议报文
     */
    private ProtocolMessage protocolMessage;

    /**
     * 请求报文的消息体
     */
    private Object data;

    /**
     * Netty Channel 上下文
     */
    private ChannelHandlerContext ctx;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ProtocolMessage getProtocolMessage() {
        return protocolMessage;
    }

    public void setProtocolMessage(ProtocolMessage protocol) {
        this.protocolMessage = protocol;
    }


    public boolean hasInitialized() {
        return hasInitialized;
    }

    public void setInitializeState(boolean hasInitialize) {
        this.hasInitialized = hasInitialize;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public boolean isEmpty() {
        if (path == null && protocolMessage == null && data == null) {
            return true;
        } else {
            return false;
        }
    }
}
