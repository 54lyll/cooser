package com.wonderzh.cooser.exception;

/**
 * 重复协议映射异常
 * @Author: wonderzh
 * @Date: 2020/7/8
 * @Version: 1.0
 */

public class MultiProtocolMappingException extends RuntimeException {

    private static final long serialVersionUID = 6320719601876485456L;

    public MultiProtocolMappingException(String msg) {
        super(msg);
    }
}
