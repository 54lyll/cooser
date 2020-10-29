package com.wonderzh.cooser.sever.handler;

import com.wonderzh.cooser.exception.MultiProtocolMappingException;
import com.wonderzh.cooser.sever.factory.ComponentDefinition;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * EventHandler配置器
 *
 * @Author: wonderzh
 * @Date: 2020/9/9
 * @Version: 1.0
 */

public class EventHandlerConfigureAware {

    /**
     * 创建EventHandlerMapping 并注册EventHandler
     *
     * @param definitions
     * @return
     */
    public EventHandlerMapping registerEventHandler(List<ComponentDefinition> definitions) {
        EventHandlerMapping handlerMapping = new EventHandlerMapping();
        Set<String> methodFilter = new HashSet<>();
        for (ComponentDefinition definition : definitions) {
            Class clazz = definition.getInstance().getClass();
            //扫描@ProtocolMapping的方法
            for (Method method : clazz.getMethods()) {
                ProtocolMapping protocolAnt = method.getAnnotation(ProtocolMapping.class);
                if (protocolAnt != null) {
                    if (methodFilter.contains(protocolAnt.value())) {
                        throw new MultiProtocolMappingException(protocolAnt.value() + " already exist!");
                    } else {
                        methodFilter.add(protocolAnt.value());
                    }
                    method.setAccessible(true);
                    handlerMapping.add(protocolAnt.value(),
                            new EventHandlerDefinition(method, definition.getInstance()));
                }
            }
        }
        return handlerMapping;
    }
}
