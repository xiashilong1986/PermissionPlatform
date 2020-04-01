package com.permission.wechat;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * **********************
 * * Author: StillWarm  *
 * * Date: 2019-10-29   *
 * * Time: 15:40        *
 * * to: xm             *
 * **********************
 * 微信支付工厂
 **/
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeChatPayFactory {

    public static WXPay get(WXPayConfig wxPayConfig) {
        return new WXPay(wxPayConfig);
    }
}
