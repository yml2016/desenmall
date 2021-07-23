package com.desen.desenmall.auth.feign;

import com.desen.common.utils.R;
import com.desen.desenmall.auth.vo.GitUser;
import com.desen.desenmall.auth.vo.SocialUser;
import com.desen.desenmall.auth.vo.UserLoginVo;
import com.desen.desenmall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("desenmall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVo userRegisterVo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
    R login(@RequestBody SocialUser socialUser);
    @PostMapping("/member/member/oauth2/git/login")
    R login(@RequestBody GitUser gitUser);
}