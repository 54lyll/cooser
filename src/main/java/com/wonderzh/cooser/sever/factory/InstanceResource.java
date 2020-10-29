package com.wonderzh.cooser.sever.factory;

/**
 * 类的实例化源
 * @Author: wonderzh
 * @Date: 2020/9/9
 * @Version: 1.0
 */

public interface InstanceResource {

    /**
     * 类的实例化
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getInstance(Class<T> clazz);
}
