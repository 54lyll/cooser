package com.wonderzh.cooser.exception;

/**
 * @Author: wonderzh
 * @Date: 2020/7/8
 * @Version: 1.0
 */

public class ComponentBeanInitializeException extends RuntimeException {

    private static final long serialVersionUID = 7384872983742616887L;

    public ComponentBeanInitializeException(String msg, Throwable e) {
        super(msg,e);
    }

    public ComponentBeanInitializeException(String msg) {
        super(msg);
    }
}
