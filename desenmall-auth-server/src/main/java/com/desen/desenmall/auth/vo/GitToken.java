package com.desen.desenmall.auth.vo;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class GitToken {

    //@JSONField(name="access_token")//fastjson默认支持下划线转驼峰
    private String accessToken;

    private String scope;
    private String tokenType;
    private String error;
    private String errorDescription;

}
