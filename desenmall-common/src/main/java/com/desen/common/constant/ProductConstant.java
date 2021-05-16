package com.desen.common.constant;

public class ProductConstant {

    public enum AttrEnum{
        ATTR_TYPE_BASE(1,"基本属性"),
        ATTR_TYPE_SALE(0,"销售属性");
        private int code;
        private String msg;
        AttrEnum(int code,String msg){
            this.code=code;
            this.msg=msg;
        }

        public int getCode() {
            return code;
        }

        public AttrEnum setCode(int code) {
            this.code = code;
            return this;
        }

        public String getMsg() {
            return msg;
        }

        public AttrEnum setMsg(String msg) {
            this.msg = msg;
            return this;
        }
    }

}
