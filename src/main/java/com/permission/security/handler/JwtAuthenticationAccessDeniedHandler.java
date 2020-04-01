package com.permission.security.handler;

import com.permission.utils.global.result.GlobalResultUtil;
import com.permission.utils.global.result.ResultEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-26   *
 * * Time: 13:32        *
 * * to: lz&xm          *
 * **********************
 * 接口无权限处理类
 **/
@Component
public class JwtAuthenticationAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Handles an access denied failure.
     *
     * @param request               that resulted in an <code>AccessDeniedException</code>
     * @param response              so that the user agent can be advised of the failure
     * @param accessDeniedException that caused the invocation
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        GlobalResultUtil.out(response, GlobalResultUtil.fail(ResultEnum.PERMISSION_ERROR));
    }
}
