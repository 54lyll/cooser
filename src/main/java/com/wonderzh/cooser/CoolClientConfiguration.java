package com.wonderzh.cooser;

/**
 * @Author: wonderzh
 * @Date: 2020/8/6
 * @Version: 1.0
 */
//@Configuration
public class CoolClientConfiguration {

    private Long readTimeout;
    private Integer heartPingTime ;
    private Integer heartPongTime ;
    private Integer connectRetries ;
    private Long reconnectInterval;

    public void setReadTimeout(Long readTimeout) {
        if (readTimeout > 0) {
            this.readTimeout = readTimeout;
        }
    }

    public void setHeartPingTime(Integer heartPingTime) {
        if (heartPingTime > 0) {
            this.heartPingTime = heartPingTime;
        }
    }

    public void setHeartPongTime(Integer heartPongTime) {
        if (heartPongTime > heartPingTime) {
            this.heartPongTime = heartPongTime;
        }
    }

    public void setConnectRetries(Integer connectRetries) {
        if (connectRetries >= 0) {
            this.connectRetries = connectRetries;
        }
    }

    public void setReconnectInterval(Long reconnectInterval) {
        if (reconnectInterval > 0) {
            this.reconnectInterval = reconnectInterval;
        }
    }

    public Long getReadTimeout() {
        return readTimeout;
    }

    public Integer getHeartPingTime() {
        return heartPingTime;
    }

    public Integer getHeartPongTime() {
        return heartPongTime;
    }

    public Integer getConnectRetries() {
        return connectRetries;
    }

    public Long getReconnectInterval() {
        return reconnectInterval;
    }
}
