package com.permission.wechat.applet;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.*;

import java.io.*;


/**
 * **********************
 * * Author: StillWarm  *
 * * Date: 2020-01-19   *
 * * Time: 16:48        *
 * * to: xm             *
 * **********************
 * 微信小程序配置文件
 **/
@Getter
@Setter(AccessLevel.PRIVATE)
@ToString
public class WeChatAppletConfig implements WXPayConfig {

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
     * 授权地址
     */
    private String oauth2Url = "";

    /**
     * code
     */
    private String code = "CODE";

    /**
     * 获取全局token URL
     */
    private String globalAccessTokenUrl;

    /**
     * 证书路径
     */
    private String certPath = "";

    private byte[] certData;

    private WeChatAppletConfig(String appId, String mchId, String appSecret, String apiKey) {
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
    public static WeChatAppletConfig build(String appId, String mchId, String appSecret, String apiKey) {
        WeChatAppletConfig weChatConfig = new WeChatAppletConfig(appId, mchId, appSecret, apiKey);
        weChatConfig.oauth2Url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=CODE&grant_type=authorization_code",
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
    public WeChatAppletConfig certPath(String certPath) {
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
