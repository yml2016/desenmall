package com.desen.desenmall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.desen.common.utils.http.HttpUtils;
import com.desen.desenmall.member.dao.MemberLevelDao;
import com.desen.desenmall.member.entity.MemberLevelEntity;
import com.desen.desenmall.member.exception.PhoneExistException;
import com.desen.desenmall.member.exception.UserNameExistException;
import com.desen.desenmall.member.vo.GitUser;
import com.desen.desenmall.member.vo.MemberLoginVo;
import com.desen.desenmall.member.vo.SocialUser;
import com.desen.desenmall.member.vo.UserRegisterVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.desen.common.utils.PageUtils;
import com.desen.common.utils.Query;

import com.desen.desenmall.member.dao.MemberDao;
import com.desen.desenmall.member.entity.MemberEntity;
import com.desen.desenmall.member.service.MemberService;

import javax.annotation.Resource;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Resource
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public void register(UserRegisterVo userRegisterVo) throws PhoneExistException, UserNameExistException {

        MemberEntity entity = new MemberEntity();
        // ??????????????????
        MemberLevelEntity memberLevelEntity = memberLevelDao.selectOne(
                new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
        entity.setLevelId(memberLevelEntity.getId());

        // ??????????????? ?????????????????????
        checkPhone(userRegisterVo.getPhone());
        checkUserName(userRegisterVo.getUserName());

        entity.setMobile(userRegisterVo.getPhone());
        entity.setUsername(userRegisterVo.getUserName());

        //String md5Hex = DigestUtils.md5Hex("123456");//MD5??????
        //Md5Crypt.md5Crypt("123456".getBytes(),"$444");//????????????
        // ?????????????????????
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        entity.setPassword(bCryptPasswordEncoder.encode(userRegisterVo.getPassword()));
        // ?????????????????????
        entity.setCity("???????????? ");
        entity.setCreateTime(new Date());
        entity.setStatus(0);
        entity.setNickname(userRegisterVo.getUserName());
        entity.setBirth(new Date());
        entity.setEmail("xxx@gmail.com");
        entity.setGender(1);
        entity.setJob("JAVA");
        baseMapper.insert(entity);
    }

    @Override
    public void checkPhone(String phone) throws PhoneExistException {
        if (this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone)) > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUserName(String username) throws UserNameExistException {
        if (this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username)) > 0) {
            throw new UserNameExistException();
        }
    }


    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        // ??????????????????
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                        .eq("username", loginacct)
                        .or()
                        .eq("mobile", loginacct));
        if(entity == null){
            // ????????????
            return null;
        }else{
            // ??????????????????????????? ?????????????????????????????????
            boolean matches = bCryptPasswordEncoder.matches(vo.getPassword(), entity.getPassword());
            if (matches){
                entity.setPassword(null);
                return entity;
            }else {
                return null;
            }
        }
    }


    /**
     * sign in by weibo
     * @param socialUser
     * @return
     */
    @Override
    public MemberEntity login(SocialUser socialUser) {

        // ?????????uid
        String uid = socialUser.getUid();
        // 1.?????????????????????????????????
        MemberDao dao = this.baseMapper;
        MemberEntity entity = dao.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));

        MemberEntity memberEntity = new MemberEntity();
        if(entity != null){
            // ???????????????????????????, ??????????????????
            memberEntity.setId(entity.getId());
            memberEntity.setAccessToken(socialUser.getAccessToken());
            memberEntity.setExpiresIn(socialUser.getExpiresIn());
            // ??????
            dao.updateById(memberEntity);
            entity.setAccessToken(socialUser.getAccessToken());
            entity.setExpiresIn(socialUser.getExpiresIn());
            entity.setPassword(null);
            return entity;
        }else{
            // 2. ????????????????????????????????????????????? ???????????????????????????
            HashMap<String, String> map = new HashMap<>();
            map.put("access_token", socialUser.getAccessToken());
            map.put("uid", socialUser.getUid());
            try {
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), map);
                // 3. ????????????????????????????????????(??????????????????)
                if(response.getStatusLine().getStatusCode() == 200){
                    // ????????????
                    String json = EntityUtils.toString(response.getEntity());
                    // ??????JSON?????????????????????????????????????????????
                    JSONObject jsonObject = JSON.parseObject(json);
                    memberEntity.setNickname(jsonObject.getString("name"));
                    memberEntity.setUsername(jsonObject.getString("name"));
                    memberEntity.setGender("m".equals(jsonObject.getString("gender"))?1:0);
                    memberEntity.setCity(jsonObject.getString("location"));
                    memberEntity.setJob("?????????");
                    memberEntity.setEmail(jsonObject.getString("email"));
                }
            } catch (Exception e) {
                log.warn("????????????????????????????????? [????????????]");
            }
            memberEntity.setStatus(0);
            memberEntity.setCreateTime(new Date());
            memberEntity.setBirth(new Date());
            memberEntity.setLevelId(1L);
            memberEntity.setSocialUid(socialUser.getUid());
            memberEntity.setAccessToken(socialUser.getAccessToken());
            memberEntity.setExpiresIn(socialUser.getExpiresIn());

            // ?????? -- ????????????
            dao.insert(memberEntity);
            memberEntity.setPassword(null);
            return memberEntity;
        }
    }



    /**
     * sign in by git
     * @param gitUser
     * @return
     */
    @Override
    public MemberEntity login(GitUser gitUser) {

        // git's uid
        Integer uid = gitUser.getId();
        // 1.?????????????????????????????????
        MemberDao dao = this.baseMapper;
        MemberEntity entity = dao.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));

        MemberEntity memberEntity = new MemberEntity();
        if(entity != null){
            // ???????????????????????????, ??????????????????
            memberEntity.setId(entity.getId());
            memberEntity.setAccessToken(gitUser.getAccessToken());

            // ??????
            dao.updateById(memberEntity);
            entity.setAccessToken(gitUser.getAccessToken());
            entity.setPassword(null);
            return entity;
        }else{

            memberEntity.setNickname(gitUser.getName());
            memberEntity.setUsername(gitUser.getLogin());
            memberEntity.setCity(gitUser.getLocation());
            memberEntity.setJob(gitUser.getCompany());
            memberEntity.setEmail(gitUser.getEmail());
            memberEntity.setHeader(gitUser.getAvatarUrl());
            memberEntity.setStatus(0);
            memberEntity.setCreateTime(new Date());
            memberEntity.setBirth(new Date());
            memberEntity.setLevelId(1L);
            memberEntity.setSign(gitUser.getBio());
            memberEntity.setSocialUid(String.valueOf(gitUser.getId()));
            memberEntity.setAccessToken(gitUser.getAccessToken());
            //memberEntity.setExpiresIn(socialUser.getExpiresIn());
            // ?????? -- ????????????
            dao.insert(memberEntity);
            memberEntity.setPassword(null);
            return memberEntity;
        }
    }

}