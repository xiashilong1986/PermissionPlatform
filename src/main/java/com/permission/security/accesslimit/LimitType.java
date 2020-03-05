package com.permission.security.accesslimit;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-25   *
 * * Time: 17:28        *
 * * to: lz&xm          *
 * **********************
 * 限流默认枚举
 **/
@Getter
@AllArgsConstructor
public enum LimitType {

    DEFAULT(LimitConstant.DEFAULT_MILLISECOND, LimitConstant.DEFAULT_MAX_COUNT), //默认值

    FIND(LimitConstant.DEFAULT_MILLISECOND, LimitConstant.FIND_MAX_COUNT),//查询类型的接口

    MODIFY(LimitConstant.DEFAULT_MILLISECOND, LimitConstant.MODIFY_MAX_COUNT),//修改类型的接口

    CUSTOMIZE(0L, 0);//自定义

    private long millisecond; //限制毫秒数

    private int maxCount;//最大访问次数
}
