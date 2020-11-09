package com.wonderzh.cooser.example;

import com.wonderzh.cooser.client.*;
import com.wonderzh.cooser.common.constarnt.EventType;
import com.wonderzh.cooser.exception.ExecutionException;
import com.wonderzh.cooser.exception.TimeoutException;
import com.wonderzh.cooser.meta.HfPayload;
import com.wonderzh.cooser.meta.MetaSensor;
import com.wonderzh.cooser.protocol.GenericProto;
import com.wonderzh.cooser.protocol.MessageProto;
import com.wonderzh.cooser.protocol.ProtocolMessage;
import com.wonderzh.cooser.protocol.ResponseProto;
import com.wonderzh.cooser.tool.JacksonUtil;
import com.wonderzh.cooser.tool.ProtoConverter;
import com.wonderzh.cooser.tool.ProtoFactory;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wonderzh
 * @Date: 2020/7/13
 * @Version: 1.0
 */
@Slf4j
public class CooClientDemo {


    public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {

        //testLfData();

        jsonTest();

        //concurrenceTest();
    }

    private static void testLfData() throws ExecutionException, InterruptedException {
        //创建连接
        CooClient client = createClient();

        MessageProto.Message metaData = createMeaData();

        CooFuture<ResponseProto.Ack> future = client.send(metaData, ResponseProto.Ack.class);
        //同步
        ResponseProto.Ack ack=future.get();
        System.out.println("同步  ack code:"+ack.getCode());

        //异步
        future.addListener(new FutureCallback<ResponseProto.Ack>() {
            @Override
            public void onSuccess(ResponseProto.Ack ack) {
                System.out.println("异步 ack code:"+ack.getCode());
            }
            @Override
            public void onError(Throwable error) {
                if (error instanceof ExecutionException) {
                    ExecutionException e = (ExecutionException) error;
                    System.out.println(String.format("status: %s , cause: %s",e.getStatus(),e.getMessage()));
                } else {
                    error.printStackTrace();
                }
            }
        });

        //服务断网，客户事件测试
        for (int i = 0; i < 1000; i++) {
            Thread.sleep(1*1000);
            MessageProto.Message metaData2 = createMeaData();
            CooFuture<ResponseProto.Ack> future2 = client.send(metaData2, ResponseProto.Ack.class);
            future2.addListener(new FutureCallback<ResponseProto.Ack>() {
                @Override
                public void onSuccess(ResponseProto.Ack ack) {
                    System.out.println("循环 ack code:"+ack.getCode());
                }
                @Override
                public void onError(Throwable error) {
                    if (error instanceof ExecutionException) {
                        ExecutionException e = (ExecutionException) error;
                        System.out.println(String.format("uuuid:%s, status: %s , cause: %s",metaData2.getUuid(),e.getStatus(),e.getMessage()));
                    } else {
                        System.out.println("future 打印异常");
                        error.printStackTrace();
                    }
                }
            });
        }
    }

    private static void jsonTest() throws ExecutionException, TimeoutException {
        //创建连接
        CooClient client = createClient();

        ProtocolMessage jsonBody =createJsonRequest() ;

        CooFuture<ResponseProto.Ack> future = client.send(jsonBody, ResponseProto.Ack.class);
        //同步
        ResponseProto.Ack ack=future.get(10000,TimeUnit.MILLISECONDS);
        System.out.println("同步  ack code:"+ack.getCode());

        client.close();

    }

    private static CooClient createClient() {
        //除ip外，其余参数均可隐式配置，即默认值
        return CooClient.create(ProtocolMessage.DEFAULT_PROTOCOL)//协议模板可缺省
                .remoteAddress("localhost", 8090)//服务器地址
                .reconnectInHandshake(2, 2 * 1000)//首次握手，与服务器建立连接失败重试
                .readTimeout(10, TimeUnit.SECONDS) //读超时
                .enableHeartCheck(true) //发送心跳，默认关闭
                .heartBeatTime(300, TimeUnit.SECONDS)
                .registerEventListener(new ClientEventListenerDemo())
                .connect();
    }

    private static MessageProto.Message createMeaData() {
        MetaSensor sensor = new MetaSensor();
        sensor.setItem_id("1");
        sensor.setValue(32.0);
        sensor.setTime(System.currentTimeMillis());
        List<MetaSensor> sensors = new ArrayList<>();
        sensors.add(sensor);
        return ProtoConverter.toProtoLfMessage(EventType.LF_UNIVERSAL, sensors);

    }

    private static ProtocolMessage createJsonRequest() {
        JsonBodyTest bodyTest = new JsonBodyTest();
        bodyTest.setName("json test");
        ProtocolMessage message=ProtoFactory.generateJsonRequestProtocol(
                ProtocolMessage.DEFAULT_PROTOCOL,"/dw/json",JacksonUtil.objectToJson(bodyTest));
        return message;
    }

    private static MessageProto.Message creatHfData() throws IOException {
        List<Double> value = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            value.add(23.0);
        }
        HfPayload payload = new HfPayload();
        payload.setItemId("32");
        payload.setFrequency(50);
        payload.setStartTime(new Date());
        payload.setEndTime(new Date(System.currentTimeMillis() + 5000));
        payload.setMaximum(32.0);
        payload.setMinimum(1.0);
        payload.setValue(value);

        MessageProto.Message message = ProtoConverter.toProtoHfMessage(EventType.HF_DATA, 1,  payload);
        return message;

    }



    private static void concurrenceTest() {
        int count = 1;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < count; i++) {
            final int id = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    int no = id;
                    MessageProto.Message metaData = createMeaData();
                    CooClient client = createClient();
                    countDownLatch.countDown();
                    try {
                        countDownLatch.await();
                        //log.info("{} 发送请求", no);
                        long start = System.currentTimeMillis();
                        CooFuture<ResponseProto.Ack> future = client.send(metaData, ResponseProto.Ack.class);
                        ResponseProto.Ack ack=future.get();
                        //log.info("{} 接收响应{}", no, ack.getCode());
                        System.out.println((System.currentTimeMillis()-start)+"");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
