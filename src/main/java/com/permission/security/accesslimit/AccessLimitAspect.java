package com.permission.security.accesslimit;

import com.permission.utils.global.exception.GlobalException;
import com.permission.utils.global.result.ResultEnum;
import com.permission.utils.http.HttpUtil;
import com.permission.utils.redis.RedisUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-25   *
 * * Time: 10:48        *
 * * to: lz&xm          *
 * **********************
 * 接口限流切面
 **/
@Aspect
@Component
public class AccessLimitAspect {

    //注解拦截
    @Pointcut("@annotation(com.permission.security.accesslimit.AccessLimit)")
    public void pointcut() {
        // do nothing
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        //获取http请求
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        MethodSignature signature = (MethodSignature) point.getSignature();
        //获取方法
        Method method = signature.getMethod();
        //获取注解对象
        AccessLimit limit = method.getAnnotation(AccessLimit.class);
        AccessLimitEntity accessLimit = new AccessLimitEntity(limit);
        //缓存key
        String redisKey = HttpUtil.getIp(request) + getRealPath(request.getRequestURI(), method.getName());
        if (RedisUtil.hasKey(redisKey)) {
            Integer count = Integer.valueOf(RedisUtil.get(redisKey).toString());//ip已访问接口次数
            if (count >= accessLimit.getMaxCount()) {
                throw new GlobalException(ResultEnum.ACCESS_FREQUENTLY);//限流
            } else {
                RedisUtil.set(redisKey, String.valueOf(count + 1), RedisUtil.getExpire(redisKey));//增加计数
            }
        } else {
            RedisUtil.set(redisKey, String.valueOf(1), accessLimit.getMillisecond());//新的计数数据
        }
        return point.proceed();
    }

    //获取真实请求路径
    private String getRealPath(String uri, String methodName) {
        if (uri.contains(methodName)) {
            return uri.substring(0, uri.indexOf(methodName) + methodName.length());
        }
        return uri;
    }
}
