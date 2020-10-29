package com.wonderzh.cooser.sever.factory;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 组件加载器
 * @Author: wonderzh
 * @Date: 2020/9/9
 * @Version: 1.0
 */

public interface ComponentDefinitionLoader {

    /**
     * 加载组件
     * @param location
     */
    void load(String location);
    /**
     * 获取匹配注解类型的 class
     * @param annotation
     * @return
     */
    Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation);
}
