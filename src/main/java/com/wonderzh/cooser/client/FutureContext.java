package com.wonderzh.cooser.client;


import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局 Future上下文
 * @Author: wonderzh
 * @Date: 2020/7/10
 * @Version: 1.0
 */

public class FutureContext {

    /**
     * 一个请求对应一个HprotoFuture
     */
    private static final ConcurrentHashMap<Long, CooFuture<?>> CONTAINER = new ConcurrentHashMap<>();

    /**
     * 记录Future
     * @param future
     */
    public static void apply(CooFuture<?> future) {
        if (future == null) {
            return;
        }
        CONTAINER.put(future.getUuid(), future);
    }

    /**
     * 获取future
     * @param uuid
     * @return
     */
    public static CooFuture get(Long uuid) {
        if (uuid == null) {
            return null;
        }
        return CONTAINER.get(uuid);
    }

    /**
     * 释放future
     * @param uuid
     */
    public static void release(Long uuid) {
        //TODO  一直未响应的请求如何释放？
        if (uuid == null) {
            return;
        }
        CONTAINER.remove(uuid);
    }

    public static void holdException(Throwable cause) {
        for (CooFuture<?> future : CONTAINER.values()) {
            future.holdException(cause);
        }
    }

    public static boolean hasFuture() {
        return CONTAINER.size() > 0;
    }
}
