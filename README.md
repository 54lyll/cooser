https://blog.csdn.net/qq_42022528/article/details/109387773
## 1服务器架构
### 1.1简介
![frame](http://wind_zhou.gitee.io/imagebed/pic/1.jpg)
Cooser是基于Netty框架搭建的Reactor模式高性能，可扩展的网络服务器。基于Netty对NIO的多路复用机制，高效利用cpu硬件资源，优化服务器并发连接性能，达到伸缩性要求。我司业务场景为常量连接（十百千），大量请求，作为底层TCP服务器，从数据传输体量、解析性能考虑，采用Google Protobuf通信协议，自定义传输数据结构。Disruptor是一款极速并发框架，Cooser引入它作为中间消息队列，将网络数据接收和业务处理逻辑单元解耦，极大提升服务器吞吐性能，同时保持低延迟。在网络请求处理链路的多个环节,开发了插拔组件API,如Channel生命周期监听，请求事件的前后置拦截器，业务处理类MVC模式，使cooser成为一个在业务上具有高扩展性的服务器开发框架。
### 1.2 Quick start
##### 代码方式
```java
ChannelFuture future = CooServer.newInstance(ProtocolMessage.DEFAULT_PROTOCOL)
                .maxConnection(1024)
                .componentScan("com.wonderzh.cooser.example")
                .performance(CooServer.Performance.NORMAL)
                .enableAnonymous(true)
                .bind(8090);
```
##### 注解方式
基于springboot启动，在@SpringBootApplication启动类上使用@EnableCooser。
Apllication.yml文件配置参数
```bash
cooser:
  server:
    port: 8090  #端口号
    max-connection: 5000   #最大连接数
    heart-time: 300  #心跳监听时间，单位秒
    component-scan: com.smartwater.data   #组件扫描包路径
    performance: normal   #服务器业务消费侦听线程性能
    anonymous-enable: false  #是否允许匿名访问
```
## 2服务器详细设计
### 2.1服务器初始化详细设计
服务器初始化主要做三件工作
1. 解析配置，创建服务器上下文CooserContext
2. 初始化服务器功能组件，并注册到Dispatcher调度器
3. 启动Netty Server，监听端口
![server](http://wind_zhou.gitee.io/imagebed/pic/2.jpg)
### 2.2服务器网络请求处理详细设计
服务器对网络请求处理的主要由三个功能模块承载，分别为请求消息生产MessageDuplexHandler、请求业务处理MessageProcessor以及综合调度FrameDispatcher。
![server-process](http://wind_zhou.gitee.io/imagebed/pic/3.jpg)
