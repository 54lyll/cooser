package com.wonderzh.cooser.tool;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wonderZH
 * @Date: 2018/7/23 15:11
 * @Description: Jackson转换工具
 * @Modify:
 */
public class JacksonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        //Include.Include.ALWAYS 默认
        //Include.NON_EMPTY 属性为 空（“”） 或者为 NULL 都不序列化
        //IInclude.NON_NULL 属性为NULL 不序列化
        // Include.NON_DEFAULT属性为默认值不序列化（注意0不被打印）
        //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 取消时间的转化格式,默认是时间戳,可以取消,同时需要设置要表现的时间格式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 忽略空bean转json的错误,不抛异常
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 忽略在json字符串中存在,但是在java对象中不存在对应属性的情况，即反序列化的时候多了其他属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //单引号处理
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }
    
    /**
     * Object转json字符串
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String objectToJson(T obj) {
        if (obj == null) {
            return null;
        }
        //判断参数是否是String,再进行转换
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Object转json字符串并格式化美化
     * {
     * "age" : 29,
     * "messages" : [ "msg 1", "msg 2", "msg 3" ],
     * "name" : "mkyong"
     * }
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String objToJsonPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            System.out.println("Parse object to String error");
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * jsoning转object
     *
     * @param json  json字符串
     * @param clazz 被转对象class
     * @param <T>
     * @return
     */
    public static <T> T jsonToObj(String json, Class<T> clazz) throws IOException {
        if (StringUtils.isEmpty(json) || clazz == null) {
            return null;
        }
            return clazz.equals(String.class) ? (T) json : objectMapper.readValue(json, clazz);
    }
    
    /**
     * string转object，用于指定object类型
     *
     * @param json          json字符串
     * @param typeReference 被转对象引用类型
     * @param <T>
     * @return
     */
    public static <T> T jsonToCollection(String json, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(json) || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? json : objectMapper.readValue(json, typeReference));
        } catch (IOException e) {
            System.out.println("Parse String to Object error");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * json转Map<String ,Object>
     *
     * @param json
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Map<String, Object> jsonToMap(String json) throws IOException {
        return objectMapper.readValue(json, Map.class);
    }
    
    /**
     * json转Map<String ,T>
     *
     * @param jsonStr
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Map<String, T> jsonTomap(String jsonStr, Class<T> clazz) throws Exception {
        
        Map<String, Map<String, Object>> map = objectMapper.readValue(jsonStr,
                new TypeReference<Map<String, T>>() {
                });
        
        Map<String, T> result = new HashMap<String, T>();
        for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            result.put(entry.getKey(), mapToPojo(entry.getValue(), clazz));
        }
        
        return result;
        
    }
    
    
    public static <T> List<T> jsonToList(String json, Class<T> clazz) throws Exception {

        JavaType javaType= objectMapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
        List<T> lst = (List<T>) objectMapper.readValue(json, javaType);
        //List<T> lst=objectMapper.readValue(json, new TypeReference<List<T>>() {
        //});
        return lst;
    }
    
    
    public static <T> T mapToPojo(Map map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }
    
    
}
