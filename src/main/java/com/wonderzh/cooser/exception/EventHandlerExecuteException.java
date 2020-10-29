package com.wonderzh.cooser.exception;

/**
 * @Author: wonderzh
 * @Date: 2020/7/9
 * @Version: 1.0
 */

public class EventHandlerExecuteException extends Exception{
    private static final long serialVersionUID = -7637521882763529319L;

    public EventHandlerExecuteException() {

    }

    public EventHandlerExecuteException(Throwable e) {
        super(e);
    }

    public EventHandlerExecuteException(String msg) {
        super(msg);
    }

    public EventHandlerExecuteException(String msg,Throwable e) {
        super(msg,e);
    }
}
