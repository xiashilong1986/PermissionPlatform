package com.permission.security.handler;

import com.permission.utils.global.result.GlobalResultUtil;
import com.permission.utils.global.result.ResultEnum;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-26   *
 * * Time: 15:34        *
 * * to: lz&xm          *
 * **********************
 * token认证失败处理类
 **/
@Component
public class JwtAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    /**
     * Commences an authentication scheme.
     * <p>
     * <code>ExceptionTranslationFilter</code> will populate the <code>HttpSession</code>
     * attribute named
     * <code>AbstractAuthenticationProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY</code>
     * with the requested target URL before calling this method.
     * <p>
     * Implementations should modify the headers on the <code>ServletResponse</code> as
     * necessary to commence the authentication process.
     *
     * @param request       that resulted in an <code>AuthenticationException</code>
     * @param response      so that the user agent can begin authentication
     * @param authException that caused the invocation
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        //判断token是过期了还是错误
        Integer c = 500;
        if (authException.getMessage().equals(ResultEnum.PERMISSION_CHANGE.getMsg())) {
            c = ResultEnum.PERMISSION_CHANGE.getCode();
        } else if (authException.getMessage().equals(ResultEnum.TOKEN_INVALID.getMsg())) {
            c = ResultEnum.TOKEN_INVALID.getCode();
        } else if (authException.getMessage().equals(ResultEnum.IP_ERROR.getMsg())) {
            c = ResultEnum.IP_ERROR.getCode();
        } else if (authException.getMessage().equals(ResultEnum.TOKEN_ERROR.getMsg())) {
            c = ResultEnum.TOKEN_ERROR.getCode();
        } else if (authException.getMessage().equals(ResultEnum.SESSION_ERROR.getMsg())) {
            c = ResultEnum.SESSION_ERROR.getCode();
        }
        GlobalResultUtil.out(response, GlobalResultUtil.fail(c, authException.getMessage()));
    }
}
