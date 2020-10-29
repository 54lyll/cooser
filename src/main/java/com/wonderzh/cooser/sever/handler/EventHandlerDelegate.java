package com.wonderzh.cooser.sever.handler;

import com.wonderzh.cooser.exception.ComponentBeanInitializeException;
import com.wonderzh.cooser.exception.EventHandlerExecuteException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * EventHandler 执行代表
 *
 * @Author: wonderzh
 * @Date: 2020/9/8
 * @Version: 1.0
 */
@Slf4j
public class EventHandlerDelegate {

    private EventHandlerDefinition cd;

    public EventHandlerDelegate(EventHandlerDefinition handlerDe) {
        if (handlerDe == null) {
            throw new ComponentBeanInitializeException("EventHandlerDefinition must not be null");
        }
        this.cd = handlerDe;

    }

    /**
     * 执行Mapping method
     * 对形参进行匹配调用
     * @param params
     * @return
     */
    public Object doInvoke(Object... params) throws EventHandlerExecuteException {

        try {
            Class<?>[] parameterTypes = this.cd.getParameterTypes();
            //该方法没有形参
            if (parameterTypes == null || parameterTypes.length == 0) {
                return this.cd.getMethod().invoke(cd.getInstance());
            }
            Object[] invokeParams = new Object[parameterTypes.length];
            Map<Class<?>,Object> inputParamMap = convertToClassMap(params);
            //遍历形参找寻对应的实例参数
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterClass = parameterTypes[i];
                invokeParams[i] = inputParamMap.get(parameterClass);
            }
            return this.cd.getMethod().invoke(cd.getInstance(), invokeParams);
        } catch (Exception e) {
            log.error("{}.{} invoke exception: ", cd.getClassName(), cd.getMethod().getName());
            throw new EventHandlerExecuteException(e);
        }

    }

    private Map<Class<?>,Object> convertToClassMap(Object[] params) {
        Map<Class<?>, Object> classMap = new HashMap<>();
        for (Object param : params) {
            classMap.put(param.getClass(), param);
        }
        return classMap;
    }
}
