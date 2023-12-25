package com.wz.easydemo.annotation.handler;

import com.wz.easydemo.annotation.ApiRouter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class ApiRouteHandlerProcessor implements BeanPostProcessor {

    public static final Map<String, ServiceHandler> API_MAP = new HashMap<>(128);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        for (Method method : methods) {
            ApiRouter routeRule = AnnotationUtils.findAnnotation(method, ApiRouter.class);
            if (Objects.isNull(routeRule)) {
                continue;
            }

            if (API_MAP.containsKey(routeRule.value().getCode())) {
                continue;
            }

            ServiceHandler serviceHandler = new ServiceHandler();
            serviceHandler.setBean(bean);
            serviceHandler.setMethod(method);
            serviceHandler.setParameterType(method.getParameterTypes()[0]);
            serviceHandler.setApiDesc(routeRule.value().getDesc());
            API_MAP.put(routeRule.value().getCode(), serviceHandler);
        }

        return bean;
    }

}
