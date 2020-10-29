package com.wonderzh.cooser.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: wonderzh
 * @Date: 2020/6/2
 * @Version: 1.0
 */
@Slf4j
public class EventProducer {

    private String id;
    private RingBuffer<EventContext> ringBuffer;

    public EventProducer(String id, RingBuffer<EventContext> ringBuffer) {
        this.id = id;
        this.ringBuffer = ringBuffer;
    }

    public void onMessage(String channelId, ChannelHandlerContext ctx, ProtocolMessage protocol)  {
        long sequence = ringBuffer.next();
        try {
            EventContext adapter = ringBuffer.get(sequence);
            adapter.setChannelId(channelId);
            adapter.setCtx(ctx);
            adapter.setPath(protocol.getProtocol());
            adapter.setProtocolMessage(protocol);
            adapter.setData(protocol.getBody());
            adapter.setInitializeState(true);
        } finally {
            ringBuffer.publish(sequence);
        }

    }
}
