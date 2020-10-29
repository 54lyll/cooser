package com.wonderzh.cooser.exception;

/**
 * 客户端初始化异常
 * @Author: wonderzh
 * @Date: 2020/4/17
 * @Version: 1.0
 */

public class InitializeException extends RuntimeException {

    private static final long serialVersionUID = -1;

    public InitializeException() {
        super();
    }

    public InitializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializeException(String message) {
        super(message);
    }

    public InitializeException(Throwable cause) {
        super(cause);
    }
}
