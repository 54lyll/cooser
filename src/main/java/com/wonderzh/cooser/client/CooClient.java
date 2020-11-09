package com.wonderzh.cooser.client;


import com.google.protobuf.Message;
import com.wonderzh.cooser.GlobalConfiguration;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import com.wonderzh.cooser.exception.InitializeException;
import com.wonderzh.cooser.sever.NetClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Hproto 客户端
 * 一个客户端建立一个Tcp Nio 长连接
 *
 * @Author: wonderzh
 * @Date: 2020/7/9
 * @Version: 1.0
 */
@Slf4j
public class CooClient {
    /**
     * Netty客户端
     */
    private NetClient netClient;
    /**
     * 读等待超时
     */
    private long readTimeout = GlobalConfiguration.CLIENT_READ_TIMEOUT ;
    /**
     * 读等待超时 时间单位
     */
    private TimeUnit readUnit = GlobalConfiguration.CLIENT_READ_UNIT;
    /**
     * 心跳，3次未收到心跳响应，即服务端Pong超时
     */
    private boolean enableHeartCheck=GlobalConfiguration.CLIENT_ENABLE_HEART_CHECK;
    private int heartPingTime = GlobalConfiguration.CLIENT_HEART_PING_TIME;
    private int heartPongTime = GlobalConfiguration.CLIENT_HEART_PONG_TIME;
    private TimeUnit heartUnit = GlobalConfiguration.CLIENT_HEART_UNIT;
    /**
     * 重连接次数与时间间隔
     */
    private int retries =GlobalConfiguration.CLIENT_CONNECT_RETRIES;
    private long reconnectInterval = GlobalConfiguration.CLIENT_RECONNECT_INTERVAL;
    /**
     * 远程服务端host
     */
    private String host;
    /**
     * 远程服务端port
     */
    private Integer port;

    /**
     * 连接事件监听器
     */
    private ClientEventListener eventListener;
    /**
     * 协议模板
     */
    private ProtocolMessage messagePattern=ProtocolMessage.DEFAULT_PROTOCOL;

    private boolean isClose = true;

    private CooClient() {

    }

    /**
     * 默认配置初始化
     * @return
     */
    public static CooClient create() {
        return new CooClient();
    }

    /**
     * 默认配置初始化
     * @param pattern 协议模板
     * @return
     */
    public static CooClient create(ProtocolMessage pattern) {
        CooClient client= new CooClient();
        client.messagePattern = pattern;
        return client;
    }


    /**
     * 设置远程连接地址
     * @param host
     * @param port
     * @return
     */
    public CooClient remoteAddress(String host, int port) {
        InetSocketAddress address = new InetSocketAddress(host, port);
        this.host = host;
        this.port = port;
        return this;
    }

    /**
     * 读取响应超时时间
     * @param time
     * @param unit
     * @return
     */
    public CooClient readTimeout(long time, TimeUnit unit) {
        if (time < 0) {
            throw new IllegalArgumentException("time can't be negative");
        }
        if (time < 1) {
            time = 1;
        }
        if (unit == null) {
            throw new NullPointerException();
        }
        this.readTimeout = time;
        this.readUnit = unit;
        return this;
    }

    /**
     * 与服务首次握手建立连接，失败重连次数
     * @param retries
     * @param intervalMills
     * @return
     */
    public CooClient reconnectInHandshake(int retries, long intervalMills) {
        this.retries = retries;
        this.reconnectInterval = intervalMills;
        return this;
    }

    /**
     * 是否开启心跳
     * @param enable
     * @return
     */
    public CooClient enableHeartCheck(boolean enable) {
        this.enableHeartCheck = enable;
        return this;
    }

    /**
     * 心跳发送时间机制
     * @param heartPingTime
     * @param unit
     * @return
     */
    public CooClient heartBeatTime(int heartPingTime, TimeUnit unit) {
        this.heartPingTime = heartPingTime;
        this.heartUnit = unit;
        return this;
    }

    /**
     * 注册客户端事件监听器
     * @param eventListener
     * @return
     */
    public CooClient registerEventListener(ClientEventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    /**
     * 建立远程Tcp连接
     */
    public synchronized CooClient connect() {
        if (StringUtils.isBlank(host)||port==null) {
            throw new InitializeException();
        }
        try {
            this.netClient=initNettyClient();
            this.netClient.connect(host, port);
        } catch (ConnectException e) {
            throw new InitializeException("connect confuse",e);
        }
        return this;
    }

    /**
     * 建立远程Tcp连接
     * @param host
     * @param port
     */
    public synchronized CooClient connect(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            this.netClient=initNettyClient();
            this.netClient.connect(host, port);
        } catch (ConnectException e) {
            throw new InitializeException("connect confuse",e);
        }
        return this;
    }

    /**
     * 初始化NettyClient
     * 使用Netty提供的Probobuf编解码器
     */
    private NetClient initNettyClient() {
        NetClient netClient = new NetClient();
        netClient.setReconnect(retries, reconnectInterval);
        netClient.registerChannelInitializer(new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                //心跳检测基础
                if (enableHeartCheck) {
                    pipeline.addLast(new IdleStateHandler(heartPongTime, heartPingTime, 0,heartUnit));
                }
                //解码
                pipeline.addLast(new ProtobufVarint32FrameDecoder());
                pipeline.addLast(new ProtobufDecoder(messagePattern));
                //编码
                pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                pipeline.addLast(new ProtobufEncoder());
                //心跳监测handler，ctx.write 从此往前写心跳，才能编码
                if (enableHeartCheck) {
                    pipeline.addLast(new HeartBeatHandler(messagePattern));
                }
                pipeline.addLast(new ChannelEventHandler(getCooClient(), getClientEventListener()));
            }
        });
        return netClient;
    }

    private CooClient getCooClient() {
        return this;

    }

    public ClientEventListener getClientEventListener() {
        return this.eventListener == null ? new DefaultClientEventAdapter() : this.eventListener;
    }

    /**
     * 发送
     * 每一条 request message，生成一个唯一CooFuture管理其response
     * NetClient.send已经使用读写锁保护
     *
     * @param message 信息 Google ProtoBuf  Message子类
     * @param responseType 响应体类型
     * @param <T>
     * @return 响应超时返回null
     */
    public <T extends Message> CooFuture<T> send(ProtocolMessage message, Class<T> responseType) {
        CooFuture<T> future = new CooFuture<T>(message.getUuid(),responseType,
                TimeUnit.MILLISECONDS.convert(this.readTimeout, this.readUnit));
        FutureContext.apply(future);
        try {
            ChannelFuture channelFuture= netClient.send(message);
            future.registerChannelFuture(channelFuture);
        } catch (IOException e) {
            future.holdException( e);
        }
        return future;
    }

    /**
     * 关闭
     */
    public synchronized void close() {
        netClient.close();
        netClient = null;
    }

    /**
     * 与服务器在通信状态下断开连接，尝试重连
     *  netClient.reconnectForever()异步调用
     */
    protected void doReconnectInCommunication() {
        this.netClient.reconnectForever();
    }


    public long getReadTimeout() {
        return readTimeout;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public boolean isConnect() {
        return netClient.isConnected();
    }



}
