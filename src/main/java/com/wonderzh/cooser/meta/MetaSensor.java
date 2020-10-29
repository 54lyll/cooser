package com.wonderzh.cooser.meta;

import lombok.Data;

import java.util.Map;

/**
 * 采集量
 * @Author: wonderzh
 * @Date: 2019/8/22
 * @Version: 1.0
 */
@Data
public class MetaSensor extends MetaData implements Comparable {

    private Map<String, String> extraInfo;


    public MetaSensor() {
    }

    public MetaSensor(String itemId, Long time, Double value) {
        super();
        this.item_id = itemId;
        this.time = time;
        this.value = value;
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * 按时间大小排序
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        MetaSensor other;
        if (o instanceof MetaSensor) {
            other = (MetaSensor) o;
        } else {
            throw new ClassCastException();
        }
        return getTime().compareTo(other.getTime());
    }

    @Override
    public String toString() {
        return "MetaSensor{" +
                "extraInfo=" + extraInfo +
                ", obj_id='" + obj_id + '\'' +
                ", item_id='" + item_id + '\'' +
                ", value=" + value +
                ", time=" + time +
                ", type=" + type +
                ", siteId=" + siteId +
                ", siteName='" + siteName + '\'' +
                '}';
    }
}
