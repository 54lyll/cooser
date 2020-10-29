package com.wonderzh.cooser;

import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import com.wonderzh.cooser.disruptor.EventConsumer;
import com.wonderzh.cooser.disruptor.RingWorkerPoolFactory;
import com.wonderzh.cooser.exception.InitializeException;
import com.wonderzh.cooser.sever.NetServer;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import com.wonderzh.cooser.sever.*;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * High-Concurrency Server with custom protocol
 * 百万高并发服务器模型 Netty + Protobuf + Disruptor
 * Netty处理大数据量并发连接-->Protobuf自定义协议传输数据-->Disruptor高性能生产消费模式异步处理业务
 * Tcp+Google Protobuf 为传输协议栈，PRH Frame为TCP请求路由框架
 * Netty WorkerEventGroup线程压力由Disruptor释放
 *
 * @Author: wonderzh
 * @Date: 2020/7/8
 * @Version: 1.0
 */
public class CooServer {

    /**
     * 端口
     */
    private int port=GlobalConfiguration.SERVER_PORT;
    /**
     * 最大连接数
     */
    private int maxConnection = GlobalConfiguration.SERVER_MAX_CONNECTION;
    /**
     * 扫描EventHandler包路径
     */
    private String componentScan = GlobalConfiguration.SERVER_COMPONENT_SCAN;

    /**
     * 心跳监听时间
     */
    private long heartTime = GlobalConfiguration.SERVER_HEART_TIME;
    private TimeUnit heartUnit = GlobalConfiguration.SERVER_HEART_UNIT;
    /**
     * 最大心跳断连周期
     */
    public static int MAX_DISCONNECT_PERIOD = GlobalConfiguration.SERVER_MAX_DISCONNECT_PERIOD;
    /**
     * ChannelHandler 初始化类
     */
    private ChannelInitializer channelInitializer;

    /**
     * Google Protobuf 消息实体
     */
    private ProtocolMessage messageLite = ProtocolMessage.DEFAULT_PROTOCOL;

    /**
     * 匿名访问
     */
    private boolean anonymousEnable = false;

    /**
     * Netty Server
     */
    private NetServer server;

    /**
     * 应用上下文
     */
    private ConfigurableServerContext serverContext = new GenericServerContext(); ;

    /**
     * 对外暴露顶层接口CooserContext
     * @return
     */
    public CooserContext getServerContext() {
        return serverContext;
    }

    /**
     * disruptor消费组
     */
    private static int MIN_SIZE = GlobalConfiguration.SERVER_CONSUMER_MIN_SIZE;
    private static int CONSUMER_THREAD_SIZE;

    static {
        int processors = Runtime.getRuntime().availableProcessors();
        if (processors < MIN_SIZE) {
            processors = MIN_SIZE;
        }
        CONSUMER_THREAD_SIZE = processors * 2;
    }
    /**
     * 消息传冲队列大小
     */
    private int messageBufferSize = GlobalConfiguration.SERVER_RING_BUFFER_SIZE;

    /**
     * disruptor 消费者等待策略
     * normal SleepingWaitStrategy
     * hight YieldingWaitStrategy
     */
    private String performance= GlobalConfiguration.SERVER_PERFORMANCE;

    private final AtomicBoolean active = new AtomicBoolean();

    /**
     * 创建服务器实例
     *
     * @param protocol 传输协议
     * @return
     */
    public static CooServer newInstance(ProtocolMessage protocol) {
        CooServer server = new CooServer();
        server.registerProtocol(protocol);
        return server;
    }

    /**
     * 创建服务器实例
     * 使用默认传输协议
     *
     * @return
     */
    public static CooServer newInstance() {
        return new CooServer();
    }


    /**
     * Google Protobuf 协议模板
     *
     * @param protocolFormat
     */
    public CooServer registerProtocol(ProtocolMessage protocolFormat) {
        if (protocolFormat == null) {
            throw new InitializeException("ProtocolPattern is null");
        }
        this.messageLite = protocolFormat;
        return this;
    }

    /**
     * 配置类进行参数配置
     * @param configuration
     * @return
     */
    public CooServer configuration(CooserConfiguration configuration) {
        if (configuration == null) {
            throw new InitializeException("configuration is null");
        }
        this.port = configuration.getPort();
        this.maxConnection = configuration.getMaxConnection();
        this.componentScan =configuration.getComponentScan();
        this.heartTime = configuration.getHeartTime();
        this.messageBufferSize=configuration.getRingBufferSize();
        MAX_DISCONNECT_PERIOD=configuration.getMaxDisconnectPeriod();
        this.performance = configuration.getPerformance();
        this.serverContext.configuration(configuration);
        return this;
    }

    /**
     * 融合Spring容器
     * @param applicationContext
     * @return
     */
    public CooServer springContext(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            throw new InitializeException("ApplicationContext is null");
        }
        this.serverContext.registerSpringContext(applicationContext);
        return this;
    }

    /**
     * 最大连接数
     *
     * @param count
     */
    public CooServer maxConnection(int count) {
        this.maxConnection = count;
        return this;
    }

    /**
     * @param scanPackage
     * @return
     * @ EvetnHandler @ ProtoclMapping 所在包
     */
    public CooServer componentScan(String scanPackage) {
        if (StringUtils.isBlank(scanPackage)) {
            throw new InitializeException("the package of mapper is blank");
        }
        this.componentScan = scanPackage;
        return this;
    }

    /**
     * 服务器消费者性能模式
     * HIGH模式cpu 70%-90% 仅适合单机部署
     * @param performance
     * @return
     */
    public CooServer performance(Performance performance) {
        if (performance == null) {
            throw new InitializeException("Performance is null");
        }
        this.performance = performance.key;
        return this;
    }

    /**
     * 是否允许匿名访问
     * @param anonymousEnable
     * @return
     */
    public CooServer enableAnonymous(boolean anonymousEnable) {
        this.anonymousEnable = anonymousEnable;
        return this;
    }

    /**
     * 服务器初始化准备工作
     * 初始化CooServer组件
     *  注册ChannelHandler
     *  启动Disruptor消息队列
     */
    private void prepareInitialize() {
        initServerContext();
        registerHandler();
        startDisruptor();
    }

    /**
     * 编解码Handler Google protobuf
     * 路由器Handler
     *
     */
    private void registerHandler() {
        ConfigurableServerContext context = this.serverContext;
        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //心跳
                ch.pipeline().addLast(new IdleStateHandler(heartTime, 0, 0, heartUnit));
                //解码
                ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                ch.pipeline().addLast(new ProtobufDecoder(messageLite));
                //编码
                ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                ch.pipeline().addLast(new ProtobufEncoder());
                //心跳
                ch.pipeline().addLast(new ServerIdleStateTrigger());
                //Disruptor生产者
                ch.pipeline().addLast(new MessageDuplexHandler(context));
            }
        };

    }

    /**
     * 初始化Disruptor组件
     * 消费者为协议分发器 Protocol Route
     */
    private void startDisruptor() {
        EventConsumer[] consumers = new EventConsumer[CONSUMER_THREAD_SIZE];
        try {
            for (int i = 0; i < CONSUMER_THREAD_SIZE; i++) {
                EventConsumer consumer = new MessageProcessor(this.serverContext);
                consumers[i] = consumer;
            }
        } catch (Exception e) {
            throw new InitializeException();
        }
        WaitStrategy waitStrategy = Performance.get(this.performance);
        if (waitStrategy == null) {
            throw new InitializeException("illegal performance: "+this.performance);
        }
        RingWorkerPoolFactory.instance().initialize(ProducerType.MULTI, this.messageBufferSize,
                this.maxConnection, waitStrategy, CONSUMER_THREAD_SIZE,consumers);
    }

    /**
     * 初始化服务器上下文
     * 扫描注册Cooser组件，如业务处理逻辑单元EventHandler
     */
    private void initServerContext() {
        if (serverContext.getConfiguration() == null) {
            CooserConfiguration configuration = new CooserConfiguration();
            configuration.setAnonymousEnable(this.anonymousEnable);
            configuration.setComponentScan(this.componentScan);
            configuration.setHeartTime(this.heartTime);
            configuration.setMaxConnection(this.maxConnection);
            configuration.setMaxDisconnectPeriod(MAX_DISCONNECT_PERIOD);
            configuration.setPerformance(this.performance);
            configuration.setPort(this.port);
            configuration.setRingBufferSize(this.messageBufferSize);
            serverContext.configuration(configuration);
        }
        serverContext.setProtocolPattern(this.messageLite);
        serverContext.initialize(componentScan);
    }

    /**
     * 启动服务器
     * @return
     */
    public ChannelFuture bind() {
        return bind(this.port);
    }

    /**
     * 启动服务器
     *
     * @param port
     * @return
     */
    public ChannelFuture bind(int port) {
        if (active.get()) {
            throw new IllegalStateException("The server is running");
        }

        prepareInitialize();

        this.server = new NetServer()
                .connection(maxConnection)
                .keepAlive(true)
                .registerChannelInitializer(channelInitializer);

        ChannelFuture future= server.bind(port);
        active.set(true);
        return future;
    }

    /**
     * 关闭服务
     * 清理线程池、容器等资源
     */
    public void close() {
        //关闭netty server
        server.shutdown();
        server = null;
        //关闭消费者线程
        RingWorkerPoolFactory.instance().shutdown();
        //注销容器
        serverContext.destroy();

        active.set(false);
    }

    public ProtocolMessage getProtocolFormat() {
        return messageLite;
    }


    /**
     * 性能模式
     */
    public enum Performance {
        /**
         * 对应Disruptor 消费者等待策略YieldingWaitStrategy cpu70%-100%
         */
        HIGH("high", new YieldingWaitStrategy()),
        /**
         * 对应Disruptor 消费者等待策略SleepingWaitStrategy cpu0%-40%
         */
        NORMAL("normal",new SleepingWaitStrategy()),;

        private String key;
        private WaitStrategy waitStrategy;

        Performance(String key, WaitStrategy waitStrategy) {
            this.key = key;
            this.waitStrategy = waitStrategy;
        }

        public static WaitStrategy get(String performance) {
            for (Performance waitStrategy : values()) {
                if (waitStrategy.getKey().equals(performance)) {
                    return waitStrategy.getWaitStrategy();
                }
            }
            return null;
        }

        public String getKey() {
            return key;
        }

        public WaitStrategy getWaitStrategy() {
            return waitStrategy;
        }
    }

}
