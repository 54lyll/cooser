package com.wonderzh.cooser.common.constarnt;

/**
 * @Author: wonderzh
 * @Date: 2019/10/17
 * @Version: 1.0
 */

public class EventType {

    /**
     * 低频通用数据请求类型,包括清洗、转换、分析、存储
     */
    public static final byte LF_UNIVERSAL = 0;
    /**
     * 低频分发数据请求类型,包括清洗、转换、通知发送
     */
    public static final byte LF_DISTRIBUTION = 1;

    /**
     * 高频数据存储
     */
    public static final byte HF_DATA = 0;

    /**
     * 高频数据    水击防护事件
     */
    public static final byte HF_WATER_HAMMER = 1;
    /**
     * 高频数据    空气阀
     */
    public static final byte HF_AIR_VALVE = 2;

    public static boolean containsHfEvent(Integer eventType) {
        if (eventType == null) {
            return false;
        }
        int type = eventType;
        switch (type) {
            case HF_DATA:
                return true;
            case HF_WATER_HAMMER:
                return true;
            case HF_AIR_VALVE:
                return true;
            default:
                return false;
        }
    }
}
