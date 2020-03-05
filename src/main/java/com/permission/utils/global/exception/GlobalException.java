package com.permission.utils.global.exception;


import com.permission.utils.global.result.ResultEnum;
import lombok.Getter;

/**
 * created by xiashilong
 * 2017-11-23 15:27
 **/
@Getter
public class GlobalException extends RuntimeException {
    private static final long serialVersionUID = 5332247822591152001L;
    private Integer code;

    private String msg;

    public GlobalException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public GlobalException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public GlobalException() {
        super(ResultEnum.NULL_DATA.getMsg());
        this.code = ResultEnum.NULL_DATA.getCode();
        this.msg = ResultEnum.NULL_DATA.getMsg();
    }
    public static GlobalException exception100(String msg) {
        return new GlobalException(100, msg);
    }
}
