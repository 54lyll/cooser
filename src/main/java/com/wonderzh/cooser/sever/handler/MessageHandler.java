package com.wonderzh.cooser.sever.handler;

import java.lang.annotation.*;

/**
 * 请求消息处理器
 *
 * @Author: wonderzh
 * @Date: 2020/7/8
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface MessageHandler {
}
