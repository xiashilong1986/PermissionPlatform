package com.permission.wechat;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.*;

import java.io.*;
import java.net.URLEncoder;

/**
 * **********************
 * * Author: StillWarm  *
 * * Date: 2019-10-28   *
 * * Time: 15:15        *
 * * to: xm             *
 * **********************
 * 微信配置
 **/
@Getter
@Setter(AccessLevel.PRIVATE)
@ToString
public class WeChatConfig implements WXPayConfig {

    /**
     * app_id
     */
    private String appId = "";

    /**
     * appSecret
     */
    private String appSecret = "";

    /**
     * 商户号
     */
    private String mchId = "";

    /**
     * apiKey
     */
    private String apiKey = "";

    /**
     * 网页授权 授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理
     */
    private String redirectUri = "";

    /**
     * 网页授权自定义参数
     */
    private String state = "STATE";

    /**
     * 证书路径
     */
    private String certPath = "";

    /**
     * 网页授权 code URL
     */
    private String codeUrl;

    /**
     * 通过code换取网页授权access_token URL
     */
    private String accessTokenUrl;

    /**
     * 获取用户信息 URL
     */
    private String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";

    /**
     * code
     */
    private String code = "CODE";

    /**
     * 获取全局token URL
     */
    private String globalAccessTokenUrl;

    /**
     * js api 票据  URL
     */
    private String ticketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";

    private byte[] certData;

    private WeChatConfig(String appId, String mchId, String appSecret, String apiKey) {
        this.appId = appId;
        this.mchId = mchId;
        this.appSecret = appSecret;
        this.apiKey = apiKey;
    }

    /**
     * 构建微信常量
     *
     * @param appId     appId
     * @param mchId     商户号
     * @param appSecret appSecret
     * @param apiKey    apiKey
     * @return WeChatConfig
     */
    public static WeChatConfig build(String appId, String mchId, String appSecret, String apiKey) {
        WeChatConfig weChatConfig = new WeChatConfig(appId, mchId, appSecret, apiKey);
        weChatConfig.accessTokenUrl = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=CODE&grant_type=authorization_code",
                appId, appSecret);
        weChatConfig.globalAccessTokenUrl = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s", appId, appSecret);
        return weChatConfig;
    }

    /**
     * 设置证书路径
     * 必须设置
     *
     * @param certPath 证书路径
     * @return WeChatConfig
     */
    public WeChatConfig certPath(String certPath) {
        this.certPath = certPath;
        try {
            File file = new File(certPath);
            @Cleanup InputStream certStream = new FileInputStream(file);
            this.certData = new byte[(int) file.length()];
            certStream.read(this.certData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置网页授权回调地址
     * 同时会设置 网页授权 code URL
     *
     * @param redirectUri 地址
     * @return WeChatConfig
     */
    public WeChatConfig redirectUri(String redirectUri) {
        try {
            this.redirectUri = URLEncoder.encode(redirectUri, "utf-8");
            this.codeUrl = String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect",
                    this.appId, this.redirectUri);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public String getAppID() {
        return appId;
    }

    @Override
    public String getMchID() {
        return mchId;
    }

    @Override
    public String getKey() {
        return apiKey;
    }

    @Override
    public InputStream getCertStream() {
        return new ByteArrayInputStream(this.certData);
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 8000;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }
}
