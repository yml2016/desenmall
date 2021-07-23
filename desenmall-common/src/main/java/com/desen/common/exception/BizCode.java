package com.desen.common.exception;

/**
 @Description 错误吗和错误信息定义,5位数字
              前两位表示业务场景，后三位表示错误码。如：10001
              错误码列表：
                        10：通用
                           001：请求参数不正确
                           002：验证码获取频率太高
                        11：商品
                        12：订单
                        13：会员
                        14：仓储
                        15：优惠
                        ...
 @see BizCode
 @author yangminglin
 @date 2021/5/4
 @version V
**/
public enum BizCode {

    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION(10001,"请求参数不正确"),
    SMS_CODE_EXCEPTION(10002, "验证码获取频率太高,稍后再试"),
    TO_MANY_REQUEST(10003, "请求流量过大"),
    SMS_SEND_CODE_EXCEPTION(10403, "短信发送失败"),

    USER_EXIST_EXCEPTION(15001, "用户已经存在"),
    PHONE_EXIST_EXCEPTION(15002, "手机号已经存在"),
    LOGINACTT_PASSWORD_ERROR(15003, "账号或密码错误"),
    SOCIALUSER_LOGIN_ERROR(15004, "社交账号登录失败"),

    NOT_STOCK_EXCEPTION(14000, "商品库存不足"),

    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),
    ;
    private int code;
    private String msg;

    BizCode(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
