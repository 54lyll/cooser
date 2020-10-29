package com.wonderzh.cooser.client;

/**
 * Future 侦听回调
 * @Author: wonderzh
 * @Date: 2020/9/22
 * @Version: 1.0
 */

public interface FutureCallback<T> {

    void onSuccess(T t);

    void onError(Throwable error);

}
