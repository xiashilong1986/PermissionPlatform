package com.permission.security.filter;

import com.permission.security.configuration.RoleAccessDecisionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-26   *
 * * Time: 22:26        *
 * * to: lz&xm          *
 * **********************
 * 权限验证过滤器，继承AbstractSecurityInterceptor、实现Filter是必须的
 * 首先，登陆后，每次访问资源都会被这个拦截器拦截，会执行doFilter这个方法，这个方法调用了invoke方法，其中fi断点显示是一个url
 * 最重要的是beforeInvocation这个方法，它首先会调用MethodFilterInvocationSecurityMetadataSource类的getAttributes方法获取被拦截url所需的权限
 * 在调用RoleAccessDecisionManager类decide方法判断用户是否具有权限,执行完后就会执行下一个拦截器
 **/
@Component
public class AbstractSecurityInterceptorFilter extends AbstractSecurityInterceptor implements Filter {

    private final FilterInvocationSecurityMetadataSource securityMetadataSource;

    @Autowired
    public AbstractSecurityInterceptorFilter(FilterInvocationSecurityMetadataSource securityMetadataSource) {
        this.securityMetadataSource = securityMetadataSource;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(servletRequest, servletResponse, filterChain);
        invoke(fi);
    }

    private void invoke(FilterInvocation fi) throws IOException, ServletException {
        InterceptorStatusToken token = super.beforeInvocation(fi);
        fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        super.afterInvocation(token, null);
    }

    /**
     * Indicates the type of secure objects the subclass will be presenting to the
     * abstract parent for processing. This is used to ensure collaborators wired to the
     * {@code AbstractSecurityInterceptor} all support the indicated secure object class.
     *
     * @return the type of secure object the subclass provides services for
     */
    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return securityMetadataSource;
    }

    @Autowired
    public void setAccessDecisionManager(RoleAccessDecisionManager roleAccessDecisionManager) {
        super.setAccessDecisionManager(roleAccessDecisionManager);
    }
}
