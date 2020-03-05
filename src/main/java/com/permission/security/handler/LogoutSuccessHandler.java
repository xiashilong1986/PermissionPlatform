package com.permission.security.handler;

import com.permission.security.jwt.JwtTokenUtil;
import com.permission.utils.global.result.GlobalResultUtil;
import com.permission.utils.global.result.ResultEnum;
import com.permission.utils.redis.RedisUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-28   *
 * * Time: 16:54        *
 * * to: lz&xm          *
 * **********************
 * 登出成功的处理
 **/
@Component
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {
    private final static Logger logger = LoggerFactory.getLogger(LogoutSuccessHandler.class);

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 清除redis
        String accessToken = request.getHeader(JwtTokenUtil.tokenHeader);
        if (!StringUtils.isEmpty(accessToken)) {
            Claims claims = JwtTokenUtil.getClaims(accessToken);
            RedisUtil.delete(claims.getSubject());
            logger.info("用户 [ {} ] 安全登出", claims.getSubject());
            GlobalResultUtil.out(response, GlobalResultUtil.success(ResultEnum.LOGOUT_SUCCESS));
        }
        //清空上下文
        SecurityContextHolder.clearContext();
        //记录退出日志
    }
}
