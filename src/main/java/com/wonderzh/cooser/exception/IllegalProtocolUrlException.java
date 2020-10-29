package com.wonderzh.cooser.exception;

/**
 * @Author: wonderzh
 * @Date: 2020/8/18
 * @Version: 1.0
 */

public class IllegalProtocolUrlException extends Exception {
    private static final long serialVersionUID = 3051434454517241939L;

    public IllegalProtocolUrlException(String msg) {
        super(msg);
    }

    public IllegalProtocolUrlException() {

    }
}
