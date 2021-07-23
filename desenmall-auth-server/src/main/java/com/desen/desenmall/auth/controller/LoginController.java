package com.desen.desenmall.auth.controller;


import com.alibaba.fastjson.TypeReference;
import com.desen.common.constant.AuthServerConstant;
import com.desen.common.exception.BizCode;
import com.desen.common.utils.R;
import com.desen.common.vo.MemberRsepVo;
import com.desen.desenmall.auth.constant.UrlConstant;
import com.desen.desenmall.auth.feign.MemberFeignService;
import com.desen.desenmall.auth.feign.ThirdPartFeignService;
import com.desen.desenmall.auth.vo.UserLoginVo;
import com.desen.desenmall.auth.vo.UserRegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    ThirdPartFeignService thirdPartFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MemberFeignService memberFeignService;

    //这个地方应该用拦截器或过滤器来做
    @GetMapping({"/login.html","/","/index","/index.html"})
    public String loginPage(HttpSession session){
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(attribute == null){
            return "login";
        }
        return UrlConstant.REDIRECT_INDEX_URL;
    }

    @PostMapping("/login")
    public String login(UserLoginVo userLoginVo, RedirectAttributes redirectAttributes, HttpSession session){
        // 远程登录
        R r = memberFeignService.login(userLoginVo);
        if(r.getCode() == 0){
            // 登录成功
            MemberRsepVo rsepVo = r.getData("data", new TypeReference<MemberRsepVo>() {});
            log.debug("登录成功-->{}", rsepVo);
            session.setAttribute(AuthServerConstant.LOGIN_USER, rsepVo);
            log.info("\n欢迎 [" + rsepVo.getUsername() + "] 登录");
            return UrlConstant.REDIRECT_INDEX_URL;
        }else {
            HashMap<String, String> error = new HashMap<>();
            // 获取错误信息
            error.put("msg", r.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors", error);
            return UrlConstant.REDIRECT_LOGIN_URL;
        }
    }


    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
        //1.todo 接口防刷
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String redisCode = ops.get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (StringUtils.isNotEmpty(redisCode)) {
            String[] codeArr = redisCode.split("_");
            if (System.currentTimeMillis() - Long.parseLong(codeArr[1]) < 60 * 1000) {
                //60秒内不能再次发送
                return R.error(BizCode.SMS_CODE_EXCEPTION);
            }
        }
        String code = UUID.randomUUID().toString().substring(0, 5);
        //2.用于验证码的再校验，格式: sms:code:phone, code
        ops.set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, code + "_" + System.currentTimeMillis(), 300, TimeUnit.SECONDS);

        try {
            thirdPartFeignService.sendCode(phone, code);
        } catch (Exception e) {
            log.warn("远程调用不知名错误 [无需解决]", e);
        }
        return R.ok();
    }

    /**
     * TODO 重定向携带数据,利用session原理 将数据放在sessoin中 取一次之后删掉
     *
     * TODO 1. 分布式下的session问题
     * 校验
     * RedirectAttributes redirectAttributes ： 模拟重定向带上数据
     */
    @PostMapping("/register")
    public String register(@Valid UserRegisterVo vo, BindingResult result, RedirectAttributes redirectAttributes){

        if(result.hasErrors()){
            // 将错误属性与错误信息一一封装
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, fieldError -> fieldError.getDefaultMessage()));
            // addFlashAttribute 这个数据只取一次
            redirectAttributes.addFlashAttribute("errors", errors);
            return UrlConstant.REDIRECT_REG_URL;
        }
        // 开始注册 调用远程服务
        // 1.校验验证码
        String code = vo.getCode();

        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if(!StringUtils.isEmpty(redisCode)){
            // 验证码通过
            if(code.equals(redisCode.split("_")[0])){
                // 删除验证码
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                // 调用远程服务进行注册
                R r = memberFeignService.register(vo);
                if(r.getCode() == 0){
                    // 成功
                    return UrlConstant.REDIRECT_LOGIN_URL;
                }else{
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors",errors);
                    return UrlConstant.REDIRECT_REG_URL;
                }
            }else{
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                // addFlashAttribute 这个数据只取一次
                redirectAttributes.addFlashAttribute("errors", errors);
                return UrlConstant.REDIRECT_REG_URL;
            }
        }else{
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码已失效");
            // addFlashAttribute 这个数据只取一次
            redirectAttributes.addFlashAttribute("errors", errors);
            return UrlConstant.REDIRECT_REG_URL;
        }
    }

}
