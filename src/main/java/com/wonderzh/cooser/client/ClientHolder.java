package com.wonderzh.cooser.client;

import com.google.protobuf.Message;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author: wonderzh
 * @Date: 2020/11/5
 * @Version: 1.0
 */

public class ClientHolder {

    private CooClient client;

    private ChannelHandlerContext ctx;

    public ClientHolder(CooClient client, ChannelHandlerContext ctx) {
        this.client = client;
        this.ctx = ctx;
    }

    public CooClient getClient() {
        return client;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public <T extends Message> CooFuture<T> send(ProtocolMessage message, Class<T> responseType) {
        return client.send(message, responseType);
    }
}
