package com.wonderzh.cooser.client;

/**
 * Future 侦听回调
 * @Author: wonderzh
 * @Date: 2020/9/22
 * @Version: 1.0
 */

public interface FutureCallback<T> {

    /**
     * 成功
     * @param t
     * @throws Exception onError捕获
     */
    void onSuccess(T t) throws Exception;

    /**
     * 异常
     * @param error
     * @throws Exception
     */
    void onError(Throwable error) throws Exception;

}
