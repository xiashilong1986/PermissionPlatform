package com.permission.security.accesslimit;

import lombok.Data;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-25   *
 * * Time: 18:11        *
 * * to: lz&xm          *
 * **********************
 **/
@Data
class AccessLimitEntity {

    private long millisecond; //限制毫秒数

    private int maxCount;//最大访问次数

    AccessLimitEntity(AccessLimit accessLimit) {
        LimitType type = accessLimit.type();
        switch (type) {
            case CUSTOMIZE:
                this.millisecond = accessLimit.millisecond();
                this.maxCount = accessLimit.maxCount();
                break;
            default:
                this.millisecond = type.getMillisecond();
                this.maxCount = type.getMaxCount();
                break;
        }
    }
}
