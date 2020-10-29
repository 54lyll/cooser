package com.wonderzh.cooser.sever;

import com.wonderzh.cooser.common.constarnt.StatusCode;
import com.wonderzh.cooser.disruptor.EventProducer;
import com.wonderzh.cooser.disruptor.RingWorkerPoolFactory;
import com.wonderzh.cooser.exception.IllegalProtocolUrlException;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import com.wonderzh.cooser.protocol.ProtocolUrl;
import com.wonderzh.cooser.sever.interceptor.InterceptorExecuteChain;
import com.wonderzh.cooser.tool.ObjectUtil;
import com.wonderzh.cooser.tool.ProtoConverter;
import com.wonderzh.cooser.tool.ProtoFactory;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 报文在netty channel阶段双向处理器
 * 消息预处理并生产队列数据
 *
 * @Author: wonderzh
 * @Date: 2020/6/2
 * @Version: 1.0
 */
@Slf4j
public class MessageDuplexHandler extends ChannelDuplexHandler {

    private ConfigurableServerContext serverContext;

    private FrameDispatcher dispatcher;
    /**
     * 全局匿名身份
     */
    private static AtomicLong count = new AtomicLong(0);
    /**
     * 是否匿名者
     */
    private boolean isAnonymity = true;
    private boolean serverEnableAnonymity;
    /**
     * 通道身份
     */
    private String identity="none";
    //TODO : 缓存解决内存泄漏
    private HashMap<Long, InterceptorExecuteChain> chainCache = new HashMap<>(8);


    public MessageDuplexHandler(ConfigurableServerContext serverContext) {
        this.serverContext = serverContext;
        this.dispatcher = serverContext.getFrameDispatcher();
        this.serverEnableAnonymity = this.serverContext.getConfiguration().isAnonymousEnable();
    }

    /**
     * 过滤心跳，执行前置拦截器，向Disruptor生产消息
     *  客户端建立后应立即签名身份，未签名通道为匿名访问
     *  若服务器不允许匿名访问，将关闭连接
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  {
        long uuid = 0;
        ProtocolMessage protocol = null;
        try {
            protocol = (ProtocolMessage) msg;
            uuid = protocol.getUuid();
            //过滤心跳
            if (ProtocolUrl.isHeart(protocol.getMode())) {
                return;
            }
            //签名,身份验证
            if (ProtocolUrl.isSign(protocol.getMode())) {
                signChannelIdentity(ctx, protocol);
                return;
            }
            //拦截匿名访问
            if (this.isAnonymity &&!this.serverEnableAnonymity) {
                ctx.writeAndFlush(ProtoFactory.generateErrorResponseProtocol(serverContext.getProtocolPattern(), protocol.getUuid(), StatusCode.REFUSE_ANONYMOUS_ACCESS));
                ctx.channel().close();
                log.warn("关闭匿名访问连接{}",ctx.channel().remoteAddress().toString());
                return;
            }
            //前置拦截器
            InterceptorExecuteChain chain=dispatcher.getInterceptorChain(protocol.getProtocol());
            if (chain.hasInterceptor()) {
                chainCache.put(uuid, chain);
                if (!chain.applyPreHandle(protocol, new HashMap<>())) {
                    return;
                }
            }
            //推送至队列
            pushMessage(ctx, protocol);

        } catch (Exception e) {
            ctx.writeAndFlush(ProtoFactory.generateErrorResponseProtocol(protocol, uuid, e));
            throw e;
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    /***
     * 签署长连接身份
     *  若不允许匿名访问，进行身份验证，失败关闭连接
     *  可重复注册
     * @param ctx
     * @param protocol
     */
    private void signChannelIdentity(ChannelHandlerContext ctx, ProtocolMessage protocol) {
        String id =ProtoConverter.getStringValue(protocol.getAuthorization()) ;
        if (ObjectUtil.isBlank(id )) {
            id = count.incrementAndGet() + "";
        }
        //重复注册不同的身份，先清理之前缓存
        if (this.identity != null&&!this.identity.equals(id)) {
            this.serverContext.releaseChannel(identity);
        }
        this.identity = id;

        if (!serverContext.getConfiguration().isAnonymousEnable()) {
            if (dispatcher.doAuthenticate(this.identity)) {
                //身份验证通过，存储通道
                this.serverContext.signChannel(this.identity, ctx);
                this.isAnonymity = false;
                ctx.writeAndFlush(ProtoFactory.generateACKResponseProtocol(serverContext.getProtocolPattern(), protocol.getUuid()));
            } else {
                //身份验证失败，关闭连接
                ctx.writeAndFlush(ProtoFactory.generateErrorResponseProtocol(serverContext.getProtocolPattern(), protocol.getUuid(), StatusCode.ILLEGAL_IDENTITY));
                ctx.channel().close();
            }
        } else {
            //允许匿名访问，直接存储通道
            this.serverContext.signChannel(this.identity, ctx);
            ctx.writeAndFlush(ProtoFactory.generateACKResponseProtocol(serverContext.getProtocolPattern(), protocol.getUuid()));
        }
    }

    /**
     * 将消息推送至 disruptor 队列
     * @param ctx
     * @param protocol
     * @throws IllegalProtocolUrlException
     */
    private void pushMessage(ChannelHandlerContext ctx, ProtocolMessage protocol) {
        //int count = random.nextInt(100);
        //String producerId = PRODUCER_PREFIX + count % size;
        //MessageProducer<Object> messageProducer = RingWorkerPoolFactory.instance().producer(producerId);

        EventProducer eventProducer = RingWorkerPoolFactory.instance().defaultProducer();
        eventProducer.onMessage(this.identity,ctx, protocol);
    }

    /**
     * 执行后置拦截器
     * @param ctx
     * @param msg
     * @param promise
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //后置拦截器
        ProtocolMessage protocol = (ProtocolMessage) msg;
        InterceptorExecuteChain chain = chainCache.get(protocol.getUuid());
        if (chain != null) {
            chain.applyAfterHandle(protocol, new HashMap<>());
            chainCache.remove(protocol.getUuid());
        }
        super.write(ctx, msg, promise);
    }

    /**
     * 捕获Channel异常
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server caught an channel exception:", cause);
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            ChannelEventPublisher eventPublisher=dispatcher.getChannelEventPublisher();
            if (eventPublisher != null) {
                eventPublisher.activeEvent(ctx);
            }
        } catch (Exception e) {
            log.error("ChannelEventPublisher execute active event exception",e);
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            ChannelEventPublisher eventPublisher=dispatcher.getChannelEventPublisher();
            if (eventPublisher != null) {
                eventPublisher.inactiveEvent(new ChannelHandlerContextHolder(this.identity,ctx));
            }
        } catch (Exception e) {
            log.error("ChannelEventPublisher execute inactive event exception",e);
        }
        if (this.identity != null) {
            this.serverContext.releaseChannel(this.identity);
        }
        this.chainCache.clear();
        this.chainCache = null;
        this.serverContext = null;
        this.dispatcher = null;
        super.channelInactive(ctx);
    }

}
