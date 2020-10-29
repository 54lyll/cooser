package com.wonderzh.cooser.tool;

import java.util.Collection;

/**
 * @Author: wonderzh
 * @Date: 2020/9/8
 * @Version: 1.0
 */

public class ObjectUtil {


    public static String getBeanName(Class<?> clazz) {
        //TODO: spring beanName 命名规范
        String simpleName=clazz.getSimpleName();
        String name=simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        return name;
    }

    public static boolean isEmpty(Collection<?> collections) {
        return collections == null || collections.size() == 0;
    }

    public static boolean isBlank(String value) {
        return value == null || value == "";
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }
}
