package com.wonderzh.cooser.meta;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: wonderzh
 * @Date: 2020/8/10
 * @Version: 2.0
 */
@Data
public class HfPayload {
    /**
     * 监控量id
     */
    private String itemId;
    /**
     * 量的值
     */
    private List<Double> value;
    /**
     * 本组数据中最大值
     */
    private Double maximum;
    /**
     * 本组数据中最小值
     */
    private Double minimum;
    /**
     * 数据采集开始时间
     */
    private Date startTime;
    /**
     * 数据采集结束时间
     */
    private Date endTime;
    /**
     * 频率
     */
    private Integer frequency;

    private String collectionId;

    private String collectionName;

    private String deviceId;

    private Map<String,String> extraInfo;


}
