package com.wonderzh.cooser.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import com.wonderzh.cooser.tool.ThreadPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 环形工作池工厂
 * 生产者消费者线程模型
 *
 * @Author: wonderzh
 * @Date: 2020/6/2
 * @Version: 1.0
 */

public class RingWorkerPoolFactory {

    private Map<String, EventProducer> producers = new ConcurrentHashMap<>(16);
    private Map<String, EventConsumer> consumers = new ConcurrentHashMap<>(16);

    private RingBuffer<EventContext> ringBuffer;

    private SequenceBarrier sequenceBarrier;

    private WorkerPool<EventContext> workerPool;

    private static ExecutorService threadPool;

    private EventProducer defaultProducer;

    private static class SingletonHolder{
        private static final RingWorkerPoolFactory INSTANCE = new RingWorkerPoolFactory();
    }

    private RingWorkerPoolFactory() {

    }

    public  static RingWorkerPoolFactory instance() {
        return SingletonHolder.INSTANCE;
    }

    public void initialize(ProducerType type, int bufferSize, int maxConnection, WaitStrategy waitStrategy, int consumerThreadSize, EventConsumer... eventConsumers) {
        //1. 构建ringBuffer对象
        this.ringBuffer = RingBuffer.create(type,
                new EventFactory() {
                    @Override
                    public EventContext newInstance() {
                        return new EventContext();
                    }
                }, bufferSize, waitStrategy);
        //2.设置序号栅栏
        this.sequenceBarrier = this.ringBuffer.newBarrier();
        //3.设置工作池
        this.workerPool = new WorkerPool(this.ringBuffer,
                this.sequenceBarrier, new EventExceptionHandler(), eventConsumers);
        //4 把所构建的消费者置入池中
        for(EventConsumer mc : eventConsumers){
            consumers.put(mc.getId(), mc);
        }
        //5 添加sequences
        this.ringBuffer.addGatingSequences(this.workerPool.getWorkerSequences());
        //6 启动工作池
        int threadCount = Runtime.getRuntime().availableProcessors();
        threadPool = ThreadPool.newFixedThreadPool("Disruptor-worker",
                consumerThreadSize, consumerThreadSize, 10, maxConnection);
        this.workerPool.start(threadPool);

        defaultProducer = new EventProducer("producer", this.ringBuffer);
    }

    public EventProducer producer(String id) {
        EventProducer producer = producers.get(id);
        if (producer == null) {
            producer = new EventProducer(id,this.ringBuffer);
            producers.put(id, producer);
        }
        return producer;
    }

    public EventProducer defaultProducer() {
        return defaultProducer;
    }

    public void shutdown() {
        //TODO: 是否正确关闭RingBuffer
        if (threadPool != null) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadPool = null;
        }
        this.defaultProducer = null;
    }

    /**
     * 异常静态类
     * @author Alienware
     *
     */
    static class EventExceptionHandler implements ExceptionHandler<EventContext> {
        @Override
        public void handleEventException(Throwable ex, long sequence, EventContext event) {
            ex.printStackTrace();
        }

        @Override
        public void handleOnStartException(Throwable ex) {
            ex.printStackTrace();
        }

        @Override
        public void handleOnShutdownException(Throwable ex) {
            ex.printStackTrace();
        }
    }









}
