package com.wonderzh.cooser.sever.factory;

import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 类路径扫描注册器
 * Reflections框架实现
 *
 * @Author: wonderzh
 * @Date: 2020/9/9
 * @Version: 1.0
 */

public class ClassPathAnnotationScanner implements ComponentDefinitionLoader {

    private Reflections reflections ;


    @Override
    public void load(String location) {
        this.reflections = StringUtils.isBlank(location) ? new Reflections() : new Reflections(location);
    }

    @Override
    public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) {
        return reflections.getTypesAnnotatedWith(annotation);
    }
}
