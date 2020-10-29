package com.wonderzh.cooser.sever;

import com.wonderzh.cooser.CooserConfiguration;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import com.wonderzh.cooser.sever.factory.ComponentFactory;
import com.wonderzh.cooser.tool.ObjectUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 服务器上下文基础容器
 *
 * @Author: wonderzh
 * @Date: 2020/9/8
 * @Version: 1.0
 */

public class GenericServerContext implements ConfigurableServerContext {

    /**
     * spring ioc
     */
    private ApplicationContext springContext;
    /**
     * 服务调度器
     */
    private FrameDispatcher frameDispatcher;
    /**
     * ChannelHandlerContext容器，  key：probeId
     */
    private ConcurrentHashMap<String, ChannelHandlerContext> channelContainer = new ConcurrentHashMap<>(64);
    /**
     * 服务器配置项
     */
    private CooserConfiguration configuration;
    /**
     * 传输协议模板
     */
    private ProtocolMessage protocolPattern;

    private List<ServerContextAware> contextAwares;

    /**
     * 初始化服务上下文
     * 初始化框架组件
     *
     * @param componentScan
     */
    @Override
    public synchronized void initialize(String componentScan) {
        prepareConfigurer();
        ComponentFactory.instance().loadComponent(componentScan, this);
        doFinish();
    }

    /**
     *  context初始化前置准备
     */
    private void prepareConfigurer() {
        if (frameDispatcher == null) {
            this.frameDispatcher = new GenericFrameDispatcher(this);
        }
    }

    /**
     * context初始化完成后触发事件
     */
    private void doFinish() {
        //serverContext的持有类注入context
        if (!ObjectUtil.isEmpty(this.contextAwares)) {
            for (ServerContextAware contextAware : this.contextAwares) {
                contextAware.setServerContext(this);
            }
            contextAwares.clear();
        }
    }

    /**
     * 融合Spring IOC
     *
     * @param applicationContext
     */
    @Override
    public void registerSpringContext(ApplicationContext applicationContext) {
        this.springContext = applicationContext;
    }

    @Override
    public ApplicationContext getSpringContext() {
        return this.springContext;
    }

    /**
     * 销毁资源
     */
    @Override
    public synchronized void destroy() {
        frameDispatcher.destroy();
    }

    /**
     * 保存通道上下文
     * 探针网络不稳定，重连后可重复注册，即更新 ChannelHandlerContext
     *
     * @param id
     * @param ctx
     */
    @Override
    public boolean signChannel(String id, ChannelHandlerContext ctx) {
        if (!ObjectUtil.isBlank(id) && !ObjectUtil.isNull(ctx)) {
            channelContainer.put(id, ctx);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取通道上下文
     * @param id
     */
    @Override
    public ChannelHandlerContext getChannel(String id) {
        if (ObjectUtil.isBlank(id)) {
            return null;
        }
        return channelContainer.get(id);
    }

    /**
     * 获取所有通道上下文
     * 浅拷贝
     * @return
     */
    @Override
    public Map<String, ChannelHandlerContext> getAllChannel() {
        Map<String, ChannelHandlerContext> map = new HashMap<>();
        for (Map.Entry<String, ChannelHandlerContext> entry : channelContainer.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    @Override
    public void releaseChannel(String id) {
        if (!ObjectUtil.isBlank(id)) {
            channelContainer.remove(id);
        }
    }

    @Override
    public void registerServerContextAware(List<ServerContextAware> serverContextAwares) {
        this.contextAwares = serverContextAwares;
    }

    @Override
    public void configuration(CooserConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public FrameDispatcher getFrameDispatcher() {
        return frameDispatcher;
    }

    @Override
    public CooserConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setProtocolPattern(ProtocolMessage messageLite) {
        this.protocolPattern = messageLite;
    }

    @Override
    public ProtocolMessage getProtocolPattern() {
        return protocolPattern;
    }


}
