package com.wonderzh.cooser.sever.handler;

import com.wonderzh.cooser.sever.factory.BasicComponentDefinition;

import java.lang.reflect.Method;

/**
 * @Author: wonderzh
 * @Date: 2020/9/8
 * @Version: 1.0
 */

public class EventHandlerDefinition extends BasicComponentDefinition {

    /**
     * @ProtocolMapping 注解的方法
     */
    private Method method;

    private Class<?>[] parameterTypes;

    private int parameterCount;

    public EventHandlerDefinition(Method method, Object instance) {
        this.method = method;
        this.instance = instance;
        this.className = instance.getClass().getName();
        this.parameterTypes=method.getParameterTypes();
        this.parameterCount = method.getParameterCount();
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public int getParameterCount() {
        return parameterCount;
    }
}
