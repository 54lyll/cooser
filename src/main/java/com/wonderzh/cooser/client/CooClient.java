package com.wonderzh.cooser.client;


import com.google.protobuf.Message;
import com.wonderzh.cooser.ExceptionHandler;
import com.wonderzh.cooser.GlobalConfiguration;
import com.wonderzh.cooser.exception.InitializeException;
import com.wonderzh.cooser.protocol.ProtocolMessage;
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
     * 默认异常处理监听
     */
    private ExceptionHandler exceptionHandler=(th)-> {
            log.error("client execute exception : ",th);
    };

    private RequestHandler requestHandler;
    /**
     * 协议模板
     */
    private ProtocolMessage messagePattern=ProtocolMessage.DEFAULT_PROTOCOL;

    /**
     * 默认配置初始化
     * @return
     */
    public static CooClient create() {
        CooClient client= new CooClient();
        client.initNettyClient();
        return client;
    }

    /**
     * 默认配置初始化
     * @param pattern 协议模板
     * @return
     */
    public static CooClient create(ProtocolMessage pattern) {
        CooClient client= new CooClient();
        client.messagePattern = pattern;
        client.initNettyClient();
        return client;
    }

    /**
     * 初始化NettyClient
     * 使用Netty提供的Probobuf编解码器
     */
    private void initNettyClient() {
        netClient = new NetClient();
        netClient.setReconnect(retries, reconnectInterval);
        netClient.registerChannelInitializer(new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                //心跳
                if (enableHeartCheck) {
                    pipeline.addLast(new IdleStateHandler(heartPongTime, heartPingTime, 0,heartUnit));
                }
                //解码
                pipeline.addLast(new ProtobufVarint32FrameDecoder());
                pipeline.addLast(new ProtobufDecoder(messagePattern));
                //编码
                pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                pipeline.addLast(new ProtobufEncoder());
                //心跳，ctx.write 从此往前写心跳
                pipeline.addLast(new HeartBeatHandler(messagePattern,netClient));
                pipeline.addLast(new ResponseHandler(exceptionHandler,requestHandler));
            }
        });
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
     * 连接建立，重连次数
     * @param retries
     * @param intervalMills
     * @return
     */
    public CooClient setReconnect(int retries, long intervalMills) {
        this.retries = retries;
        this.reconnectInterval = intervalMills;
        return this;
    }

    public CooClient enableHeartCheck(boolean enable) {
        this.enableHeartCheck = enable;
        return this;
    }

    public CooClient heartBeatTIme(int heartPingTime, TimeUnit unit) {
        this.heartPingTime = heartPingTime;
        this.heartUnit = unit;
        return this;
    }

    public CooClient addRequestHandler(RequestHandler handler) {
        this.requestHandler = handler;
        return this;
    }

    /**
     * 建立远程Tcp连接
     */
    public synchronized CooClient connect() {
        if (netClient == null||StringUtils.isBlank(host)||port==null) {
            throw new InitializeException();
        }
        try {
            netClient.connect(this.host, this.port);
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
        if (netClient == null) {
            throw new InitializeException();
        }
        this.host = host;
        this.port = port;
        try {
            netClient.connect(host, port);
        } catch (ConnectException e) {
            throw new InitializeException("connect confuse",e);
        }
        return this;
    }

    /**
     * 发送
     * 每一条 request message，生成一个唯一HprotoFuture管理其response
     * HprotoFuture 阻塞同步返回response
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
