package com.wonderzh.cooser.sever.interceptor;

/**
 * 可路径匹配的过滤器
 * @Author: wonderzh
 * @Date: 2020/9/9
 * @Version: 1.0
 */

public interface PathMatchingInterceptor extends ChannelInterceptor {

    String PATH_INTERCEPT_NONE = "#";

    boolean match(String path);

    String getPathPattern();
}
