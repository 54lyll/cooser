package com.wonderzh.cooser.sever;

import com.wonderzh.cooser.exception.InitializeException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * netty server
 *
 * @Author: wonderzh
 * @Date: 2020/6/2
 * @Version: 1.0
 */
@Slf4j
public class NetServer {

    /**
     * sync(握手)+accept(连接)
     */
    private static final int DEFAULT_BACKLOG_SIZE = 1024;
    public static final int MAX_CONNECTIONS = DEFAULT_BACKLOG_SIZE * 100;
    /**
     * 缓存区动态调配（如果业务能确定报文稳定大小，使用固定值）
     */
    private static final AdaptiveRecvByteBufAllocator RECEIVE_BUFFER_SIZE = AdaptiveRecvByteBufAllocator.DEFAULT;

    private int port = 8090;
    private int CONNECTION_COUNT = DEFAULT_BACKLOG_SIZE;
    private boolean isKeepAlive = true;

    private ChannelInitializer initializer;
    private ChannelFuture channelFuture;
    private EventLoopGroup boss;
    private EventLoopGroup worker;


    public NetServer() {

    }

    /**
     * 端口号
     *
     * @param port
     */
    public NetServer(int port) {
        if (port < 1000) {
            throw new IllegalStateException("port should grant than 1000");
        }
        this.port = port;
    }

    /**
     * 并发连接数
     *
     * @param count
     * @return
     */
    public NetServer connection(int count) {
        if (count < 0) {
            throw new InitializeException("count can't be negative");
        } else if (count == 0) {
            count = 1;
        } else if (count > MAX_CONNECTIONS) {
            count = MAX_CONNECTIONS;
        }
        this.CONNECTION_COUNT = count;
        return this;
    }

    /**
     * 注册channelInitializer
     *
     * @param channelInitializer
     * @return
     */
    public NetServer registerChannelInitializer(ChannelInitializer channelInitializer) {
        this.initializer = channelInitializer;
        return this;
    }

    /**
     * 是否使用长连接
     *
     * @param isKeepAlive
     * @return
     */
    public NetServer keepAlive(boolean isKeepAlive) {
        this.isKeepAlive = isKeepAlive;
        return this;
    }

    /**
     * 绑定启动服务
     *
     * @return
     */
    public ChannelFuture bind() {
        return running();
    }

    /**
     * 绑定启动服务
     *
     * @param port
     * @return
     */
    public ChannelFuture bind(int port) {
        if (port < 1000) {
            throw new IllegalStateException("port should grant than 1000");
        }
        this.port = port;
        return bind();
    }

    /**
     * 启动服务
     *
     * @return
     */
    private synchronized ChannelFuture running() {
        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    //连接数
                    .option(ChannelOption.SO_BACKLOG, CONNECTION_COUNT)
                    //传冲大小
                    .option(ChannelOption.RCVBUF_ALLOCATOR, RECEIVE_BUFFER_SIZE)
                    //缓冲池化
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.SO_KEEPALIVE, isKeepAlive)
                    .childOption(ChannelOption.TCP_NODELAY, true);
            if (this.initializer != null) {
                bootstrap.childHandler(this.initializer);
            }

            //同步等待请求连接
            channelFuture = bootstrap.bind(port).sync();

            log.info("Cooser startup on port: {}",port);
        } catch (Exception e) {
            log.error("Sever startup failed", e);
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
        return channelFuture;
    }

    /**
     * 关闭服务
     */
    public synchronized void shutdown() {
        log.info("shutdown server");
        try {
            //channelFuture.channel().closeFuture().sync();
            channelFuture.channel().close();
        } finally {
            if (boss != null) {
                boss.shutdownGracefully();
            }
            if (worker != null) {
                worker.shutdownGracefully();
            }
        }

    }
}
