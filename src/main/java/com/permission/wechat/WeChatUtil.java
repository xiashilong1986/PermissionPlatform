package com.permission.wechat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.permission.utils.global.exception.GlobalException;
import com.permission.wechat.applet.AppletAccessTokenFactory;
import com.permission.wechat.applet.WeChatAppletConfig;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

/**
 * **********************
 * * Author: StillWarm  *
 * * Date: 2019-10-28   *
 * * Time: 16:13        *
 * * to: xm             *
 * **********************
 **/
@Slf4j
@Component
public class WeChatUtil {

    //其他微信配置对象
    private final WeChatConfig weChatConfig;
    //其他微信配置对象
    private static WeChatConfig WE_CHAT_CONFIG;

    //小程序配置对象
    private final WeChatAppletConfig weChatAppletConfig;
    //小程序配置对象
    private static WeChatAppletConfig WE_CHAT_APPLET_CONFIG;

    //小程序支付对象
    private static WXPay appletWeChatPay;

    //其他微信支付对象
    private static WXPay weChatPay;

    @Autowired
    public WeChatUtil(WeChatConfig weChatConfig, WeChatAppletConfig weChatAppletConfig) {
        this.weChatConfig = weChatConfig;
        this.weChatAppletConfig = weChatAppletConfig;
    }

    @PostConstruct
    public void init() {
        WE_CHAT_CONFIG = weChatConfig;
        WE_CHAT_APPLET_CONFIG = weChatAppletConfig;
        appletWeChatPay = WeChatPayFactory.get(weChatAppletConfig);
        weChatPay = WeChatPayFactory.get(weChatConfig);
    }

    /**
     * 网页授权获取code URL
     *
     * @param state 自定义参数
     * @return String
     */
    public static String getCodeUrl(String state) {
        return WE_CHAT_CONFIG.getCode().replace(WE_CHAT_CONFIG.getState(), state);
    }


    /**
     * 通过code 获取用户信息
     *
     * @param code 微信code
     * @return Map
     */
    public static Map getUserInfo(String code) {
        log.info("weChat code is : " + code);
        String url = WE_CHAT_CONFIG.getAccessTokenUrl().replace(WE_CHAT_CONFIG.getCode(), code);
        log.info("weChat get user url is : " + url);
        //code 换 token
        Map tokenMap = (Map) HttpRequest.restHttpRequest(url, HttpMethod.GET, null, Map.class);
        if (null == tokenMap.get("openid") || "".equals(tokenMap.get("openid"))) {
            log.error(tokenMap.toString());
            throw GlobalException.exception100("WeChat code error");
        }
        log.info("weChat token is : " + tokenMap.toString());
        //获取微信用户信息
        return (Map) HttpRequest.restHttpRequest(String.format(WE_CHAT_CONFIG.getUserInfoUrl(), tokenMap.get("access_token"), tokenMap.get("openid")), HttpMethod.GET, null, Map.class);
    }

    /**
     * 小程序获取用户信息
     *
     * @param code 微信小程序code
     * @return Map
     */
    public static Map getAppletUserInfo(String code) {
        String url = WE_CHAT_APPLET_CONFIG.getOauth2Url().replace(WE_CHAT_APPLET_CONFIG.getCode(), code);
        Map map = (Map) HttpRequest.restHttpRequest(url, HttpMethod.GET, null, Map.class);
        log.info("weChatApplet user info is : " + map.toString());
        if (map.get("errcode") == null || "".equals(map.get("errcode"))) {
            return map;
        } else {
            log.error(map.get("errcode").toString() + map.get("errmsg"));
            throw new GlobalException(Integer.valueOf(map.get("errcode").toString()), map.get("errmsg").toString());
        }
    }

    /**
     * 小程序 支付
     *
     * @param orderNumber 订单号
     * @param price       支付金额
     * @param body        商品描述
     * @param ip          用户ip
     * @param notifyUrl   异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数
     * @param openId      用户openid
     * @param attach      自定义参数
     * @return 支付调起所需参数
     */
    public static Map<String, String> payForApplet(String orderNumber, Integer price, String body, String ip, String notifyUrl, String openId, String attach) throws Exception {
        Map<String, String> pay = pay(orderNumber, price, body, ip, notifyUrl, TradeType.JSAPI, openId, attach, appletWeChatPay);
        Map<String, String> map = new HashMap<>();
        map.put("appId", WE_CHAT_APPLET_CONFIG.getAppID());
        map.put("timeStamp", String.valueOf(generateTimestamp()));
        map.put("nonceStr", pay.get("nonce_str"));
        map.put("package", "prepay_id=" + pay.get("prepay_id"));
        map.put("signType", WXPayConstants.MD5);
        map.put("paySign", WXPayUtil.generateSignature(map, WE_CHAT_APPLET_CONFIG.getApiKey()));
        pay.put("applet", JSONArray.toJSONString(map));
        return pay;
    }

    /**
     * jsApi 支付
     *
     * @param orderNumber 订单号
     * @param price       支付金额
     * @param body        商品描述
     * @param ip          用户ip
     * @param notifyUrl   异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数
     * @param openId      用户openid
     * @param attach      自定义参数
     * @return 支付调起所需参数
     */
    public static Map<String, String> payForJsApi(String orderNumber, Integer price, String body, String ip, String notifyUrl, String openId, String attach) throws Exception {
        Map<String, String> pay = pay(orderNumber, price, body, ip, notifyUrl, TradeType.JSAPI, openId, attach, weChatPay);
        Map<String, String> map = new HashMap<>();
        map.put("appId", WE_CHAT_CONFIG.getAppID());
        map.put("timeStamp", String.valueOf(generateTimestamp()));
        map.put("nonceStr", WXPayUtil.generateNonceStr());
        map.put("package", "prepay_id=" + pay.get("prepay_id"));
        map.put("signType", WXPayConstants.MD5);
        map.put("paySign", WXPayUtil.generateSignature(map, WE_CHAT_CONFIG.getApiKey()));
        pay.put("h5", JSONArray.toJSONString(map));
        return pay;
    }

    /**
     * app 支付
     *
     * @param orderNumber 订单号
     * @param price       支付金额
     * @param body        商品描述
     * @param ip          用户ip
     * @param notifyUrl   异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数
     * @param attach      自定义参数
     * @return 支付调起所需参数
     */
    public static Map<String, String> payForApp(String orderNumber, Integer price, String body, String ip, String notifyUrl, String attach) throws Exception {
        Map<String, String> pay = pay(orderNumber, price, body, ip, notifyUrl, TradeType.APP, null, attach, weChatPay);
        Map<String, String> map = new HashMap<>();
        map.put("appid", WE_CHAT_CONFIG.getAppID());
        map.put("partnerid", WE_CHAT_CONFIG.getMchID());
        map.put("prepayid", pay.get("prepay_id"));
        map.put("package", "Sign=WXPay");
        map.put("noncestr", pay.get("nonce_str"));
        map.put("timestamp", String.valueOf(generateTimestamp()));
        map.put("sign", WXPayUtil.generateSignature(map, WE_CHAT_CONFIG.getApiKey()));
        pay.put("app", JSONArray.toJSONString(map));
        return pay;
    }

    /**
     * 统一下单
     *
     * @param orderNumber 订单号
     * @param price       支付金额
     * @param body        商品描述
     * @param ip          用户ip
     * @param notifyUrl   异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数
     * @param tradeType   支付类型<see>TradeType</see>
     * @param openId      用户openid
     * @param attach      自定义参数
     * @return Map
     */
    private static Map<String, String> pay(String orderNumber, Integer price, String body, String ip, String notifyUrl, TradeType tradeType, String openId, String attach, WXPay wxPay) throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("body", body);//商品简单描述，该字段请按照规范传递，具体请见参数规定
        data.put("out_trade_no", orderNumber);//商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一。
        data.put("device_info", "WEB");//自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
        data.put("fee_type", "CNY");//符合ISO 4217标准的三位字母代码，默认人民币：CNY，详细列表请参见货币类型
        data.put("total_fee", price.toString());//订单总金额，单位为分
        data.put("spbill_create_ip", ip);//支持IPV4和IPV6两种格式的IP地址。用户的客户端IP
        data.put("notify_url", notifyUrl);//异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        data.put("trade_type", tradeType.getValue());  // JSAPI -JSAPI支付;NATIVE -Native支付;APP -APP支付
        if (!StringUtils.isEmpty(openId)) {
            data.put("openid", openId);//trade_type=JSAPI时（即JSAPI支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识。
        }
        if (!StringUtils.isEmpty(attach)) {
            data.put("attach", attach);//附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。
        }
        Map<String, String> returnMap = wxPay.unifiedOrder(data);
        log.info("weChat pay is : " + returnMap.toString());
        if (returnMap.get("return_code").equals(WXPayConstants.SUCCESS)) {
            if (returnMap.get("result_code").equals(WXPayConstants.SUCCESS)) {
                return returnMap;
            } else {
                log.error("weChat pay error message : " + returnMap.get("err_code_des"));
                throw GlobalException.exception100(returnMap.get("err_code"));
            }
        } else {
            throw GlobalException.exception100(returnMap.get("return_msg"));
        }
    }

    /**
     * 其他微信订单查询
     *
     * @param orderNumber 订单号(微信的订单号或商户系统内部订单号)，建议优先微信的订单号
     * @param weChatNum   true 微信订单号,false 商户订单号
     * @return Map
     * @throws Exception e
     */
    public static Map<String, String> orderQuery(String orderNumber, boolean weChatNum) throws Exception {
        return orderQuery(orderNumber, weChatNum, weChatPay);
    }

    /**
     * 小程序订单查询
     *
     * @param orderNumber 订单号(微信的订单号或商户系统内部订单号)，建议优先微信的订单号
     * @param weChatNum   true 微信订单号,false 商户订单号
     * @return Map
     * @throws Exception e
     */
    public static Map<String, String> orderQueryApplet(String orderNumber, boolean weChatNum) throws Exception {
        return orderQuery(orderNumber, weChatNum, appletWeChatPay);
    }

    /**
     * 查询订单
     *
     * @param orderNumber 订单号(微信的订单号或商户系统内部订单号)，建议优先微信的订单号
     * @param weChatNum   true 微信订单号,false 商户订单号
     * @return map
     */
    private static Map<String, String> orderQuery(String orderNumber, boolean weChatNum, WXPay wxPay) throws Exception {
        Map<String, String> data = new HashMap<>();
        if (weChatNum) {
            data.put("transaction_id", orderNumber);
        } else {
            data.put("out_trade_no", orderNumber);
        }
        Map<String, String> returnMap = wxPay.orderQuery(data);
        log.info("weChat order query is : " + returnMap.toString());
        return returnMap;
    }

    /**
     * 其他微信支付结果通知
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return Map
     * @throws Exception e
     */
    public static Map<String, String> payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return payNotify(request, response, weChatPay);
    }

    /**
     * 小程序支付结果通知
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return Map
     * @throws Exception e
     */
    public static Map<String, String> payNotifyApplet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return payNotify(request, response, appletWeChatPay);
    }

    /**
     * 微信支付结果通知
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return Map
     */
    private static Map<String, String> payNotify(HttpServletRequest request, HttpServletResponse response, WXPay wxPay) throws Exception {
        // 读取参数
        @Cleanup InputStream inputStream = request.getInputStream();
        StringBuilder sb = new StringBuilder();
        String s;
        @Cleanup BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }
        Map<String, String> notifyMap = WXPayUtil.xmlToMap(sb.toString());
        //验证签名
        if (wxPay.isPayResultNotifySignatureValid(notifyMap)) {
            if (WXPayConstants.SUCCESS.equals(notifyMap.get("return_code"))) {
                Map<String, String> returnMap = new HashMap<>();//返回微信数据
                if (WXPayConstants.SUCCESS.equals(notifyMap.get("result_code"))) {// 支付成功
                    returnMap.put("return_code", WXPayConstants.SUCCESS);
                    returnMap.put("return_msg", "OK");
                } else {
                    returnMap.put("return_code", WXPayConstants.FAIL);
                    returnMap.put("return_msg", notifyMap.get("err_code"));
                }
                @Cleanup BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(WXPayUtil.mapToXml(returnMap).getBytes());
            } else {
                log.error("weChat pay notify error : " + notifyMap.get("return_msg"));
                throw GlobalException.exception100("weChat pay notify error");
            }
        } else {
            log.error("weChat pay notify sign fail : " + notifyMap.toString());
            throw GlobalException.exception100("weChat pay notify sign fail");
        }
        log.info("weChat pay notify is : " + notifyMap.toString());
        return notifyMap;
    }

    /**
     * 其他微信申请退款
     *
     * @param orderNumber 订单号(微信的订单号或商户系统内部订单号)
     * @param weChatNum   true 微信订单号,false 商户订单号
     * @param outRefundNo 商户系统内部的退款单号
     * @param totalFee    订单总金额，单位为分，只能为整数
     * @param refundFee   退款总金额，订单总金额，单位为分，只能为整数
     * @param refundDesc  若商户传入，会在下发给用户的退款消息中体现退款原因;注意：若订单退款金额≤1元，且属于部分退款，则不会在退款消息中体现退款原因
     * @return Map
     * @throws Exception e
     */
    public static Map<String, String> refund(String orderNumber, boolean weChatNum, String outRefundNo, Integer totalFee, Integer refundFee, String refundDesc) throws Exception {
        return refund(orderNumber, weChatNum, outRefundNo, totalFee, refundFee, refundDesc, weChatPay);
    }

    /**
     * 小程序申请退款
     *
     * @param orderNumber 订单号(微信的订单号或商户系统内部订单号)
     * @param weChatNum   true 微信订单号,false 商户订单号
     * @param outRefundNo 商户系统内部的退款单号
     * @param totalFee    订单总金额，单位为分，只能为整数
     * @param refundFee   退款总金额，订单总金额，单位为分，只能为整数
     * @param refundDesc  若商户传入，会在下发给用户的退款消息中体现退款原因;注意：若订单退款金额≤1元，且属于部分退款，则不会在退款消息中体现退款原因
     * @return Map
     * @throws Exception e
     */
    public static Map<String, String> refundApplet(String orderNumber, boolean weChatNum, String outRefundNo, Integer totalFee, Integer refundFee, String refundDesc) throws Exception {
        return refund(orderNumber, weChatNum, outRefundNo, totalFee, refundFee, refundDesc, appletWeChatPay);
    }

    /**
     * 申请退款
     *
     * @param orderNumber 订单号(微信的订单号或商户系统内部订单号)
     * @param weChatNum   true 微信订单号,false 商户订单号
     * @param outRefundNo 商户系统内部的退款单号
     * @param totalFee    订单总金额，单位为分，只能为整数
     * @param refundFee   退款总金额，订单总金额，单位为分，只能为整数
     * @param refundDesc  若商户传入，会在下发给用户的退款消息中体现退款原因;注意：若订单退款金额≤1元，且属于部分退款，则不会在退款消息中体现退款原因
     * @return Map
     */
    private static Map<String, String> refund(String orderNumber, boolean weChatNum, String outRefundNo, Integer totalFee, Integer refundFee, String refundDesc, WXPay wxPay) throws Exception {
        Map<String, String> data = new HashMap<>();
        if (weChatNum) {
            data.put("transaction_id", orderNumber);
        } else {
            data.put("out_trade_no", orderNumber);
        }
        data.put("out_refund_no", outRefundNo);
        data.put("total_fee", totalFee.toString());
        data.put("refund_fee", refundFee.toString());
        data.put("refund_desc", refundDesc);
        Map<String, String> returnMap = wxPay.refund(data);
        if (returnMap.get("return_code").equals(WXPayConstants.SUCCESS)) {
            if (returnMap.get("result_code").equals(WXPayConstants.SUCCESS)) {
                return returnMap;
            } else {
                log.error("weChat refund error message : " + returnMap.get("err_code") + " - " + returnMap.get("err_code_des"));
                if (returnMap.get("err_code").equals("NOTENOUGH")) {
                    throw GlobalException.exception100("可用退款余额不足");
                } else {
                    throw GlobalException.exception100(returnMap.get("err_code_des"));
                }
            }
        } else {
            throw GlobalException.exception100(returnMap.get("return_msg"));
        }
    }

    /**
     * 其他微信查询退款
     *
     * @param refundNumber 退单号(微信的退单号或商户系统内部退单号)，建议优先微信的订单号
     * @param weChatNum    true 微信退单号,false 商户退单号
     * @return Map
     * @throws Exception e
     */
    public static Map<String, String> refundQuery(String refundNumber, boolean weChatNum) throws Exception {
        return refundQuery(refundNumber, weChatNum, weChatPay);
    }

    /**
     * 小程序查询退款
     *
     * @param refundNumber 退单号(微信的退单号或商户系统内部退单号)，建议优先微信的订单号
     * @param weChatNum    true 微信退单号,false 商户退单号
     * @return Map
     * @throws Exception e
     */
    public static Map<String, String> refundQueryApplet(String refundNumber, boolean weChatNum) throws Exception {
        return refundQuery(refundNumber, weChatNum, appletWeChatPay);
    }

    /**
     * 查询退款
     *
     * @param refundNumber 退单号(微信的退单号或商户系统内部退单号)，建议优先微信的订单号
     * @param weChatNum    true 微信退单号,false 商户退单号
     * @return Map
     */
    private static Map<String, String> refundQuery(String refundNumber, boolean weChatNum, WXPay wxPay) throws Exception {
        Map<String, String> data = new HashMap<>();
        if (weChatNum) {
            data.put("refund_id", refundNumber);
        } else {
            data.put("out_refund_no", refundNumber);
        }
        Map<String, String> returnMap = wxPay.refundQuery(data);
        log.info("weChat refund query is : " + returnMap.toString());
        return returnMap;
    }

    /**
     * 获取 js SDK 所需参数
     *
     * @param url 当前网页的URL，不包含#及其后面部分
     * @return Map
     */
    public static Map<String, String> getJsSDKParameter(String url) {
        Map<String, String> data = new HashMap<>();
        String ticket = TicketFactory.get();
        String nonceStr = WXPayUtil.generateNonceStr();
        String timestamp = String.valueOf(generateTimestamp());
        String signature = "jsapi_ticket=" + ticket +
                "&noncestr=" + nonceStr +
                "&timestamp=" + timestamp +
                "&url=" + url;
        data.put("noncestr", nonceStr);
        data.put("signature", generateJsSDKSignature(signature));
        data.put("timestamp", timestamp);
        data.put("appId", WE_CHAT_CONFIG.getAppID());
        return data;
    }

    /**
     * 其他微信单笔转账到微信零钱接口
     *
     * @param amount         转账金额
     * @param openId         微信账号
     * @param ip             请求ip
     * @param desc           企业付款操作说明信息
     * @param partnerTradeNo 商户订单号，需保持唯一性  (只能是字母或者数字，不能包含有符号)
     * @return Map
     */
    public static Map<String, String> transferToPocketMoney(Integer amount, String openId, String ip, String desc, String partnerTradeNo) throws Exception {
        return transferToPocketMoney(amount, openId, ip, desc, partnerTradeNo, weChatPay, WE_CHAT_CONFIG);
    }

    /**
     * 小程序单笔转账到微信零钱接口
     *
     * @param amount         转账金额
     * @param openId         微信账号
     * @param ip             请求ip
     * @param desc           企业付款操作说明信息
     * @param partnerTradeNo 商户订单号，需保持唯一性  (只能是字母或者数字，不能包含有符号)
     * @return Map
     */
    public static Map<String, String> transferToPocketMoneyApplet(Integer amount, String openId, String ip, String desc, String partnerTradeNo) throws Exception {
        return transferToPocketMoney(amount, openId, ip, desc, partnerTradeNo, appletWeChatPay, WE_CHAT_APPLET_CONFIG);
    }

    /**
     * 验证微信小程序上传图片
     *
     * @param multipartFile 文件对象
     * @return boolean
     */
    public static boolean appletCheckPhoto(MultipartFile multipartFile) {
        try {

            CloseableHttpClient httpclient = HttpClients.createDefault();

            CloseableHttpResponse response;

            HttpPost request = new HttpPost("https://api.weixin.qq.com/wxa/img_sec_check?access_token=" + AppletAccessTokenFactory.get());
            request.addHeader("Content-Type", "application/octet-stream");

            InputStream inputStream = multipartFile.getInputStream();

            byte[] byt = new byte[inputStream.available()];
            inputStream.read(byt);
            request.setEntity(new ByteArrayEntity(byt, ContentType.create(multipartFile.getContentType())));

            response = httpclient.execute(request);
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity, "UTF-8");// 转成string
            JSONObject jso = JSONObject.parseObject(result);
            return getResult(jso);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("----------------调用腾讯内容过滤系统出错------------------");
            return true;
        }
    }

    /**
     * 验证微信小程序文字
     *
     * @param content 验证的内容
     * @return boolean
     */
    public static boolean appletCheckText(String content) {
        try {

            CloseableHttpClient httpclient = HttpClients.createDefault();

            CloseableHttpResponse response;

            HttpPost request = new HttpPost("https://api.weixin.qq.com/wxa/msg_sec_check?access_token=" + AppletAccessTokenFactory.get());
            request.addHeader("Content-Type", "application/json;charset=UTF-8");

            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("content", content);
            request.setEntity(new StringEntity(JSONObject.toJSONString(paramMap), ContentType.create("application/json", "utf-8")));

            response = httpclient.execute(request);
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity, "UTF-8");// 转成string
            JSONObject jso = JSONObject.parseObject(result);
            return getResult(jso);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("----------------调用腾讯内容过滤系统出错------------------");
            return true;
        }
    }

    private static boolean getResult(JSONObject jso) {
        Object e = jso.get("errcode");
        int errCode = (int) e;
        if (errCode == 0) {
            return true;
        } else if (errCode == 87014) {
            log.info("-----------内容违规-----------");
            return false;
        }
        return true;
    }

    /**
     * 单笔转账到微信零钱接口
     *
     * @param amount         转账金额
     * @param openId         微信账号
     * @param ip             请求ip
     * @param desc           企业付款操作说明信息
     * @param partnerTradeNo 商户订单号，需保持唯一性  (只能是字母或者数字，不能包含有符号)
     * @return Map
     */
    private static Map<String, String> transferToPocketMoney(Integer amount, String openId, String ip, String desc, String partnerTradeNo, WXPay wxPay, WXPayConfig wxPayConfig) throws Exception {
        Map<String, String> param = new HashMap<>();
        param.put("mch_appid", wxPayConfig.getAppID());//申请商户号的appid或商户号绑定的appid
        param.put("mchid", wxPayConfig.getMchID());//微信支付分配的商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串，不长于32位
        param.put("partner_trade_no", partnerTradeNo);//商户订单号，需保持唯一性  (只能是字母或者数字，不能包含有符号)
        param.put("openid", openId);//商户appid下，某用户的openid
        param.put("check_name", "NO_CHECK");//  NO_CHECK：不校验真实姓名  FORCE_CHECK：强校验真实姓名
        param.put("amount", amount.toString());// 企业付款金额，单位为分
        param.put("desc", desc);// 企业付款操作说明信息。必填。
        param.put("spbill_create_ip", ip);//该IP同在商户平台设置的IP白名单中的IP没有关联，该IP可传用户端或者服务端的IP。
        param.put("sign", WXPayUtil.generateSignature(param, wxPayConfig.getKey()));
        String str = wxPay.requestWithCert("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", param, wxPayConfig.getHttpConnectTimeoutMs(), wxPayConfig.getHttpReadTimeoutMs());
        Map<String, String> wxResponse = WXPayUtil.xmlToMap(str);
        if (wxResponse.get("return_code").equals(WXPayConstants.SUCCESS) && wxResponse.get("result_code").equals(WXPayConstants.SUCCESS)) {
            log.info("transferToPocketMoney : " + wxResponse);
            return wxResponse;
        } else {
            log.error("weChat transferToPocketMoney error message : " + wxResponse.get("err_code_des"));
            throw GlobalException.exception100(wxResponse.get("err_code_des"));
        }
    }

    //获取微信时间戳
    private static long generateTimestamp() {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
    }

    //js SDK 签名
    private static String generateJsSDKSignature(String data) {
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(data.getBytes("UTF-8"));
            return byteToHex(crypt.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private static String byteToHex(final byte[] hash) {
        @Cleanup Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
