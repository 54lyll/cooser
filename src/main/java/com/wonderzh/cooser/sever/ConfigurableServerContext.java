package com.wonderzh.cooser.sever;

import com.wonderzh.cooser.CooserConfiguration;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * 扩展CooserContext 提供各种配置接口
 * @Author: wonderzh
 * @Date: 2020/9/8
 * @Version: 1.0
 */

public interface ConfigurableServerContext extends CooserContext{

    /**
     * 初始化
     * @param componentScan
     */
    void initialize(String componentScan);
    /**
     * 融合Spring IOC
     * @param applicationContext
     */
    void registerSpringContext(ApplicationContext applicationContext);

    /**
     * 获取调度器
     * @return
     */
    FrameDispatcher getFrameDispatcher();

    /**
     * 服务器关闭，注销容器中的组件
     */
    void destroy();

    /**
     * 保存通道上下文
     * @param id
     * @param ctx
     */
    boolean signChannel(String id, ChannelHandlerContext ctx);

    /**
     * 释放通道上下文
     * @param identity
     */
    void releaseChannel(String identity);

    /**
     * 配置文件
     * @param configuration
     */
    void configuration(CooserConfiguration configuration);

    /**
     * 协议模板
     * @param messageLite
     */
    void setProtocolPattern(ProtocolMessage messageLite);

    /**
     * 注册serverContext的持有类
     * @param serverContextAwares
     */
    void registerServerContextAware(List<ServerContextAware> serverContextAwares);
}
