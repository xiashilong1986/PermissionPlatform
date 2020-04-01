package com.permission.security.handler;

import com.permission.utils.global.result.GlobalResultUtil;
import com.permission.utils.global.result.ResultEnum;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-28   *
 * * Time: 16:50        *
 * * to: lz&xm          *
 * **********************
 * 登陆失败的异常处理
 **/
@Component
public class LoginAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        if (e instanceof BadCredentialsException) {
            GlobalResultUtil.out(response, GlobalResultUtil.fail(ResultEnum.WRONG_LOGIN));//账号或密码错误
        } else if (e instanceof LockedException) {
            GlobalResultUtil.out(response, GlobalResultUtil.fail(ResultEnum.ACCOUNT_LOCKED));//账号被锁定
        } else if (e instanceof InternalAuthenticationServiceException) {
            GlobalResultUtil.out(response, GlobalResultUtil.fail(ResultEnum.ACCESS_FREQUENTLY));//接口限流
        } else {
            GlobalResultUtil.out(response, GlobalResultUtil.fail(ResultEnum.LOGIN_FAIL));//未知异常;排查原因
        }
    }
}
