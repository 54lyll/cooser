package com.wonderzh.cooser.sever;


/**
 * Cooser服务器上下文注册接口
 * @Author: wonderzh
 * @Date: 2020/10/16
 * @Version: 1.0
 */

public interface ServerContextAware {

    void setServerContext(CooserContext serverContext);

}
