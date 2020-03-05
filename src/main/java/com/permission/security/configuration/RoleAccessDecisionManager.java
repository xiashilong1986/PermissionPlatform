package com.permission.security.configuration;

import com.permission.security.exception.JwtPermissionInsufficientException;
import com.permission.utils.global.result.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-26   *
 * * Time: 22:07        *
 * * to: lz&xm          *
 * **********************
 * 接口权限管理
 **/
@Component
@Slf4j
public class RoleAccessDecisionManager implements AccessDecisionManager {

    /**
     * Resolves an access control decision for the passed parameters.
     *
     * @param authentication   the caller invoking the method (not null)
     * @param object           the secured object being called
     * @param configAttributes the configuration attributes associated with the secured
     *                         object being invoked
     * @throws AccessDeniedException               if access is denied as the authentication does not
     *                                             hold a required authority or ACL privilege
     * @throws InsufficientAuthenticationException if access is denied as the
     *                                             authentication does not provide a sufficient level of trust
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        Collection<? extends GrantedAuthority> myRoles = authentication.getAuthorities();
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            log.info("Access user : {}", userDetails.getUsername());
        }
        // 如果前面的 getAttributes() 返回非空,则返回的数据做为形参传入, 如果返回为null 则不会进入decide() 直接放行
        log.info("Access interface:{}", configAttributes);
        //放行接口
        for (ConfigAttribute urlRoles : configAttributes) {
            if (urlRoles.getAttribute().contains(AuthUrl.AUTH_MARK) || urlRoles.getAttribute().contains(AuthUrl.OPEN_MARK)) {
                return;
            }
        }
        for (GrantedAuthority myRole : myRoles) {// 当前登录的角色
            for (ConfigAttribute urlRoles : configAttributes) {// 前台传入的url  AuthUrl.AUTH_MARK开放路径标识
                if (myRole.getAuthority().equals(urlRoles.getAttribute())) {
                    // 说明此URL地址符合权限,可以放行
                    return;
                }
            }
        }
        throw new JwtPermissionInsufficientException(ResultEnum.PERMISSION_ERROR);
    }

    /**
     * Indicates whether this <code>AccessDecisionManager</code> is able to process
     * authorization requests presented with the passed <code>ConfigAttribute</code>.
     * <p>
     * This allows the <code>AbstractSecurityInterceptor</code> to check every
     * configuration attribute can be consumed by the configured
     * <code>AccessDecisionManager</code> and/or <code>RunAsManager</code> and/or
     * <code>AfterInvocationManager</code>.
     * </p>
     *
     * @param attribute a configuration attribute that has been configured against the
     *                  <code>AbstractSecurityInterceptor</code>
     * @return true if this <code>AccessDecisionManager</code> can support the passed
     * configuration attribute
     */
    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    /**
     * Indicates whether the <code>AccessDecisionManager</code> implementation is able to
     * provide access control decisions for the indicated secured object type.
     *
     * @param clazz the class that is being queried
     * @return <code>true</code> if the implementation can process the indicated class
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
