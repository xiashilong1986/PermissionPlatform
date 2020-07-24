package com.permission.security.accesslimit;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-25   *
 * * Time: 17:43        *
 * * to: lz&xm          *
 * **********************
 **/
class LimitConstant {

    //限制毫秒数
    final static long DEFAULT_MILLISECOND = 60000L;
    //最大访问次数
    final static int DEFAULT_MAX_COUNT = 40;//默认
    final static int FIND_MAX_COUNT = 50; //查询
    final static int MODIFY_MAX_COUNT = 30; //更新
}
