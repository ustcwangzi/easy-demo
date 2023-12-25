package com.wz.easydemo;

import com.google.gson.Gson;
import com.wz.easydemo.annotation.handler.ApiRouteHandlerProcessor;
import com.wz.easydemo.annotation.handler.ServiceHandler;
import com.wz.easydemo.dto.BaseRequest;
import com.wz.easydemo.dto.MyRequestV1;
import com.wz.easydemo.dto.MyRequestV2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Type;

@SpringBootTest
public class RouterTest {

    @Test
    public void test1() throws Exception {
        MyRequestV1 request = new MyRequestV1();
        request.setId(1);
        request.setName("test1");
        request.setRouterCode("test/one");

        ServiceHandler serviceHandler = ApiRouteHandlerProcessor.API_MAP.get(request.getRouterCode());
        Object object = new Gson().fromJson(new Gson().toJson(request), serviceHandler.getParameterType());
        Object resp = serviceHandler.getMethod().invoke(serviceHandler.getBean(), object);
        System.out.println(resp);
    }

    @Test
    public void test2() throws Exception {
        MyRequestV2 request = new MyRequestV2();
        request.setName("test2");
        request.setRouterCode("test/two");
        ServiceHandler serviceHandler = ApiRouteHandlerProcessor.API_MAP.get(request.getRouterCode());
        Object object = new Gson().fromJson(new Gson().toJson(request), serviceHandler.getParameterType());
        Object resp = serviceHandler.getMethod().invoke(serviceHandler.getBean(), object);
        System.out.println(resp);
    }
}
