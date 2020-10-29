package com.wonderzh.cooser.sever.factory;

import com.wonderzh.cooser.exception.ComponentBeanInitializeException;
import com.wonderzh.cooser.sever.Authenticator;
import com.wonderzh.cooser.sever.ChannelLifecycleListener;
import com.wonderzh.cooser.sever.ConfigurableServerContext;
import com.wonderzh.cooser.sever.ServerContextAware;
import com.wonderzh.cooser.sever.handler.MessageHandler;
import com.wonderzh.cooser.sever.interceptor.InterceptorConfigurer;
import com.wonderzh.cooser.tool.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Cooser 服务器装配工厂，负责实例化与注册组件
 * 注册@EventHandler{@link MessageHandler},
 * 注册拦截器{@link InterceptorConfigurer},
 * 注册身份验证器{@link Authenticator},
 * 注册通道生命周期侦听类{@link ChannelLifecycleListener},
 * 注册serverContext的侦听类{@link ServerContextAware},
 *
 * @Author: wonderzh
 * @Date: 2020/7/8
 * @Version: 1.0
 */
public class ComponentFactory implements InstanceResource {

    private static final Logger logger = LoggerFactory.getLogger(ComponentFactory.class);

    private ConfigurableServerContext context;

    private ComponentFactory() {
    }

    private static class InnerSingleton {
        private static ComponentFactory instance = new ComponentFactory();
    }

    public static ComponentFactory instance() {
        return InnerSingleton.instance;
    }

    /**
     * 装配Cooser框架组件
     *
     * @param packagePath
     * @param serverContext
     */
    public synchronized void loadComponent(String packagePath, ConfigurableServerContext serverContext) {
        this.context = serverContext;
        //加载组件
        ComponentDefinitionLoader cdLoader = createComponentLoader();
        cdLoader.load(packagePath);

        //初始化@EventHandler
        doInitEventHandler(cdLoader.getTypesAnnotatedWith(MessageHandler.class));

        //初始化默认组件
        doInitDefaultComponent(cdLoader.getTypesAnnotatedWith(CooserComponent.class));
    }

    /**
     * 创建组件加载器
     * serverContext的持有类需最后注册
     * @return
     */
    private static ComponentDefinitionLoader createComponentLoader() {
        return new ClassPathAnnotationScanner();
    }

    /**
     *初始化默认组件
     * @param componentTypes
     */
    private void doInitDefaultComponent(Set<Class<?>> componentTypes) {

        List<ComponentDefinition> definitions = doInstantiate(componentTypes);

        List<ServerContextAware> serverContextAwares = new ArrayList<>();
        List<ChannelLifecycleListener> channelLifecycleListeners = new ArrayList<>();
        for (ComponentDefinition definition : definitions) {
            //注册拦截器
            if (definition.getInstance() instanceof InterceptorConfigurer) {
                registerChannelInterceptor(definition);
            } else if (definition.getInstance() instanceof Authenticator) {
                //注册身份验证器
                registerAuthenticator(definition);
            } else if (definition.getInstance() instanceof ChannelLifecycleListener) {
                channelLifecycleListeners.add((ChannelLifecycleListener) definition.getInstance());
            } else if (definition.getInstance() instanceof ServerContextAware) {
                serverContextAwares.add((ServerContextAware) definition.getInstance());
            }
        }

        //注册通道生命周期监听器
        if (channelLifecycleListeners.size() > 0) {
            registerChannelLifecycle(channelLifecycleListeners);
        }

        //注册serverContext的持有类
        if (serverContextAwares.size() > 0) {
            registerServerContextAware(serverContextAwares);
        }

    }

    /**
     * 注册通道生命周期监听器
     * @param channelLifecycleListeners
     */
    private void registerChannelLifecycle(List<ChannelLifecycleListener> channelLifecycleListeners) {
        context.getFrameDispatcher().registerChannelLifecycle(channelLifecycleListeners);
    }

    /**
     * 注册serverContext的持有类
     * @param serverContextAwares
     */
    private void registerServerContextAware(List<ServerContextAware> serverContextAwares) {
        context.registerServerContextAware(serverContextAwares);
    }

    /**
     * 向FrameDispatcher注册身份验证器
     * @param definition
     */
    private void registerAuthenticator(ComponentDefinition definition) {
        context.getFrameDispatcher().registerAuthenticator(definition);
    }

    /**
     * 向FrameDispatcher注册拦截器
     * @param interceptorConfigurer
     */
    private void registerChannelInterceptor(ComponentDefinition interceptorConfigurer) {
        context.getFrameDispatcher().registerChannelInterceptor(interceptorConfigurer);
    }

    /**
     * 初始化网络事件处理单元 EventHandler组件
     *
     * @param handlerTypes
     */
    private void doInitEventHandler(Set<Class<?>> handlerTypes) {
        List<ComponentDefinition> definitions = doInstantiate(handlerTypes);
        context.getFrameDispatcher().registerEventHandler(definitions);
    }

    /**
     * 实例化组件
     * @param classes
     * @return
     */
    private List<ComponentDefinition> doInstantiate(Set<Class<?>> classes) {
        List<ComponentDefinition> objects = new ArrayList<>();
        if (classes != null && classes.size() >0) {
            for (Class<?> clazz : classes) {
                BasicComponentDefinition definition = new BasicComponentDefinition();
                definition.setInstance(getInstance(clazz));
                objects.add(definition);
            }
        }
        return objects;
    }

    /**
     * 优先从Spring IOC中获取实例，
     * 没有进行反射实例化
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T getInstance(Class<T> clazz) {
        Object obj = null;
        if (context != null) {
            ApplicationContext springContext = context.getSpringContext();
            if (springContext != null) {
                if (springContext.containsBean(ObjectUtil.getBeanName(clazz))) {
                    obj = springContext.getBean(clazz);
                }
            }
        }
        if (obj == null) {
            try {
                return clazz.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new ComponentBeanInitializeException(clazz.getName() + "construct error", e);
            }
        }
        return (T) obj;
    }
}

