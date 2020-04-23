package com.permission.utils.global.exception;


import com.permission.utils.global.result.GlobalResult;
import com.permission.utils.global.result.GlobalResultUtil;
import com.permission.utils.global.result.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * created by xiashilong
 * 2017-11-23 15:21
 **/
@ControllerAdvice
@Slf4j
public class ExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public GlobalResult handle(Exception e) {
        if (e instanceof GlobalException) {
            GlobalException g = (GlobalException) e;
            if (!g.getCode().equals(100) || !g.getMsg().equals(ResultEnum.NULL_DATA.getMsg())) {
                log.error("[GlobalException]", g);
            }
            return new GlobalResult(g.getCode(), g.getMessage());
        } else if (e instanceof DataIntegrityViolationException) {//唯一键数据
            return new GlobalResult(401, "相同数据已存在");
        } else if (e instanceof AuthenticationException) {
            return new GlobalResult(500, e.getMessage());
        } else {
            log.error("[SystemException]", e);
            return GlobalResultUtil.fail(ResultEnum.UNKNOWN_ERROR, e.getMessage());
        }
    }
}
