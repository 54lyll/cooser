package com.wonderzh.cooser.sever.handler;

import com.wonderzh.cooser.tool.ObjectUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: wonderzh
 * @Date: 2020/9/11
 * @Version: 1.0
 */

public class EventHandlerMapping {

    /**
     * key；path
     * value: EventHandler执行代理
     */
    private static final ConcurrentHashMap<String, EventHandlerDelegate> MAPPING = new ConcurrentHashMap<>();

    /**
     * 注册EventHandler
     * EventHandlerDelegate代理EventHandlerDefinition
     * @param path
     * @param eventHandlerDefinition
     */
    public void add(String path, EventHandlerDefinition eventHandlerDefinition) {
        if (ObjectUtil.isBlank(path) || ObjectUtil.isNull(eventHandlerDefinition)) {
            return;
        }
        MAPPING.put(path, new EventHandlerDelegate(eventHandlerDefinition));
    }

    public EventHandlerDelegate getHandlerDelegate(String path) {
        if (path == null) {
            return null;
        }
        return MAPPING.get(path);
    }

    public void clear() {
        MAPPING.clear();
    }
}
