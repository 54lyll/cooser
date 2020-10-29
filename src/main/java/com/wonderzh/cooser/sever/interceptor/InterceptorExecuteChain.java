package com.wonderzh.cooser.sever.interceptor;

import com.wonderzh.cooser.protocol.ProtocolMessage;

import java.util.*;

/**
 * 拦截器执行链条
 * @Author: wonderzh
 * @Date: 2020/9/9
 * @Version: 1.0
 */

public class InterceptorExecuteChain {


    private List<ChannelInterceptor> interceptorList;


    public InterceptorExecuteChain addInterceptor(InterceptorExecuteChain chain) {
        if (chain != null&&chain.hasInterceptor()) {
            getInterceptors().addAll(chain.interceptorList);
        }
        return this;
    }

    public InterceptorExecuteChain addInterceptor(List<? extends ChannelInterceptor> interceptors) {
        if (interceptors != null&&interceptors.size()>0) {
            getInterceptors().addAll(interceptors);
        }
        return this;
    }

    public InterceptorExecuteChain addInterceptor(ChannelInterceptor... interceptors) {
        if (interceptors != null&&interceptors.length>0) {
            getInterceptors().addAll(Arrays.asList(interceptors));
        }
        return this;
    }
    /**
     * 增加处理节点
     * @param interceptor
     * @return
     */
    public InterceptorExecuteChain addInterceptor(ChannelInterceptor interceptor) {
        if (interceptor != null) {
            getInterceptors().add(interceptor);
        }
        return this;
    }

    /**
     * 执行拦截器前置方法
     *
     * @param msg
     * @param attributes
     * @return 任意一个拦截器返回false  该方法返回false
     */
    public boolean applyPreHandle(ProtocolMessage msg, Map<String,Object> attributes) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        if (this.interceptorList.size() > 0) {
            for (ChannelInterceptor interceptor : interceptorList) {
                if (!interceptor.preHandle(msg, attributes)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 执行拦截器后置方法
     * @param msg
     * @param attributes
     */
    public void applyAfterHandle(ProtocolMessage msg, Map<String,Object> attributes) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        if (this.interceptorList.size() > 0) {
            for (int i = interceptorList.size() - 1; i >= 0; i--) {
                interceptorList.get(i).afterHandle(msg,attributes);
            }
        }
    }

    private List<ChannelInterceptor> getInterceptors() {
        if (interceptorList == null) {
            interceptorList = new ArrayList<>();
        }
        return interceptorList;
    }

    public boolean hasInterceptor() {
        return interceptorList != null && interceptorList.size() > 0;
    }
}
