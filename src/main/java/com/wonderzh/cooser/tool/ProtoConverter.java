package com.wonderzh.cooser.tool;

import com.wonderzh.cooser.meta.HfPayload;
import com.wonderzh.cooser.meta.MetaSensor;
import com.wonderzh.cooser.protocol.MessageProto;
import com.wonderzh.cooser.protocol.NullDataFlag;
import com.wonderzh.cooser.protocol.ProtocolUrl;
import com.wonderzh.cooser.protocol.RequestProto;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Author: wonderzh
 * @Date: 2020/8/5
 * @Version: 1.0
 */

public class ProtoConverter {

    private static  MsgUuidGenerator uuid = MsgUuidGenerator.instance();

    public static void configUuidGenerator(int workerId) {
        uuid = new MsgUuidGenerator(workerId);
    }

    /**
     * 转换成低频传感器数据集合
     * @param lfPayLoads
     * @return
     */
    public static List<MetaSensor> toMetaSensors(List<RequestProto.LfData.LfPayLoad> lfPayLoads) {
        if (lfPayLoads==null) {
            return Collections.emptyList();
        }
        List<MetaSensor> sensors = new ArrayList<>();
        for (RequestProto.LfData.LfPayLoad lfPayLoad : lfPayLoads) {
            String itemId = getStringValue(lfPayLoad.getItemId());
            Double value = getDoubleValue(lfPayLoad.getValue());
            Long time = getLongValue(lfPayLoad.getTime());
            sensors.add(new MetaSensor(itemId,time,value));
        }
        return sensors;
    }

    /**
     * 低频传感数据转换成Protobuf传输协议
     * @param eventType
     * @param sensors
     * @return
     */
    public static MessageProto.Message toProtoLfMessage(int eventType, List<MetaSensor> sensors) {

        if (ObjectUtil.isEmpty(sensors)) {
            throw new NullPointerException("sensors is empty");
        }
        RequestProto.LfData.Builder lfBuilder = RequestProto.LfData.newBuilder();
        lfBuilder.setEventType(setIntValue(eventType));

        for (MetaSensor sensor : sensors) {
            RequestProto.LfData.LfPayLoad.Builder lfPayloadBuilder = RequestProto.LfData.LfPayLoad.newBuilder();
            lfPayloadBuilder.setItemId(setStringValue(sensor.getItem_id()))
                    .setValue(setDoubleValue(sensor.getValue())).setTime(setLongValue(sensor.getTime()));
            if (sensor.getExtraInfo() != null && sensor.getExtraInfo().size() > 0) {
                lfPayloadBuilder.putAllExtraInfo(sensor.getExtraInfo());
            }
            lfBuilder.addData(lfPayloadBuilder.build());
        }

        MessageProto.Message.Builder mdBuilder = MessageProto.Message.newBuilder();
        MessageProto.Message protoLfData = mdBuilder.setLfData(lfBuilder.build())
                .setMode(ProtocolUrl.REQUEST_MODE).setProtocol(ProtocolUrl.PATH_LF_DATA)
                .setUuid(uuid.nextId()).build();

        return protoLfData;
    }


    /**
     * 转换成高频数据载体
     *
     * @param data
     * @return
     */
    public static HfPayload toMetaHfPayload(RequestProto.HfData.HfPayload data) {
        HfPayload payload = new HfPayload();
        payload.setItemId(getStringValue(data.getItemId())) ;
        payload.setValue(data.getValueList());
        Long startTime = getLongValue(data.getStartTime());
        Long endTime = getLongValue(data.getEndTime());
        if (startTime != null) {
            payload.setStartTime(new Date(startTime));
        }
        if (endTime != null) {
            payload.setEndTime(new Date(endTime));
        }
        payload.setMaximum(getDoubleValue(data.getMaximum()));
        payload.setMinimum(getDoubleValue(data.getMinimum()));
        payload.setFrequency(getIntValue(data.getFrequency()));
        return payload;
    }

    /**
     * 转换成高频传感传输协议
     * @param eventType
     * @param subType
     * @param payload
     * @return
     */
    public static MessageProto.Message toProtoHfMessage( int eventType, int subType, HfPayload payload) {

        if (payload.getValue() == null |payload.getValue().size() == 0) {
            throw new NullPointerException("value is empty");
        }

        RequestProto.HfData.HfPayload.Builder hfPayloadBuilder = RequestProto.HfData.HfPayload.newBuilder();
        hfPayloadBuilder.setItemId(setStringValue(payload.getItemId()))
                .setFrequency(setIntValue(payload.getFrequency()))
                .setStartTime(setLongValue(payload.getStartTime().getTime()))
                .setEndTime(setLongValue(payload.getEndTime().getTime()))
                .addAllValue(payload.getValue())
                .setMaximum(setDoubleValue(payload.getMaximum()))
                .setMinimum(setDoubleValue(payload.getMinimum()));
        if (payload.getExtraInfo() != null && payload.getExtraInfo().size() > 0) {
            hfPayloadBuilder.putAllExtraInfo(payload.getExtraInfo());
        }

        RequestProto.HfData.Builder hfBuilder = RequestProto.HfData.newBuilder();
        RequestProto.HfData hfData = hfBuilder.setEventType(setIntValue(eventType))
                .setSubType(setIntValue(subType))
                .setData(hfPayloadBuilder.build())
                .build();

        MessageProto.Message.Builder msgBuilder = MessageProto.Message.newBuilder();
        return msgBuilder.setMode(ProtocolUrl.REQUEST_MODE).setProtocol(ProtocolUrl.PATH_HF_DATA)
                .setUuid(uuid.nextId())
                .setHfData(hfData)
                .build();
    }


    private static String setStringValue(String value) {
        if (StringUtils.isBlank(value)) {
            return NullDataFlag.INVALID_STRING;
        } else {
            return value;
        }
    }

    public static Integer setIntValue(Integer value) {
        return value == null ? NullDataFlag.INVALID_INT : value;
    }

    public static Double setDoubleValue(Double value) {
        return value == null ? NullDataFlag.INVALID_DOUBLE : value;
    }

    public static Long setLongValue(Long value) {
        return value == null ? NullDataFlag.INVALID_LONG : value;
    }

    public static Integer getIntValue(int value) {
        return value == NullDataFlag.INVALID_INT ? null : value;
    }

    public static Long getLongValue(long value) {
        return value == NullDataFlag.INVALID_LONG ? null : value;
    }

    public static String getStringValue(String value) {
        return value.equals(NullDataFlag.INVALID_STRING) ? null : value;
    }

    public static Double getDoubleValue(double value) {
        return value == NullDataFlag.INVALID_DOUBLE ? null : value;
    }



}
