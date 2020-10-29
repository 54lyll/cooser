package com.wonderzh.cooser.sever;

import com.wonderzh.cooser.exception.ComponentBeanInitializeException;
import com.wonderzh.cooser.sever.factory.*;
import com.wonderzh.cooser.sever.handler.EventHandlerConfigureAware;
import com.wonderzh.cooser.sever.handler.EventHandlerDelegate;
import com.wonderzh.cooser.sever.handler.EventHandlerMapping;
import com.wonderzh.cooser.sever.interceptor.InterceptorConfigureAware;
import com.wonderzh.cooser.sever.interceptor.InterceptorExecuteChain;
import com.wonderzh.cooser.sever.interceptor.InterceptorMapping;
import com.wonderzh.cooser.sever.interceptor.PathMatchingInterceptor;
import com.wonderzh.cooser.tool.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础框架调度器
 * 中介者模式
 *
 * @Author: wonderzh
 * @Date: 2020/9/10
 * @Version: 1.0
 */
@Slf4j
public class GenericFrameDispatcher implements FrameDispatcher {

    /**
     * 应用上下文
     */
    private ConfigurableServerContext serverContext;

    /**
     * 拦截器映射
     */
    private List<InterceptorMapping> interceptorMappings = new ArrayList<>();

    /**
     * 处理器映射
     */
    private EventHandlerMapping handlerMapping;

    /**
     * 身份验证器
     */
    private Authenticator authenticator;

    /**
     * 通道生命周期监听器
     */
    private ChannelEventPublisher eventPublisher;

    public GenericFrameDispatcher(GenericServerContext serverContext) {
        this.serverContext = serverContext;
    }

    /**
     * 注册拦截器
     *
     * @param interceptorConfigurer 拦截器配置类
     */
    @Override
    public void registerChannelInterceptor(ComponentDefinition interceptorConfigurer) {
        InterceptorConfigureAware ica = new InterceptorConfigureAware();
        InterceptorMapping mapping = ica.registerChannelInterceptor(interceptorConfigurer);
        interceptorMappings.add(mapping);
    }

    /**
     * 获取拦截器执行链
     *
     * @param path
     * @return
     */
    @Override
    public InterceptorExecuteChain getInterceptorChain(String path) {
        InterceptorExecuteChain chain = new InterceptorExecuteChain();
        for (InterceptorMapping interceptorMapping : interceptorMappings) {
            List<PathMatchingInterceptor> matched = interceptorMapping.getInterceptors(path);
            if (!ObjectUtil.isEmpty(matched)) {
                chain.addInterceptor(matched);
            }
        }
        return chain;
    }

    /**
     * 注册网络事件处理单元
     * @param definitions
     */
    @Override
    public void registerEventHandler(List<ComponentDefinition> definitions) {
        EventHandlerConfigureAware aware = new EventHandlerConfigureAware();
        this.handlerMapping=aware.registerEventHandler(definitions);
    }

    /**
     * 获取网络事件处理handler代理
     * @param path
     * @return
     */
    @Override
    public EventHandlerDelegate getEventHandlerProxy(String path) {
        return handlerMapping.getHandlerDelegate(path);
    }


    /**
     * 身份验证
     * @param identity
     * @return
     */
    @Override
    public boolean doAuthenticate(String identity) {
        if (authenticator == null) {
            throw new ComponentBeanInitializeException("please register authenticator component");
        }
        boolean isPermitted = false;
        try {
            isPermitted = this.authenticator.isPermitted(identity);
        } catch (Exception ignore) {
            log.error("authenticator exception: ",ignore);
        }
        return isPermitted;
    }

    /**
     * 注册身份验证器
     * @param definition
     */
    @Override
    public void registerAuthenticator(ComponentDefinition definition) {
        if (this.authenticator != null) {
            throw new ComponentBeanInitializeException("there are multiple authenticators");
        }
        this.authenticator = (Authenticator) definition.getInstance();
    }

    /**
     * 注册通道生命周期监听器
     * @param channelLifecycleListeners
     */
    @Override
    public void registerChannelLifecycle(List<ChannelLifecycleListener> channelLifecycleListeners) {
        if (this.eventPublisher == null) {
            this.eventPublisher = new ChannelEventPublisher();
        }
        this.eventPublisher.addListeners(channelLifecycleListeners);
    }

    /**
     * 获取通道事件发布器
     * @return null able
     */
    @Override
    public ChannelEventPublisher getChannelEventPublisher() {
        return this.eventPublisher;
    }

    /**
     * 销毁
     */
    @Override
    public void destroy() {
        serverContext = null;
        if (!ObjectUtil.isEmpty(interceptorMappings)) {
            for (InterceptorMapping interceptorMapping : interceptorMappings) {
                interceptorMapping.clear();
            }
            interceptorMappings.clear();
            interceptorMappings = null;
        }
        handlerMapping.clear();
        handlerMapping = null;
    }

}
