package com.permission.security.exception;

import com.permission.utils.global.result.ResultEnum;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-29   *
 * * Time: 15:04        *
 * * to: lz&xm          *
 * **********************
 * token被修改异常
 **/
@Getter
public class JwtSignatureException extends AuthenticationException {
    private Integer code;
    private String msg;

    public JwtSignatureException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }
}
