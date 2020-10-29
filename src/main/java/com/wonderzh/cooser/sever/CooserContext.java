package com.wonderzh.cooser.sever;

import com.wonderzh.cooser.CooserConfiguration;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Cooser服务器上下文
 * @Author: wonderzh
 * @Date: 2020/10/27
 * @Version: 1.0
 */

public interface CooserContext {


    /**
     *获取Spring 上下文
     * @return
     */
    ApplicationContext getSpringContext();

    /**
     * 获取通道上下文
     * @param id
     */
    ChannelHandlerContext getChannel(String id);

    /**
     * 获取所有通道上下文
     * @return
     */
    Map<String, ChannelHandlerContext> getAllChannel();

    /**
     * 获取服务器配置项
     * @return
     */
    CooserConfiguration getConfiguration();

    /**
     * 获取服务器使用的模板协议
     * @return
     */
    ProtocolMessage getProtocolPattern();


}
