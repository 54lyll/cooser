package com.wonderzh.cooser.tool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @Author: wonderzh
 * @Date: 2020/5/1
 * @Version: 1.0
 */

public class ThreadPool {

    public static ExecutorService newFixedThreadPool(String name, int core, int max,int aliveTime, int queue) {
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat(name + "-%d").build();
        ExecutorService executor = new ThreadPoolExecutor(core, max, aliveTime, TimeUnit.SECONDS, new LinkedBlockingDeque<>(queue),
                nameThreadFactory);
        return executor;
    }

    public static ExecutorService newCacheTreadPool(String name, int core,int max) {
        ThreadFactory nameThreadFactory = (new ThreadFactoryBuilder()).setNameFormat(name + "-%d").build();
        ExecutorService executor = new ThreadPoolExecutor(core, max, 0, TimeUnit.SECONDS, new SynchronousQueue(), nameThreadFactory);
        return executor;
    }
}
