package com.permission.security.handler;

import com.permission.utils.global.result.GlobalResultUtil;
import com.permission.utils.global.result.ResultEnum;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-31   *
 * * Time: 11:11        *
 * * to: lz&xm          *
 * **********************
 * 单点登陆异常
 **/
@Component
public class SessionInformationExpiredStrategyHandler implements SessionInformationExpiredStrategy {
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) {
        GlobalResultUtil.out(event.getResponse(), GlobalResultUtil.success(ResultEnum.SESSION_ERROR));
    }
}
