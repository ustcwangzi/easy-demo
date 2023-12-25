package com.wz.easydemo.annotation;

import lombok.Getter;

@Getter
public enum ApiEnum {
    TEST_ONE("test/one", "测试一"),
    TEST_TWO("test/two", "测试二"),

    ;

    private final String code;

    private final String desc;

    ApiEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
