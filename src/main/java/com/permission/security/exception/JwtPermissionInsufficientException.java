package com.permission.security.exception;

import com.permission.utils.global.result.ResultEnum;
import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-29   *
 * * Time: 17:43        *
 * * to: lz&xm          *
 * **********************
 * 无接口权限异常
 **/
@Getter
public class JwtPermissionInsufficientException extends AccessDeniedException {
    private Integer code;
    private String msg;


    public JwtPermissionInsufficientException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }
}
