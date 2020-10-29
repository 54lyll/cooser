package com.wonderzh.cooser.sever.interceptor;

import java.util.Arrays;
import java.util.List;

/**
 * 拦截器注册工具类
 *
 * @Author: wonderzh
 * @Date: 2020/9/9
 * @Version: 1.0
 */

public class ChannelInterceptorHolder {

    /**
     * 匹配地址
     * Ant语法
     */
    private String pathPattern;

    /**
     * 排除地址，即白名单
     */
    private List<String > excludePathList;

    private ChannelInterceptor realInterceptor;

    private ChannelInterceptorHolder(ChannelInterceptor interceptor) {
        this.realInterceptor = interceptor;
    }

    protected static ChannelInterceptorHolder hold(ChannelInterceptor interceptor) {
        return new ChannelInterceptorHolder(interceptor);
    }

    /**
     * 添加路径模板
     * Ant语法规则
     * @param pathPattern
     * @return
     */
    public ChannelInterceptorHolder addPathPattern(String pathPattern) {
        this.pathPattern = pathPattern;
        return this;
    }

    /**
     * 添加白名单
     * @param excludePath
     * @return
     */
    public ChannelInterceptorHolder addExcludePath(String... excludePath) {
        if (excludePath != null && excludePath.length > 0) {
            excludePathList.addAll(Arrays.asList(excludePath));
        }
        return this;
    }

    protected String getPathPattern() {
        return pathPattern;
    }

    protected List<String> getExcludePathList() {
        return excludePathList;
    }

    protected ChannelInterceptor getRealInterceptor() {
        return realInterceptor;
    }
}
