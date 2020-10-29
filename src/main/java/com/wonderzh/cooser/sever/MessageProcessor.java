package com.wonderzh.cooser.sever;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.wonderzh.cooser.common.constarnt.StatusCode;
import com.wonderzh.cooser.disruptor.EventConsumer;
import com.wonderzh.cooser.disruptor.EventContext;
import com.wonderzh.cooser.exception.EventHandlerExecuteException;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import com.wonderzh.cooser.protocol.Response;
import com.wonderzh.cooser.sever.handler.EventHandlerDelegate;
import com.wonderzh.cooser.tool.ProtoFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Cooser 分发路由器
 * Disruptor 固定数量多实例消费者
 *
 * @Author: wonderzh
 * @Date: 2020/7/8
 * @Version: 1.0
 */
@Slf4j
public class MessageProcessor extends EventConsumer {

    private static Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    private static FrameDispatcher dispatcher;

    private ConfigurableServerContext serverContext;

    public MessageProcessor(ConfigurableServerContext serverContext) {
        this.serverContext = serverContext;
        dispatcher = serverContext.getFrameDispatcher();
    }

    /**
     * 分发请求到对应EventHandler
     * 统一全局异常处理
     * 包装返回Proto响应
     * web 请求-响应模式，每次请求都有一次响应
     *
     * @param event
     * @throws Exception
     */
    @Override
    public void onEvent(EventContext event) {
        if (!event.hasInitialized()) {
            return;
        }
        Long uuid = event.getProtocolMessage().getUuid();
        Message response = null;
        Throwable error = null;
        try {
            response = dispatchEvent(event);
        } catch (EventHandlerExecuteException e) {
            logger.error("EventHandler for {} execute exception：", event.getPath(), e);
            error = e;
        } catch (Throwable th) {
            logger.error("EventHandler for {} execute error：", event.getPath(), th);
            error = th;
        }

        ProtocolMessage protocolPattern = serverContext.getProtocolPattern();
        Response responseProto = null;

        if (error == null) {
            //响应Response
            if (response instanceof Response) {
                responseProto = (Response) response;
            } else {
                //响应 ? extends Message
                responseProto = ProtoFactory.generateResponse(protocolPattern, StatusCode.OK.code(), StatusCode.OK.message());
                responseProto.setData(Any.pack(response));
            }
        } else {
            //异常响应
            responseProto = ProtoFactory.generateResponse(protocolPattern,
                    StatusCode.RESPONSE_ERROR.code(), error.getMessage());
        }

        ProtocolMessage protocol = ProtoFactory.generateResponseProtocol(protocolPattern,
                uuid, responseProto);
        event.getCtx().channel().writeAndFlush(protocol);
    }

    /**
     * 调度handler业务逻辑处理单元
     * EventContext作为默认形参传入handler 被调用的方法
     * @param event
     * @return ? extends Message or Response
     * @throws EventHandlerExecuteException
     */
    private Message dispatchEvent(EventContext event) throws EventHandlerExecuteException {
        EventHandlerDelegate handlerDe = dispatcher.getEventHandlerProxy(event.getPath());
        if (handlerDe == null) {
            throw new EventHandlerExecuteException("no handler mapping for " + event.getPath());
        }
        //调用handler
        Object result = handlerDe.doInvoke(event,event.getData());
        //默认null返回ack响应
        if (result == null) {
            ProtocolMessage protocolPattern = serverContext.getProtocolPattern();
            result = ProtoFactory.generateResponse(protocolPattern, StatusCode.OK.code(), StatusCode.OK.message());
            Response responseProto = (Response) result;
            responseProto.setData(Any.pack(responseProto.getAck()));
            return responseProto;
        }
        //返回? extends Message
        if (result instanceof Message) {
            return (Message) result;
        } else {
            throw new EventHandlerExecuteException("message convert exception for " + result.getClass().getName());
        }
    }


    @Override
    public String getId() {
        return UUID.randomUUID().toString();
    }

}
