package com.permission.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.permission.security.entity.SystemUser;
import com.permission.security.jwt.JwtTokenUtil;
import com.permission.utils.global.result.GlobalResultUtil;
import com.permission.utils.global.result.ResultEnum;
import com.permission.utils.http.HttpUtil;
import com.permission.utils.redis.RedisUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-28   *
 * * Time: 15:56        *
 * * to: lz&xm          *
 * **********************
 * 登陆成功处理
 **/
@Component
public class LoginAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SystemUser systemUser = (SystemUser) authentication.getPrincipal();
        //获取token
        String token = JwtTokenUtil.tokenPrefix + JwtTokenUtil.generateAccessToken(systemUser, HttpUtil.getIp(request));
        //保存token到redis中,用户名是唯一的作为key, 登出,修改权限等操作用;设置的过期时间为token的过期时间
        RedisUtil.set(systemUser.getUsername(), token, JwtTokenUtil.tokenExpireTime);
        //返回对象
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("tokenExpireTime", JwtTokenUtil.tokenExpireTime);
//        map.put("router", systemUser.getRouter());
        map.put("user", SystemUser.of(systemUser.getId(), systemUser.getUsername(), systemUser.getRoleId(), systemUser.getAccountLocked(), systemUser.getMenuList()));
        //为web-view保存信息
        RedisUtil.set("web-view" + systemUser.getUsername(), JSONObject.toJSONString(map), JwtTokenUtil.tokenExpireTime);
        //在此处拼接前端路由
        //登陆成功 返回的token包含前缀,如果不需要可以去掉
        GlobalResultUtil.out(response, GlobalResultUtil.success(ResultEnum.LOGIN_SUCCESS, map));
    }
}
