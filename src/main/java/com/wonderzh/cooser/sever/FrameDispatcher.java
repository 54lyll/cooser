package com.wonderzh.cooser.sever;

import com.wonderzh.cooser.sever.factory.ComponentDefinition;
import com.wonderzh.cooser.sever.handler.EventHandlerDelegate;
import com.wonderzh.cooser.sever.interceptor.InterceptorExecuteChain;

import java.util.List;

/**
 * 框架调度器
 *
 * @Author: wonderzh
 * @Date: 2020/7/8
 * @Version: 1.0
 */

public interface FrameDispatcher {

    /**
     * 注册拦截器
     * 一个拦截器配置类定义一组拦截器
     *
     * @param interceptorConfigurer 拦截器配置类
     */
    void registerChannelInterceptor(ComponentDefinition interceptorConfigurer);

    /**
     * 获取拦截器执行链
     * 如果开发者实现多个拦截器配置类，每组间执行顺序不定，组内拦截器执行顺序一致
     *
     * @param protocol
     * @return
     */
    InterceptorExecuteChain getInterceptorChain(String protocol);

    /**
     * 注册网络事件处理单元
     * @param definitions
     */
    void registerEventHandler(List<ComponentDefinition> definitions);

    /**
     * 获取网络事件处理handler代理
     * @param protoPath
     * @return
     */
    EventHandlerDelegate getEventHandlerProxy(String protoPath);

    /**
     * 清理资源
     */
    void destroy();

    /**
     * 身份验证
     * anonymous_enable = false 时进行身份验证
     * @param identity
     * @return
     */
    boolean doAuthenticate(String identity);

    /**
     * 注册身份验证器
     * @param definition
     */
    void registerAuthenticator(ComponentDefinition definition);

    /**
     * 注册通道生命周期监听器
     * @param channelLifecycleListeners
     */
    void registerChannelLifecycle(List<ChannelLifecycleListener> channelLifecycleListeners);

    /**
     * 获取通道事件发布器
     * @return null able
     */
    ChannelEventPublisher getChannelEventPublisher();
}
