package com.wonderzh.cooser.sever;

/**
 * @Author: wonderzh
 * @Date: 2020/9/21
 * @Version: 1.0
 */

public interface Authenticator {

    boolean isPermitted(String identity);
}
