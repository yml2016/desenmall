package com.desen.desenmall.member.exception;

/**
 * 尝试使用异常机制来做业务判断
 */
public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("手机号存在");
    }
}
