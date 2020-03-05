package com.permission.security.configuration;

import com.permission.security.filter.JwtAuthenticationTokenFilter;
import com.permission.security.handler.*;
import com.permission.security.service.impl.SystemUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-26   *
 * * Time: 13:28        *
 * * to: lz&xm          *
 * **********************
 **/
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //用户登陆服务
    private final SystemUserServiceImpl systemUserServiceImpl;
    //验证异常
    private final JwtAuthenticationEntryPointHandler jwtAuthenticationEntryPoint;
    //权限异常
    private final JwtAuthenticationAccessDeniedHandler jwtAuthenticationAccessDeniedHandler;
    //登陆成功处理
    private final LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler;
    //登陆失败处理
    private final LoginAuthenticationFailureHandler loginAuthenticationFailureHandler;
    //登出处理
    private final LogoutSuccessHandler logoutSuccessHandler;
    //单点登陆处理
    private final SessionInformationExpiredStrategyHandler sessionInformationExpiredStrategyHandler;

    @Autowired
    public WebSecurityConfig(SystemUserServiceImpl systemUserServiceImpl, JwtAuthenticationEntryPointHandler jwtAuthenticationEntryPoint, JwtAuthenticationAccessDeniedHandler jwtAuthenticationAccessDeniedHandler, LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler, LoginAuthenticationFailureHandler loginAuthenticationFailureHandler, LogoutSuccessHandler logoutSuccessHandler, SessionInformationExpiredStrategyHandler sessionInformationExpiredStrategyHandler) {
        this.systemUserServiceImpl = systemUserServiceImpl;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationAccessDeniedHandler = jwtAuthenticationAccessDeniedHandler;
        this.loginAuthenticationSuccessHandler = loginAuthenticationSuccessHandler;
        this.loginAuthenticationFailureHandler = loginAuthenticationFailureHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.sessionInformationExpiredStrategyHandler = sessionInformationExpiredStrategyHandler;
    }


    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)//解决AuthenticationManager不能注入的问题
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(systemUserServiceImpl).passwordEncoder(new BCryptPasswordEncoder());
    }


    /**
     * 配置spring security的控制逻辑
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //设置session
//        http.sessionManagement().maximumSessions(1).expiredSessionStrategy(sessionInformationExpiredStrategyHandler);
        // 关闭跨站请求防护
        http.csrf().disable()
                .cors()
                .and()
                //对请求进行认证
                //url认证配置顺序为：1.先配置放行不需要认证的 permitAll() 2.然后配置 需要特定权限的 hasRole() 3.最后配置 anyRequest().authenticated()
                .authorizeRequests()
                .antMatchers(AuthUrl.AUTH_URL).permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                //其他请求都需要进行认证,认证通过够才能访问
                // 待考证：如果使用重定向 httpServletRequest.getRequestDispatcher(url).forward(httpServletRequest,httpServletResponse); 重定向跳转的url不会被拦截（即在这里配置了重定向的url需要特定权限认证不起效），但是如果在Controller 方法上配置了方法级的权限则会进行拦截
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                //在访问一个受保护的资源，用户没有通过登录认证，则抛出登录认证异常，MyAuthenticationEntryPointHandler类中commence()就会调用
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                //在访问一个受保护的资源，用户通过了登录认证，但是权限不够，抛出授权异常，在myAccessDeniedHandler中处理
                .accessDeniedHandler(jwtAuthenticationAccessDeniedHandler)
                .and()
                .formLogin()
                //登录页面路径
                .loginPage("/auth/systemUser/login")
                //登录url
                .loginProcessingUrl("/auth/systemUser/login")//此登录url 和Controller 无关系
                //登录成功跳转路径
                .successForwardUrl("/")
                //登录失败跳转路径
                .failureUrl("/")
                .permitAll()
                //登录成功后 onAuthenticationSuccess（）被调用
                .successHandler(loginAuthenticationSuccessHandler)
                //登录失败后 onAuthenticationFailure（）被调用
                .failureHandler(loginAuthenticationFailureHandler)
                .and()
                .logout()
                //退出系统url
                .logoutUrl("/systemUser/logout")
                //退出系统后的url跳转
                .logoutSuccessUrl("/")
                //退出系统后的 业务处理
                .logoutSuccessHandler(logoutSuccessHandler)
                .permitAll()
                .invalidateHttpSession(true)
                .and();
        //登录后记住用户，下次自动登录,数据库中必须存在名为persistent_logins的表
        // 勾选Remember me登录会在PERSISTENT_LOGINS表中，生成一条记录
//                .rememberMe()
        //cookie的有效期（秒为单位
//                .tokenValiditySeconds(3600);

        //添加JWT过滤器 除/login其它请求都需经过此过滤器
        http.addFilter(new JwtAuthenticationTokenFilter(authenticationManager(), jwtAuthenticationEntryPoint));
        //禁用缓存
        http.headers().cacheControl();
    }
}
