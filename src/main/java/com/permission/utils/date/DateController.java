package com.permission.utils.date;

import com.permission.security.accesslimit.AccessLimit;
import com.permission.security.accesslimit.LimitType;
import com.permission.utils.global.result.GlobalResult;
import com.permission.utils.global.result.GlobalResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * **********************
 * * Author: StillWarm  *
 * * Date: 2019-11-20   *
 * * Time: 9:03        *
 * * to: xm             *
 * **********************
 **/
@RestController
@RequestMapping(value = "/date")
public class DateController {

    /**
     * 获取当前时间
     *
     * @return GlobalResult
     */
    @AccessLimit(type = LimitType.FIND)
    @GetMapping(value = "/auth/now")
    public GlobalResult now() {
        return GlobalResultUtil.success(LocalDateTime.now());
    }

}
