package com.permission.security.exception;

import com.permission.utils.global.result.ResultEnum;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-29   *
 * * Time: 14:57        *
 * * to: lz&xm          *
 * **********************
 * token过期异常
 **/
@Getter
public class JwtExpiredException extends AuthenticationException {
    private Integer code;
    private String msg;

    public JwtExpiredException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }
}
