package com.wonderzh.cooser.tool;

import com.google.protobuf.Any;
import com.wonderzh.cooser.common.constarnt.StatusCode;
import com.wonderzh.cooser.protocol.GenericProto;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import com.wonderzh.cooser.protocol.ProtocolUrl;
import com.wonderzh.cooser.protocol.Response;

/**
 * 协议创建工厂类
 * @Author: wonderzh
 * @Date: 2020/8/18
 * @Version: 1.0
 */

public class ProtoFactory {

    private static MsgUuidGenerator uuidFactory = MsgUuidGenerator.instance();

    public static void configUuidGenerator(int workerId) {
        uuidFactory = new MsgUuidGenerator(workerId);
    }

    /**
     * 创建身份认证协议报文
     * @param protocolPattern
     * @param uuid
     * @param id
     * @return
     */
    public static ProtocolMessage createIdentityProof(ProtocolMessage protocolPattern, String id) {
        ProtocolMessage protocol= protocolPattern.newInstance();
        protocol.setMode(ProtocolUrl.SIGN_MODE);
        protocol.setUuid(uuidFactory.nextId());
        protocol.setAuthorization(id);
        return protocol;
    }

    /**
     * 创建Json载体的请求报文
     * @param protocolPattern
     * @param jsonBody
     */
    public static ProtocolMessage generateJsonRequestProtocol(ProtocolMessage protocolPattern,String path,String jsonBody) {
        if (protocolPattern==null||ObjectUtil.isBlank(jsonBody)||ObjectUtil.isBlank(path)) {
            throw new NullPointerException();
        }
        ProtocolMessage protocol=protocolPattern;
        protocol.setMode(ProtocolUrl.REQUEST_MODE);
        protocol.setUuid(uuidFactory.nextId());
        protocol.setProtocol(path);
        GenericProto.JsonObj.Builder builder = GenericProto.JsonObj.newBuilder();
        builder.setBody(jsonBody);
        protocol.setJsonBody(builder.build());

        return protocol;
    }

    /**
     * 服务端使用
     * 创建ACK响应报文
     * @param protocolPattern 服务器内置协议模板
     * @param uuid
     * @return
     */
    public static ProtocolMessage generateACKResponseProtocol(ProtocolMessage protocolPattern, long uuid) {
        ProtocolMessage protocol= protocolPattern.newInstance();
        protocol.setMode(ProtocolUrl.RESPONSE_MODE);
        protocol.setUuid(uuid);
        Response response=protocolPattern.newResponseInstance();
        response.setStatus(StatusCode.OK.code());
        response.setMessage(StatusCode.OK.message());
        response.setData(Any.pack(response.getAck()));
        protocol.setResponse(response);
        return protocol;
    }

    /**
     * 服务端使用
     * 创建通用响应报文
     * @param protocolPattern 服务器内置协议模板
     * @param uuid
     * @param response
     * @return
     */
    public static ProtocolMessage generateResponseProtocol(ProtocolMessage protocolPattern, long uuid, Response response) {
        ProtocolMessage protocol= protocolPattern.newInstance();
        protocol.setMode(ProtocolUrl.RESPONSE_MODE);
        protocol.setUuid(uuid);
        protocol.setResponse(response);
        return protocol;
    }

    /**
     * 服务端使用
     * 创建无消息体响应报文
     * @param protocolPattern 服务器内置协议模板
     * @param code
     * @param message
     * @return
     */
    public static Response generateResponse(ProtocolMessage protocolPattern, int code, String message) {
        return generateResponse(protocolPattern,code,message,null);
    }

    /**
     * 服务端使用
     * 创建通用响应报文
     * @param protocolPattern 服务器内置协议模板
     * @param code
     * @param message
     * @param data
     * @return
     */
    public static Response generateResponse(ProtocolMessage protocolPattern, int code, String message, Any data) {
        Response response = protocolPattern.newResponseInstance();
        response.setStatus(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * 服务端使用
     * 创建异常响应报文
     * @param protocolPattern 服务器内置协议模板
     * @param uuid
     * @param e
     * @return
     */
    public static ProtocolMessage generateErrorResponseProtocol(ProtocolMessage protocolPattern, long uuid, Exception e) {
        ProtocolMessage protocol = protocolPattern.newInstance();
        Response response = protocol.newResponseInstance();
        response.setStatus(StatusCode.RESPONSE_ERROR.code());
        response.setMessage(e.getMessage());
        protocol.setUuid(uuid);
        protocol.setProtocol(ProtocolUrl.PATH_DEFAULT_RESPONSE);
        protocol.setResponse(response);
        protocol.setMode(ProtocolUrl.RESPONSE_MODE);
        return protocol;
    }

    /**
     * 服务端使用
     * 创建异常响应报文
     * @param protocolPattern 服务器内置协议模板
     * @param uuid
     * @param status
     * @return
     */
    public static ProtocolMessage generateErrorResponseProtocol(ProtocolMessage protocolPattern, long uuid,StatusCode status) {
        ProtocolMessage protocol = protocolPattern.newInstance();
        Response response = protocol.newResponseInstance();
        response.setStatus(status.code());
        response.setMessage(status.message());
        protocol.setUuid(uuid);
        protocol.setProtocol(ProtocolUrl.PATH_DEFAULT_RESPONSE);
        protocol.setResponse(response);
        protocol.setMode(ProtocolUrl.RESPONSE_MODE);
        return protocol;
    }


}
