package com.wz.easydemo.dto;

import lombok.Data;

@Data
public class BaseResponse {
    private int code;
    private String message;

    public BaseResponse() {
    }

    public BaseResponse(int code) {
        this.code = code;
    }
}
