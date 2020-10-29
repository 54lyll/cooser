package com.wonderzh.cooser;

/**
 * 异常处理
 * @Author: wonderzh
 * @Date: 2020/7/10
 * @Version: 1.0
 */
@FunctionalInterface
public interface ExceptionHandler {

    void caught(Throwable th);

}
