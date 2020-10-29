package com.wonderzh.cooser.protocol;

import com.google.protobuf.Message;

/**
 * @Author: wonderzh
 * @Date: 2020/7/9
 * @Version: 1.0
 */

public interface Response extends Message {

    int getStatus();

    void setStatus(int status);

    void setMessage(String message);

    String getMessage();

    com.google.protobuf.Any getData();

    void setData(com.google.protobuf.Any data);

    ResponseProto.Ack getAck();

}
