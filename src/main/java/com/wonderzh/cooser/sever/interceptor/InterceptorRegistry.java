package com.wonderzh.cooser.sever.interceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 拦截器注册工具
 *
 * @Author: wonderzh
 * @Date: 2020/9/10
 * @Version: 1.0
 */

public class InterceptorRegistry {

    private List<ChannelInterceptorHolder> holders = new ArrayList<>();

    public ChannelInterceptorHolder addInterceptor(ChannelInterceptor interceptor) {
        ChannelInterceptorHolder holder = ChannelInterceptorHolder.hold(interceptor);
        holders.add(holder);
        return holder;
    }

    public List<ChannelInterceptorHolder> getInterceptors() {
        return this.holders;
    }
}
