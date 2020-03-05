package com.permission.utils.global.result;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局http返回对象
 * created by xiashilong
 * 2017-11-23 13:59
 **/
@Data
@NoArgsConstructor
public class GlobalResult {
    /**
     * 错误码
     */
    private Integer code;
    /**
     * 错误消息
     */
    private String msg;
    /**
     * 返回值
     */
    private Object data;

    public GlobalResult(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public GlobalResult(Integer code, String msg) {
        this(code, msg, null);
    }

    public GlobalResult(ResultEnum resultEnum, Object data) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
        this.data = data;
    }
}

