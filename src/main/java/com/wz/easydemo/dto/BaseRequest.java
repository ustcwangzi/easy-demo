package com.wz.easydemo.dto;

import lombok.Data;

@Data
public class BaseRequest {
    /**
     * @see com.wz.easydemo.annotation.ApiEnum
     */
    private String routerCode;
}
