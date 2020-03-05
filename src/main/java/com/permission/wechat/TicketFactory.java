package com.permission.wechat;

import com.permission.utils.global.exception.GlobalException;
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
 * * Date: 2019-11-12   *
 * * Time: 10:10        *
 * * to: xm             *
 * **********************
 * jsapi_ticket 工厂
 **/
@Slf4j
@Component
public class TicketFactory {

    private final WeChatConfig weChatConfig;

    private static WeChatConfig WE_CHAT_CONFIG;
    //js api 票据
    private static String TICKET;

    //过期时间
    private static LocalDateTime EXPIRES_IN;

    @Autowired
    public TicketFactory(WeChatConfig weChatConfig) {
        this.weChatConfig = weChatConfig;
    }

    @PostConstruct
    public void init() {
        WE_CHAT_CONFIG = weChatConfig;
    }

    public static synchronized String get() {
        if (StringUtils.isEmpty(TICKET) || LocalDateTime.now().isAfter(EXPIRES_IN)) {
            Map map = (Map) HttpRequest.restHttpRequest(String.format(WE_CHAT_CONFIG.getTicketUrl(), GlobalAccessTokenFactory.get()), HttpMethod.GET, null, Map.class);
            if (!StringUtils.isEmpty(map.get("ticket").toString())) {
                TICKET = map.get("ticket").toString();
                EXPIRES_IN = LocalDateTime.now().plusSeconds(Integer.valueOf(map.get("expires_in").toString()) - 100);
                log.info("TICKET : " + TICKET);
                log.info("TICKET_EXPIRES_IN : " + EXPIRES_IN);
            } else {
                log.error(map.get("errcode").toString() + " : " + map.get("errmsg").toString());
                throw GlobalException.exception100(map.get("errcode").toString() + " : " + map.get("errmsg").toString());
            }

        }
        return TICKET;
    }
}
