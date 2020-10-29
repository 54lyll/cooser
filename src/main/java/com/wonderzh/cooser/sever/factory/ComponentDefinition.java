package com.wonderzh.cooser.sever.factory;


/**
 * 组件定义信息
 *
 * @Author: wonderzh
 * @Date: 2020/9/8
 * @Version: 1.0
 */

public interface ComponentDefinition {

    /**
     * 实例
     * @return
     */
    Object getInstance();

    /**
     * 类名称
     * @return
     */
    String getClassName();

}
