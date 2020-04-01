package com.permission.wechat;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * **********************
 * * Author: StillWarm  *
 * * Date: 2020-01-13   *
 * * Time: 14:41        *
 * * to: xm             *
 * **********************
 * 微信支付类型
 **/
@Getter
@AllArgsConstructor
enum TradeType {

    JSAPI("JSAPI"), // h5支付
    NATIVE("NATIVE"), // h5支付
    APP("APP"), // app支付
    APPLET("APPLET");//小程序支付

    private String value;
}
