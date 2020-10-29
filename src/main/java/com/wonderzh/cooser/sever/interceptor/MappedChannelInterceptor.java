package com.wonderzh.cooser.sever.interceptor;

import com.wonderzh.cooser.exception.ComponentBeanInitializeException;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.List;
import java.util.Map;

/**
 * @Author: wonderzh
 * @Date: 2020/9/10
 * @Version: 1.0
 */

public class MappedChannelInterceptor implements PathMatchingInterceptor {

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

    private PathMatcher pathMatcher = new AntPathMatcher();

    public MappedChannelInterceptor(ChannelInterceptorHolder holder) {
        if (holder == null) {
            throw new ComponentBeanInitializeException("InterceptorHolder must not be null");
        }
        this.pathPattern = holder.getPathPattern();
        this.excludePathList = holder.getExcludePathList();
        this.realInterceptor = holder.getRealInterceptor();
    }

    @Override
    public boolean preHandle(ProtocolMessage msg, Map<String, Object> attributes) {
        return realInterceptor.preHandle(msg, attributes);
    }

    @Override
    public void afterHandle(ProtocolMessage response, Map<String, Object> attributes) {
        realInterceptor.afterHandle(response, attributes);
    }

    @Override
    public boolean match(String path) {
        if (path == null) {
            return false;
        }
        if (PATH_INTERCEPT_NONE.equals(getPathPattern())) {
            return false;
        }
        return pathMatcher.match(getPathPattern(),path);
    }

    @Override
    public String getPathPattern() {
        if (pathPattern == null) {
            return PATH_INTERCEPT_NONE;
        }
        return this.pathPattern;
    }
}
