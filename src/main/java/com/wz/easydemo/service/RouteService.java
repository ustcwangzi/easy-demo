package com.wz.easydemo.service;

import com.wz.easydemo.annotation.ApiEnum;
import com.wz.easydemo.annotation.ApiRouter;
import com.wz.easydemo.dto.BaseResponse;
import com.wz.easydemo.dto.MyRequestV1;
import com.wz.easydemo.dto.MyRequestV2;
import org.springframework.stereotype.Service;

@Service
public class RouteService {

    @ApiRouter(ApiEnum.TEST_ONE)
    public BaseResponse test(MyRequestV1 request) {
        return new BaseResponse(1);
    }

    @ApiRouter(ApiEnum.TEST_TWO)
    public BaseResponse test(MyRequestV2 request) {
        return new BaseResponse(2);
    }
}
