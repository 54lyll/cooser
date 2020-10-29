package com.wonderzh.cooser.protocol;

import org.apache.commons.lang.StringUtils;

/**
 * 协议唯一资源定位符
 * @Author: wonderzh
 * @Date: 2020/8/18
 * @Version: 1.0
 */

public class ProtocolUrl {

    /**
     *协议方法：请求
     */
    public static final String REQUEST_MODE = MessageMode.REQUEST.getTag();

    /**
     * 协议方法：响应
     */
    public static final String RESPONSE_MODE = MessageMode.RESPONSE.getTag();
    /**
     * 协议方法：心跳
     */
    public static final String HEART_MODE = MessageMode.HEART.getTag();
    /**
     * 协议方法：签名
     */
    public static final String SIGN_MODE = MessageMode.SIGN.getTag();

    /**
     * 协议头分隔符
     */
    public static final String PROTOCOL_SEPARATOR = "://";

    /**
     * 心跳
     */
    public static final String PATH_PING = "/PING";

    /**
     * 低频数据
     */
    public static final String PATH_LF_DATA = "/dw/lf";
    /**
     * 高频数据
     */
    public static final String PATH_HF_DATA = "/dw/hf";
    /**
     * 命令
     */
    public static final String PATH_COMMAND = "/command";
    /**
     * 默认响应头
     */
    public static final String PATH_DEFAULT_RESPONSE = "/response";


    public static boolean isHeart(String mode) {
        if (StringUtils.isBlank(mode)) {
            return false;
        }
        return HEART_MODE.equals(mode);
    }

    public static boolean isSign(String mode) {
        if (StringUtils.isBlank(mode)) {
            return false;
        }
        return SIGN_MODE.equals(mode);
    }


    public enum MessageMode {

        REQUEST("request"),
        RESPONSE("response"),
        HEART("heart"),
        SIGN("sign");

        private String tag;

        MessageMode(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }

}
