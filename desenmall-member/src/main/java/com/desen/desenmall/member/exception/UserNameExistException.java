package com.desen.desenmall.member.exception;

/**
 * 尝试使用异常机制来做业务判断
 */
public class UserNameExistException extends RuntimeException {
    public UserNameExistException() {
        super("用户名存在");
    }
}
