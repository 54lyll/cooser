package com.wonderzh.cooser.tool;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * 消息唯一id生成器，主要用于探针
 * 64bit 后 63bit: 14 bit 作为工作机器 id(随机id) , 41 bit 作为毫秒数, 8 bit 作为并发序列号。
 * @Author: wonderzh
 * @Date: 2019/7/9
 * @Version: 1.0
 */
@Slf4j
public class MsgUuidGenerator {

    //开始时间截 (2020-08-018)，自己设定
    private final long twepoch = 1597730994176L;
    //机器ID所占位置
    private final long workerIdBits = 14L;

    //支持的最大机器id，结果是16383 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    //序列在id中占的位数
    private final long sequenceBits = 8L;
    //生成序列的掩码，这里为255 ,也是8位能存储的最大正整数：255
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
    //机器ID向左移8位
    private final long workerIdShift = sequenceBits;

    //时间截向左移22位(8+14)
    private final long timestampLeftShift = sequenceBits + workerIdBits ;

    //工作机器ID
    private long workerId;
    //毫秒内序列
    private long sequence = 0L;
    //上次生成ID的时间截
    private long lastTimestamp = -1L;

    private Random random = new Random();

    private MsgUuidGenerator() {
        this.workerId=random.nextInt((int) maxWorkerId);
    }

    private static class InnerSingleton {
        private static final MsgUuidGenerator instance = new MsgUuidGenerator();

    }

    /**
     * 静态单例
     * @return
     */
    public static MsgUuidGenerator instance() {
        return InnerSingleton.instance;
    }

    /**
     * 带参构造函数
     * 数据中心号和工作机器号根据实际部署情况手动设置
     *
     * @param workerId
     */
    public MsgUuidGenerator(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format(
                    "Worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        this.workerId = workerId;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return
     */
    public synchronized long nextId() {
        long timestamp = timeNow();
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            log.error(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
            timestamp = lastTimestamp;
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = untilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    private long untilNextMillis(long lastTimestamp) {
        long timestamp = timeNow();
        while (timestamp <= lastTimestamp) {
            timestamp = timeNow();
        }
        return timestamp;
    }

    private long timeNow() {
        return System.currentTimeMillis();
    }

}
