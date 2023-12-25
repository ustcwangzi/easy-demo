package com.wz.easydemo.annotation.handler;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class ServiceHandler {

    /**
     * 请求处理BEAN
     */
    private Object bean;

    /**
     * 请求处理方法
     */
    private Method method;

    /**
     * 方法描述
     */
    private String apiDesc;

    private Class<?> parameterType;
}
