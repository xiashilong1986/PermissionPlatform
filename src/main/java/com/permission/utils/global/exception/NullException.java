package com.permission.utils.global.exception;

import com.permission.utils.global.result.ResultEnum;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2018-05-12   *
 * * Time: 15:36        *
 * * to: lz&xm          *
 * **********************
 **/
public class NullException {
    /**
     * 判断是否有数据
     *
     * @param obj o
     */
    public static void isEmpty(Object obj) {
        if (StringUtils.isEmpty(obj)) {
            throw new GlobalException(ResultEnum.NULL_DATA);
        }
        if (obj instanceof List) {
            List list = (List) obj;
            if (list.isEmpty()) {
                throw new GlobalException(ResultEnum.NULL_DATA);
            }
        }
    }
}
