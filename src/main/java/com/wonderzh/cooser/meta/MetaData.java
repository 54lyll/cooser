package com.wonderzh.cooser.meta;

/**
 * 接入量元数据
 *
 * @Author: wonderzh
 * @Date: 2019/8/15
 * @Version: 1.0
 */

public class MetaData {

    /**
     * 被监控对象id
     */
    protected String obj_id;
    /**
     * 监控量id
     */
    protected String item_id;
    /**
     * 量的值
     */
    protected Double value;
    /**
     * 量的值的上报时间
     */
    protected Long time;
    /**
     * 量的类型
     */
    protected Byte type;

    protected Integer siteId;

    protected String siteName;

    @Override
    public int hashCode() {
        int h = 17;
        h = h * 31 + (item_id == null ? 0 : item_id.hashCode());
        h = h * 31 + (time == null ? 0 : time.hashCode());
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof MetaData) {
            MetaData other = (MetaData) obj;
            if (item_id.equals(other.getItem_id()) && time.equals(other.time)) {
                return true;
            }
        }
        return false;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getObj_id() {
        return obj_id;
    }

    public void setObj_id(String obj_id) {
        this.obj_id = obj_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }
}
