package com.wonderzh.cooser;

import com.wonderzh.cooser.sever.CooserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @Author: wonderzh
 * @Date: 2020/8/6
 * @Version: 1.0
 */
@EnableConfigurationProperties(CooserConfiguration.class)
@Slf4j
public class AutoFactoryConfiguration {

    /**
     * spring容器 启动服务
     * @param applicationContext
     * @param configuration
     * @return
     */
    @Bean
    public CooserContext cooserContext(ApplicationContext applicationContext,
                                       CooserConfiguration configuration) {
        log.info("begin inject Cooser into Spring IOC");
        CooServer server = CooServer.newInstance()
                .configuration(configuration)
                .springContext(applicationContext);
        server.bind();
        return server.getServerContext();
    }
}
