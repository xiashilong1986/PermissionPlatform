package com.permission.security.exception;

import com.permission.utils.global.result.ResultEnum;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-31   *
 * * Time: 9:45        *
 * * to: lz&xm          *
 * **********************
 **/
@Getter
public class JwtIpException extends AuthenticationException {

    private Integer code;
    private String msg;

    public JwtIpException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }
}
