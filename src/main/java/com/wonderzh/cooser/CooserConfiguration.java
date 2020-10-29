package com.wonderzh.cooser;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: wonderzh
 * @Date: 2020/8/6
 * @Version: 1.0
 */
@ConfigurationProperties(prefix = "cooser.server")
public class CooserConfiguration {

    private Integer port = GlobalConfiguration.SERVER_PORT;
    private Integer maxConnection =GlobalConfiguration.SERVER_MAX_CONNECTION;
    private String componentScan =GlobalConfiguration.SERVER_COMPONENT_SCAN;
    private Long heartTime=GlobalConfiguration.SERVER_HEART_TIME;
    private Integer maxDisconnectPeriod=GlobalConfiguration.SERVER_MAX_DISCONNECT_PERIOD;
    private Integer ringBufferSize=GlobalConfiguration.SERVER_RING_BUFFER_SIZE;
    private String performance = GlobalConfiguration.SERVER_PERFORMANCE;
    private boolean anonymousEnable = GlobalConfiguration.SERVER_ANONYMOUS_ENABLE;


    public void setPort(Integer port) {
        if (port > 1000) {
            this.port = port;
        }
    }

    public void setMaxConnection(Integer maxConnection) {
        if (maxConnection > 1) {
            this.maxConnection = maxConnection;
        }
    }

    public void setComponentScan(String componentScan) {
        if (StringUtils.isNotBlank(componentScan)) {
            this.componentScan = componentScan;
        }
    }

    public void setHeartTime(Long heartTime) {
        if (heartTime > 0) {
            this.heartTime = heartTime;
        }
    }

    public void setRingBufferSize(Integer ringBufferSize) {
        if (ringBufferSize > 0) {
            this.ringBufferSize = ringBufferSize;
        }
    }

    public void setMaxDisconnectPeriod(Integer maxDisconnectPeriod) {
        if (maxDisconnectPeriod > 0) {
            this.maxDisconnectPeriod = maxDisconnectPeriod;
        }
    }

    public Integer getMaxConnection() {
        return maxConnection;
    }

    public String getComponentScan() {
        return componentScan;
    }

    public Long getHeartTime() {
        return heartTime;
    }

    public Integer getMaxDisconnectPeriod() {
        return maxDisconnectPeriod;
    }

    public Integer getRingBufferSize() {
        return ringBufferSize;
    }

    public Integer getPort() {
        return port;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public boolean isAnonymousEnable() {
        return anonymousEnable;
    }

    public void setAnonymousEnable(boolean anonymousEnable) {
        this.anonymousEnable = anonymousEnable;
    }
}
