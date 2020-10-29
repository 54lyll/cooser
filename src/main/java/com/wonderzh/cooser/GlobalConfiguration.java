package com.wonderzh.cooser;


import java.util.concurrent.TimeUnit;

/**
 * 默认全局配置
 * @Author: wonderzh
 * @Date: 2020/7/20
 * @Version: 1.0
 */

public class GlobalConfiguration {

    //-------------------------client start---------------------------------//
    /**
     * 读等待超时
     */
    public static long CLIENT_READ_TIMEOUT = 60 ;

    /**
     * 读等待超时 时间单位
     */
    public static TimeUnit CLIENT_READ_UNIT = TimeUnit.SECONDS;
    /**
     * 客户端心跳发送间隔
     */
    public static int CLIENT_HEART_PING_TIME = 3*60;
    /**
     * 客户端收到服务端心跳间隔，3次未收到心跳响应，即服务端Pong超时
     */
    public static final boolean CLIENT_ENABLE_HEART_CHECK =false ;
    public static int CLIENT_HEART_PONG_TIME = CLIENT_HEART_PING_TIME * 3;
    public static TimeUnit CLIENT_HEART_UNIT = TimeUnit.SECONDS;
    /**
     * 重连接次数与时间间隔
     */
    public static int CLIENT_CONNECT_RETRIES = 2;
    public static long CLIENT_RECONNECT_INTERVAL = 1*1000;

    //-------------------------client end---------------------------------//
    //-------------------------server start---------------------------------//
    /**
     * 服务器默认端口号
     */
    public static final int SERVER_PORT = 8090;
    /**
     * 最大连接数
     */
    public static int SERVER_MAX_CONNECTION = 1024 * 100;
    /**
     * 扫描EventHandler包路径
     */
    public static String SERVER_COMPONENT_SCAN = "com.smartwater";

    /**
     * 心跳监听时间
     */
    public static long SERVER_HEART_TIME =5* 60 ;
    public static TimeUnit SERVER_HEART_UNIT = TimeUnit.SECONDS;
    /**
     * 最大心跳断连周期
     */
    public static int SERVER_MAX_DISCONNECT_PERIOD = 2;
    /**
     * disruptor消费组
     */
    public static final int SERVER_CONSUMER_MIN_SIZE = 4;
    /**
     * disruptor消息环形队列大小， 2的次方 ，大于max_connection
     */
    public static int SERVER_RING_BUFFER_SIZE = 1024 * 1024;

    public static final String SERVER_PERFORMANCE = "normal";

    /**
     * 是否允许匿名访问
     */
    public static final boolean SERVER_ANONYMOUS_ENABLE = false;

    //-------------------------server end---------------------------------//


}


