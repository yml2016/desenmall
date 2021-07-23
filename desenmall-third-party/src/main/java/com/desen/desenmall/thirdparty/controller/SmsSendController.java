package com.desen.desenmall.thirdparty.controller;

import com.desen.common.utils.R;
import com.desen.desenmall.thirdparty.component.SmsComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sms")
public class SmsSendController {

    @Autowired
    SmsComponent smsComponent;

    /**
     * 提供给别人进行调用
     *
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        log.debug("发送短信-->手机号[{}],验证码[{}]", phone, code);
        smsComponent.sendSmsCode(phone, code);
        return R.ok();
    }


}
