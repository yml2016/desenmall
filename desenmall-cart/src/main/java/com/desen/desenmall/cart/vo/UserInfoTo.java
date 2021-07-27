package com.desen.desenmall.cart.vo;

import lombok.Data;
import lombok.ToString;


@ToString
@Data
public class UserInfoTo {

    /**
     * 存储已登录用户在数据库中的ID
     */
    private Long userId;

    /**
     * 存储用户名
     */
    private String username;

    /**
     * 配分一个临时的user-key
     */
    private String userKey;

    /**
     * 判断是否是临时用户
     */
    private boolean tempUser = false;
}
