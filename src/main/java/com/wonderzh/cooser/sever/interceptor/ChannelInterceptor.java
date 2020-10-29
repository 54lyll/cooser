package com.wonderzh.cooser.sever.interceptor;

import com.wonderzh.cooser.protocol.ProtocolMessage;

import java.util.Map;

/**
 * 通道过滤器
 * @Author: wonderzh
 * @Date: 2020/9/9
 * @Version: 1.0
 */

public interface ChannelInterceptor {

    /**
     * 前置拦截
     * @param msg
     * @param attributes
     * @return
     */
    boolean preHandle(ProtocolMessage msg, Map<String, Object> attributes);

    /**
     * 后置拦截
     * @param response
     * @param attributes
     */
    void afterHandle(ProtocolMessage response, Map<String, Object> attributes);

}
