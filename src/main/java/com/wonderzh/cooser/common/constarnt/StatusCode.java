package com.wonderzh.cooser.common.constarnt;


/**
 * 状态码
 *
 * @Author: wonderzh
 * @Date: 2020/4/23
 * @Version: 1.0
 */

public enum  StatusCode {
    /**
     * OK
     */
    OK(2000,"OK"),

    /**
     * 连接失败
     */
    CONNECT_REFUSE(3000,"网络连接失败"),
    /**
     * 非法请求
     */
    REQUEST_ILLEGAL(4000,"非法请求报文" ),
    /**
     * 服务器异常
     */
    RESPONSE_ERROR(5000,"服务端响应异常"),
    /**
     * 响应超时
     */
    RESPONSE_TIMEOUT(5001,"响应等待超时" ),
    /**
     * 基本为Client端业务执行异常
     */
    EXECUTION_EXCEPTION(5002,"ExecutionException"),

    /**
     * Channel IOException
     */
    CHANNEL_IO_EXCEPTION(5003, "Channel IOException"),
    /**
     * 法身份信息
     */
    ILLEGAL_IDENTITY(3002,"非法身份信息"),

    REFUSE_ANONYMOUS_ACCESS(3003, "拒绝匿名访问"),
    ;



    private int code;
    private String msg;

    StatusCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int code() {
        return this.code;
    }

    public String message() {
        return this.msg;
    }
}
