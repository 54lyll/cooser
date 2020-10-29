package com.wonderzh.cooser.meta;

import lombok.Data;

import java.util.List;

/**
 * 数据负载
 * @Author: wonderzh
 * @Date: 2019/8/21
 * @Version: 1.0
 */
@Data
public class LFPayload {
    /**
     * 采集量，传感器监测数据
     */
    private List<MetaSensor> sensor;

    private boolean hasContext=false;

    public boolean hasContext() {
        return hasContext;
    }
    private void upStatus() {
        if (!hasContext) {
            hasContext = true;
        }
    }

    public boolean hasSensor() {
        if (sensor == null || sensor.size() == 0) {
            return false;
        } else {
            return true;
        }
    }


    public void setSensor(List<MetaSensor> sensor) {
        this.sensor = sensor;
        upStatus();
    }




}
