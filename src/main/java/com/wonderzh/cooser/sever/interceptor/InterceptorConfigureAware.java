package com.wonderzh.cooser.sever.interceptor;

import com.wonderzh.cooser.sever.factory.ComponentDefinition;

import java.util.List;

/**
 * Filter 自动装配器
 * @Author: wonderzh
 * @Date: 2020/9/9
 * @Version: 1.0
 */

public class InterceptorConfigureAware {


    public InterceptorMapping registerChannelInterceptor(ComponentDefinition interceptorConfigurer) {
        Object instance = interceptorConfigurer.getInstance();
        if (instance instanceof InterceptorConfigurer) {
            InterceptorConfigurer configurer = (InterceptorConfigurer) instance;
            InterceptorRegistry registry = new InterceptorRegistry();
            configurer.addInterceptors(registry);
            return createMapping(registry);
        }
        return null;
    }

    private InterceptorMapping createMapping(InterceptorRegistry registry) {
        List<ChannelInterceptorHolder> holders = registry.getInterceptors();
        InterceptorMapping mapping = new InterceptorMapping();
        mapping.addMappedChannelInterceptor(holders);
        return mapping;
    }


}
