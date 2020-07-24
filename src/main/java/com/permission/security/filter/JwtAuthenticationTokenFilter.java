package com.permission.security.filter;

import com.alibaba.fastjson.JSONObject;
import com.permission.security.entity.SystemInterface;
import com.permission.security.exception.JwtIpException;
import com.permission.security.handler.JwtAuthenticationEntryPointHandler;
import com.permission.security.jwt.JwtTokenUtil;
import com.permission.utils.global.result.ResultEnum;
import com.permission.utils.http.HttpUtil;
import com.permission.utils.redis.RedisUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * token 验证
 */
public class JwtAuthenticationTokenFilter extends BasicAuthenticationFilter {

    private JwtAuthenticationEntryPointHandler jwtAuthenticationEntryPointHandler;

    public JwtAuthenticationTokenFilter(AuthenticationManager authenticationManager, JwtAuthenticationEntryPointHandler jwtAuthenticationEntryPointHandler) {
        super(authenticationManager, jwtAuthenticationEntryPointHandler);
        this.jwtAuthenticationEntryPointHandler = jwtAuthenticationEntryPointHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String accessToken = request.getHeader(JwtTokenUtil.tokenHeader);
            UsernamePasswordAuthenticationToken authentication = getAuthentication(accessToken, request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (JwtIpException e) {
            this.jwtAuthenticationEntryPointHandler.commence(request, response, e);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String accessToken, HttpServletRequest request) {
        if (!StringUtils.isEmpty(accessToken)) {
            //获取token中保存的数据
            Claims claims = JwtTokenUtil.getClaims(accessToken);
            //对比ip
            String userIp = claims.getId();
            if (!userIp.equals(HttpUtil.getIp(request))) { //ip异常处理
                throw new JwtIpException(ResultEnum.IP_ERROR);
            }
            //获取用户名
            String username = claims.getSubject();
            //redis 中没有该用户 拒绝访问
            if (!RedisUtil.hasKey(username)) {
                throw new JwtIpException(ResultEnum.PERMISSION_CHANGE);
            }
//            else { //验证token一致性
//                String token = (String) RedisUtil.get(username);
//                if (!accessToken.equals(token)) {
//                    throw new JwtIpException(ResultEnum.SESSION_ERROR);
//                }
//            }
            //获取权限（角色）
            List<GrantedAuthority> authorities = new ArrayList<>();
            String authority = claims.get(JwtTokenUtil.authHeader).toString();
            if (!StringUtils.isEmpty(authority)) {
                List<SystemInterface> systemMethodList = JSONObject.parseArray(authority, SystemInterface.class);
                for (SystemInterface role : systemMethodList) {
                    if (!StringUtils.isEmpty(role)) {
                        authorities.add(new SimpleGrantedAuthority(role.getUrl()));
                    }
                }
            }
            if (!StringUtils.isEmpty(username)) {
                //此处password不能为null
                User principal = new User(username, "", authorities);
                return new UsernamePasswordAuthenticationToken(principal, "", authorities);
            }
        }
        return null;
    }
}
