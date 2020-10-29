package com.wonderzh.cooser.tool;

import com.wonderzh.cooser.exception.IllegalProtocolUrlException;
import com.wonderzh.cooser.protocol.ProtocolUrl;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * protocol url 解析器
 * @Author: wonderzh
 * @Date: 2020/8/18
 * @Version: 1.0
 */

public class ProtoUrlResolver {

    /**
     * 是否为响应Message
     * @param model
     * @return
     */
    public static boolean isResponse(String model) {
        if (StringUtils.isBlank(model)) {
            return false;
        }
        return ProtocolUrl.RESPONSE_MODE.equals(model.toLowerCase());
    }

    /**
     * 获取资源地址
     * @param protocol
     * @return
     */
    public static String getContextPath(String protocol) throws IllegalProtocolUrlException {
        if (protocol == null) {
            throw new NullPointerException();
        }
        if (protocol == "") {
            return protocol;
        }

        String[] items=protocol.split(ProtocolUrl.PROTOCOL_SEPARATOR);
        if (items.length < 2) {
            throw new IllegalProtocolUrlException("illegal url :" +protocol);
        }
        return items[1];
    }


    @Data
    private class UrlContext{

        private String header;
        private String context;

        public UrlContext() {
        }

        public UrlContext(String header, String context) {
            this.header = header;
            this.context = context;
        }
    }
}
