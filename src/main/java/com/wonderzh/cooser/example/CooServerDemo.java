package com.wonderzh.cooser.example;

import com.wonderzh.cooser.CooServer;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import io.netty.channel.ChannelFuture;

/**
 * @Author: wonderzh
 * @Date: 2020/7/8
 * @Version: 1.0
 */

public class CooServerDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        ChannelFuture future = CooServer.newInstance(ProtocolMessage.DEFAULT_PROTOCOL)
                .maxConnection(1024)
                .componentScan("com.smartwater.rmc.example")
                .performance(CooServer.Performance.NORMAL)
                .enableAnonymous(true)
                .bind(8090);

        System.out.println("服务器完成启动");
    }

}
