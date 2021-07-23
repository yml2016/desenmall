package com.desen.desenmall.member.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.desen.common.exception.BizCode;
import com.desen.desenmall.member.exception.PhoneExistException;
import com.desen.desenmall.member.exception.UserNameExistException;
import com.desen.desenmall.member.feign.CouponFeignService;
import com.desen.desenmall.member.vo.GitUser;
import com.desen.desenmall.member.vo.MemberLoginVo;
import com.desen.desenmall.member.vo.SocialUser;
import com.desen.desenmall.member.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.desen.desenmall.member.entity.MemberEntity;
import com.desen.desenmall.member.service.MemberService;
import com.desen.common.utils.PageUtils;
import com.desen.common.utils.R;



/**
 * 会员
 *
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-04-11 13:21:13
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    CouponFeignService couponFeignService;


    @PostMapping("/oauth2/login")
    public R login(@RequestBody SocialUser socialUser){

        MemberEntity memberEntity = memberService.login(socialUser);
        if(memberEntity != null){
            return R.ok().setData(memberEntity);
        }else {
            return R.error(BizCode.SOCIALUSER_LOGIN_ERROR);
        }
    }

    @PostMapping("/oauth2/git/login")
    public R login(@RequestBody GitUser gitUser){

        MemberEntity memberEntity = memberService.login(gitUser);
        if(memberEntity != null){
            return R.ok().setData(memberEntity);
        }else {
            return R.error(BizCode.SOCIALUSER_LOGIN_ERROR);
        }
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo){

        MemberEntity memberEntity = memberService.login(vo);
        if(memberEntity != null){
            return R.ok().setData(memberEntity);
        }else {
            return R.error(BizCode.LOGINACTT_PASSWORD_ERROR);
        }
    }

    @PostMapping("/register")
    public R register(@RequestBody UserRegisterVo userRegisterVo){

        try {
            memberService.register(userRegisterVo);
        } catch (PhoneExistException e) {
            return R.error(BizCode.PHONE_EXIST_EXCEPTION);
        } catch (UserNameExistException e) {
            return R.error(BizCode.USER_EXIST_EXCEPTION );
        }
        return R.ok();
    }


    @RequestMapping("/coupons")
    public R test(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");

        R membercoupons = couponFeignService.membercoupons();
        return R.ok().put("member",memberEntity).put("coupons",membercoupons.get("coupons"));
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
