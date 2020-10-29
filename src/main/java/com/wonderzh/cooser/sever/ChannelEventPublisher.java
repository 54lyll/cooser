package com.wonderzh.cooser.sever;

import com.wonderzh.cooser.tool.ObjectUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wonderzh
 * @Date: 2020/10/16
 * @Version: 1.0
 */

public class ChannelEventPublisher {

    List<ChannelLifecycleListener> listeners = new ArrayList<>();

    public void addListeners(List<ChannelLifecycleListener> channelLifecycleListeners) {
        if (!ObjectUtil.isEmpty(channelLifecycleListeners)) {
            this.listeners.addAll(channelLifecycleListeners);
        }
    }


    /**
     * 通道关闭
     * @param ctxHolder
     */
    public void inactiveEvent(ChannelHandlerContextHolder ctxHolder) {
        if (hasListener()) {
            for (ChannelLifecycleListener listener : listeners) {
                listener.channelInactive(ctxHolder);
            }
        }
    }

    public void activeEvent(ChannelHandlerContext ctx) {
        if (hasListener()) {
            for (ChannelLifecycleListener listener : listeners) {
                listener.channelActive(ctx);
            }
        }
    }

    public boolean hasListener() {
        return listeners.size() > 0;
    }
}
