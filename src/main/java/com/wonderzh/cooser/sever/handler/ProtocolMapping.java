package com.wonderzh.cooser.sever.handler;

import java.lang.annotation.*;

/**
 * 协议映射方法
 * @Author: wonderzh
 * @Date: 2020/7/8
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ProtocolMapping {

    String value() default "";

}
