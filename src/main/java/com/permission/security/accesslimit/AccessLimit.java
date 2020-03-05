package com.permission.security.accesslimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AccessLimit {

    LimitType type() default LimitType.DEFAULT;//接口类型

    long millisecond() default LimitConstant.DEFAULT_MILLISECOND; //1分钟毫秒数

    int maxCount() default LimitConstant.DEFAULT_MAX_COUNT; //最大访问次数

}
