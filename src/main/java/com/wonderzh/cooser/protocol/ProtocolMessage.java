package com.wonderzh.cooser.protocol;


import com.google.protobuf.Message;

/**
 * Cooser 协议模板
 * 继承Google Protobuf MessageLite
 *
 * @Author: wonderzh
 * @Date: 2020/7/8
 * @Version: 1.0
 */

public interface ProtocolMessage extends Message {


    /**
     * 默认协议体
     */
    ProtocolMessage DEFAULT_PROTOCOL = MessageProto.Message.getDefaultInstance();

    /**
     * 请求行：协议模式
     * @return
     */
    String getMode();

    /**
     * 设置请求行：协议模式
     */
    void setMode(String mode);

    /**
     * 请求行：协议串
     * @return
     */
    String getProtocol();

    /**
     * 设置请求头：协议串
     * @param str
     */
    void setProtocol(String str);

    /**
     * 请求头：全局id
     * @return
     */
    long getUuid();

    /**
     * 设置请求头：全局id
     *
     */
    void setUuid(long id);

    /**
     * 请求头：授权身份
     * @return
     */
    String getAuthorization();

    /**
     * 设置请求头：授权身份
     * @return
     */
    void setAuthorization(String authorization);

    /**
     * 协议实例
     * @return
     */
    ProtocolMessage newInstance();

    /**
     * 请求体
     * @return
     */
    Object getBody();

    /**
     * 设置响应体实例
     */
    void setResponse(Response response);

    /**
     * 设置Josn实体
     * @param jsonBody
     */
    void setJsonBody(GenericProto.JsonObj jsonBody);

    /**
     * 默认响应体实例
     * @return Response
     */
    Response newResponseInstance();
}
