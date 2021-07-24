package com.desen.desenmall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.desen.common.constant.AuthServerConstant;
import com.desen.common.utils.R;
import com.desen.common.utils.http.HttpUtils;
import com.desen.common.vo.MemberRsepVo;
import com.desen.desenmall.auth.constant.UrlConstant;
import com.desen.desenmall.auth.feign.MemberFeignService;
import com.desen.desenmall.auth.vo.GitToken;
import com.desen.desenmall.auth.vo.GitUser;
import com.desen.desenmall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/oauth2")
public class Oauth2Controller {

    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/logout")
    public String login(HttpSession session){
        if(session.getAttribute(AuthServerConstant.LOGIN_USER) != null){
            log.info("[" + ((MemberRsepVo)session.getAttribute(AuthServerConstant.LOGIN_USER)).getUsername() + "] 已下线");
        }
        session.invalidate();
        return UrlConstant.REDIRECT_LOGIN_URL;
    }

    /**
     * 登录成功回调,（weibo登录）
     * {
     *     "access_token": "2.00b5w4HGbwxc6B0e3d62c666DlN1DD",
     *     "remind_in": "157679999",
     *     "expires_in": 157679999,
     *     "uid": "5605937365",
     *     "isRealName": "true"
     * }
     * 
     */
    @GetMapping("/weibo/success")
    public String weiBo(@RequestParam("code") String code, HttpSession session) throws Exception {

        // 根据code换取 Access Token
        Map<String,String> map = new HashMap<>();
        map.put("client_id", "1294828100");
        map.put("client_secret", "a8e8900e15fba6077591cdfa3105af44");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.desenmall.com/oauth2/weibo/success");
        map.put("code", code);
        Map<String, String> headers = new HashMap<>();
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", headers, null, map);
        if(response.getStatusLine().getStatusCode() == 200){
            // 获取到了 Access Token
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

            // 相当于我们知道了当前是那个用户
            // 1.如果用户是第一次进来 自动注册进来(为当前社交用户生成一个会员信息 以后这个账户就会关联这个账号)
            R login = memberFeignService.login(socialUser);
            if(login.getCode() == 0){
                MemberRsepVo rsepVo = login.getData("data" ,new TypeReference<MemberRsepVo>() {});

                log.info("欢迎 [" + rsepVo.getUsername() + "] 使用社交账号登录");
                // 第一次使用session 命令浏览器保存这个用户信息 JESSIONSEID 每次只要访问这个网站就会带上这个cookie
                // 在发卡的时候扩大session作用域 (指定域名为父域名)
                // TODO 1.默认发的当前域的session (需要解决子域session共享问题)
                // TODO 2.使用JSON的方式序列化到redis
//				new Cookie("JSESSIONID","").setDomain("desenmall.com");
                session.setAttribute(AuthServerConstant.LOGIN_USER, rsepVo);
                // 登录成功 跳回首页
                return "redirect:http://desenmall.com";
            }else{
                return "redirect:http://auth.desenmall.com/login.html";
            }
        }else{
            return "redirect:http://auth.desenmall.com/login.html";
        }
    }


    @GetMapping("/git/success")
    public String gitLogin(@RequestParam("code") String code, HttpSession session) throws Exception {
        log.debug("响应的git授权码[{}]", code);

        /**
         * 根据code换取 Access Token
         * https://github.com/login/oauth/access_token?client_id=5002ec1d6c1be028d6f0&client_secret=5f2b719f47b718dc329eca39efdc6982d6592595&code=d3d174415df29f10dc6c
         */
        Map<String,String> querys = new HashMap<>();
        querys.put("client_id", "5002ec1d6c1be028d6f0");
        querys.put("client_secret", "5f2b719f47b718dc329eca39efdc6982d6592595");
        querys.put("redirect_uri", "http://auth.desenmall.com/oauth2/git/success");
        querys.put("code", code);
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept","application/json;charset=UTF-8");
        Map<String, String> bodys = new HashMap<>();
        HttpResponse tokenResp = HttpUtils.doPost(
                "https://github.com", "/login/oauth/access_token", "post", headers, querys, bodys);
        log.debug("响应response->{}", tokenResp);
        if(tokenResp.getStatusLine().getStatusCode() == 200){
            String json = EntityUtils.toString(tokenResp.getEntity());
            log.debug("响应entity->{}", json);
            GitToken token = JSON.parseObject(json, GitToken.class);
            log.debug("响应entity.token->{}", token.getAccessToken());
            Map<String, String> userHeaders = new HashMap<>();
            userHeaders.put("Authorization","Bearer "+token.getAccessToken());//Bearer 后面一定要有个空格！！
            HttpResponse userResp = HttpUtils.doGet(
                    "https://api.github.com", "/user", "get", userHeaders, null);
            log.debug("响应response.user->{}", tokenResp);
            if(userResp.getStatusLine().getStatusCode()==200){
                String userJson = EntityUtils.toString(userResp.getEntity());
                log.debug("响应entity.user->{}", userJson);
                GitUser gitUser = JSON.parseObject(userJson, GitUser.class);
                log.debug("响应entity.user.login->{}", gitUser.getLogin());
                gitUser.setAccessToken(token.getAccessToken());
                R login = memberFeignService.login(gitUser);
                if(login.getCode() == 0){
                    MemberRsepVo rsepVo = login.getData("data" ,new TypeReference<MemberRsepVo>() {});
                    log.info("欢迎 [" + rsepVo.getUsername() + "] 使用社交账号登录");
                    session.setAttribute(AuthServerConstant.LOGIN_USER, rsepVo);
                    // 登录成功 跳回首页
                    return UrlConstant.REDIRECT_INDEX_URL;
                }
            }
        }
        return UrlConstant.REDIRECT_LOGIN_URL;
    }


}