package com.wonderzh.cooser;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author: wonderzh
 * @Date: 2020/8/6
 * @Version: 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AutoFactoryConfiguration.class)
public @interface EnableCooser {
}
