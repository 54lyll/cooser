package com.wonderzh.cooser.sever.factory;

import java.lang.annotation.*;

/**
 * @Author: wonderzh
 * @Date: 2020/9/9
 * @Version: 1.0
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface CooserComponent {

    String name() default "";

}
