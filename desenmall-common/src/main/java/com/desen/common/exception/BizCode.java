package com.desen.common.exception;

/**
 @Description 错误吗和错误信息定义,5位数字
              前两位表示业务场景，后三位表示错误码。如：10001
              错误码列表：
                        10：通用
                           001：请求参数不正确
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
    VALID_ECCEPTION(10001,"请求参数不正确"),
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
