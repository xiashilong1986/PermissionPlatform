package com.permission.wechat.applet;

import com.permission.utils.global.exception.GlobalException;
import com.permission.wechat.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * **********************
 * * Author: StillWarm  *
 * * Date: 2020-03-27   *
 * * Time: 15:52        *
 * * to: xm             *
 * **********************
 * 微信小程序token
 **/
@Slf4j
@Component
public class AppletAccessTokenFactory {

    private final WeChatAppletConfig weChatAppletConfig;

    private static WeChatAppletConfig WE_CHAT_APPLET_CONFIG;

    //全局token
    private static String ACCESS_TOKEN;

    //过期时间
    private static LocalDateTime EXPIRES_IN;

    @Autowired
    public AppletAccessTokenFactory(WeChatAppletConfig weChatAppletConfig) {
        this.weChatAppletConfig = weChatAppletConfig;
    }


    @PostConstruct
    public void init() {
        WE_CHAT_APPLET_CONFIG = weChatAppletConfig;
    }

    public static synchronized String get() {
        if (StringUtils.isEmpty(ACCESS_TOKEN) || LocalDateTime.now().isAfter(EXPIRES_IN)) {
            Map map = (Map) HttpRequest.restHttpRequest(WE_CHAT_APPLET_CONFIG.getGlobalAccessTokenUrl(), HttpMethod.GET, null, Map.class);
            if (!StringUtils.isEmpty(map.get("access_token").toString())) {
                ACCESS_TOKEN = map.get("access_token").toString();
                EXPIRES_IN = LocalDateTime.now().plusSeconds(Integer.valueOf(map.get("expires_in").toString()) - 100);
                log.info("ACCESS_TOKEN : " + ACCESS_TOKEN);
                log.info("ACCESS_TOKEN_EXPIRES_IN : " + EXPIRES_IN);
            } else {
                log.error(map.get("errcode").toString() + " : " + map.get("errmsg").toString());
                throw GlobalException.exception100(map.get("errcode").toString() + " : " + map.get("errmsg").toString());
            }
        }
        return ACCESS_TOKEN;
    }
}
