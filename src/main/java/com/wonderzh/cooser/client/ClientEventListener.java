package com.wonderzh.cooser.client;



/**
 * @Author: wonderzh
 * @Date: 2020/11/4
 * @Version: 1.0
 */

public interface ClientEventListener {

    /**
     * 建立连接
     * @param clientHolder
     */
    void channelActive(ClientHolder clientHolder) throws Exception;

    /**
     * 断开连接
     * @param clientHolder
     */
    void channelInactive(ClientHolder clientHolder) throws Exception;

    /**
     * 处理请求
     * @param clientHolder
     * @param msg
     * @throws Exception 将被channelException()捕捉
     */
    void channelRead(ClientHolder clientHolder, Object msg) throws Exception;

    /**
     * 捕获通道异常
     * 首先会执行Future.holdException,再执行该方法
     * @param clientHolder
     * @param cause
     */
    void channelException(ClientHolder clientHolder, Throwable cause);

}
