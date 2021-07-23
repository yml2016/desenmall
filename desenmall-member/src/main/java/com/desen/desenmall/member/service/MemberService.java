package com.desen.desenmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.desen.common.utils.PageUtils;
import com.desen.desenmall.member.entity.MemberEntity;
import com.desen.desenmall.member.exception.PhoneExistException;
import com.desen.desenmall.member.exception.UserNameExistException;
import com.desen.desenmall.member.vo.GitUser;
import com.desen.desenmall.member.vo.MemberLoginVo;
import com.desen.desenmall.member.vo.SocialUser;
import com.desen.desenmall.member.vo.UserRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-04-11 13:21:13
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(UserRegisterVo userRegisterVo);

    void checkPhone(String phone) throws PhoneExistException;

    void checkUserName(String username) throws UserNameExistException;


    /**
     * 普通登录
     */
    MemberEntity login(MemberLoginVo vo);

    /**
     * 社交登录
     */
    MemberEntity login(SocialUser socialUser);
    MemberEntity login(GitUser gitUser);
}

