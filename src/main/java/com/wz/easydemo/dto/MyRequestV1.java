package com.wz.easydemo.dto;

import lombok.Data;

@Data
public class MyRequestV1 extends BaseRequest {
    private int id;
    private String name;
}
