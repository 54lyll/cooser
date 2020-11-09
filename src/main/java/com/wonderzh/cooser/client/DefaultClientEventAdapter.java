package com.wonderzh.cooser.client;

import com.wonderzh.cooser.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: wonderzh
 * @Date: 2020/11/5
 * @Version: 1.0
 */
@Slf4j
public class DefaultClientEventAdapter implements ClientEventListener {

    private ExceptionHandler exceptionHandler = (th) -> {
        log.error("client unexpected exception : ", th);
    };

    @Override
    public void channelActive(ClientHolder clientHolder) {
        log.info("client connect to the server {}", clientHolder.getCtx().channel().remoteAddress().toString());
    }

    @Override
    public void channelInactive(ClientHolder clientHolder) {
        log.info("client disconnect from the server {}", clientHolder.getCtx().channel().remoteAddress().toString());
    }

    @Override
    public void channelRead(ClientHolder clientHolder, Object msg) {
        log.warn("client receive a message from server {}, please deal with it", clientHolder.getCtx().channel().remoteAddress().toString());
    }

    @Override
    public void channelException(ClientHolder clientHolder, Throwable cause) {
        //兜底处理异常
        exceptionHandler.caught(cause);
    }
}
