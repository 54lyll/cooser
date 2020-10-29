package com.wonderzh.cooser.sever.factory;

/**
 * @Author: wonderzh
 * @Date: 2020/9/10
 * @Version: 1.0
 */

public class BasicComponentDefinition implements ComponentDefinition {

    protected String className;

    protected Object instance;

    @Override
    public Object getInstance() {
        return this.instance;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }
}
