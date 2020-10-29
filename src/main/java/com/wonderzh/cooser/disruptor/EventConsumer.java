package com.wonderzh.cooser.disruptor;

import com.lmax.disruptor.WorkHandler;

/**
 * @Author: wonderzh
 * @Date: 2020/6/2
 * @Version: 1.0
 */

public abstract class EventConsumer implements WorkHandler<EventContext> {

    protected String id;

    public abstract String getId();


}
