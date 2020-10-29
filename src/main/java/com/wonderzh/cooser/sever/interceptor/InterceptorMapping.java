package com.wonderzh.cooser.sever.interceptor;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: wonderzh
 * @Date: 2020/9/9
 * @Version: 1.0
 */

public class InterceptorMapping {

    private static final ConcurrentHashMap<String, List<PathMatchingInterceptor>> CACHE = new ConcurrentHashMap<>();

    private List<PathMatchingInterceptor> matchingInterceptors = new ArrayList<>();


    public void addMappedChannelInterceptor(List<ChannelInterceptorHolder> holders) {
        for (ChannelInterceptorHolder holder : holders) {
            matchingInterceptors.add(new MappedChannelInterceptor(holder));
        }
    }

    /**
     * 查询路径匹配的拦截器
     *  无拦截器，缓存empty
     *  允许局部时间段数据不一致性，换取读取性能
     * @param path
     * @return matchedInterceptors or empty
     */
    public List<PathMatchingInterceptor> getInterceptors(String path) {
        if (path == null) {
            return null;
        }

        if (CACHE.contains(path)) {
            return CACHE.get(path);
        } else {
            List<PathMatchingInterceptor> matchedInterceptors = new ArrayList<>();
            for (PathMatchingInterceptor interceptor : matchingInterceptors) {
                if (interceptor.match(path)) {
                    matchedInterceptors.add(interceptor);
                }
            }
            CACHE.put(path, matchedInterceptors);
            return matchedInterceptors;
        }

    }

    public void clear() {
        CACHE.clear();
        matchingInterceptors.clear();
    }
}
