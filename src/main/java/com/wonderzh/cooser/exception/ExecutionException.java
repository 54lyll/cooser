package com.wonderzh.cooser.exception;


/**
 * @Author: wonderzh
 * @Date: 2020/9/22
 * @Version: 1.0
 */

public class ExecutionException extends Exception {

    private static final long serialVersionUID = -141559638626168776L;

    private int status;

    private String message;

    public ExecutionException(Throwable e) {
        super(e);
    }


    public ExecutionException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public ExecutionException(int code, ClassCastException e) {
        super(e);
        this.status = code;
        this.message = e.getMessage();
    }

    public ExecutionException(int status, String message, Throwable exception) {
        super(message,exception);
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
