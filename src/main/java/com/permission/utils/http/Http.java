package com.permission.utils.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * **********************
 * * Author: StillWarm  *
 * * Date: 2020-12-15   *
 * * Time: 9:33        *
 * * to: xm             *
 * **********************
 * httpclient 4.x.x 请求工具类
 **/
@Slf4j
public class Http {

    public static String doGet(String url, Map<String, String> headerMap, Map<String, String> param) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            //设置请求参数
            if (null != param && !param.isEmpty()) {
                List<BasicNameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, String> entry : param.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                String params = EntityUtils.toString(new UrlEncodedFormEntity(list, Consts.UTF_8));
                url = url + "?" + params;
            }
            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(url);
            // 设置请求头信息
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);
            // 通过返回对象获取返回数据
            HttpEntity entity = response.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            result = EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(httpClient, response);
        }
        return result;
    }

    //参数为对象的post请求(json)
    public static String doPost(String url, Map<String, String> headerMap, Map<String, Object> paramMap) {
        if (null != paramMap && !paramMap.isEmpty()) {
            String s = JSONObject.toJSONString(paramMap);
            StringEntity httpEntity = new StringEntity(s, "utf-8");
            return doPost(url, headerMap, httpEntity, null);
        }
        return "";
    }

    //参数为表单的post请求(form/data)
    public static String doPostForForm(String url, Map<String, Object> paramMap) {
        if (null != paramMap && !paramMap.isEmpty()) {
            List<NameValuePair> nameValuePairList = new ArrayList<>();
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                nameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }

            return doPost(url, null, null, nameValuePairList);
        }
        return "";
    }

    //参数为集合的post请求(json)
    public static String doPost(String url, Map<String, String> headerMap, List<Map<String, Object>> paramMap) {
        String s = JSONArray.toJSONString(paramMap);
        log.info("post 请求参数 : {}", s);
        StringEntity httpEntity = new StringEntity(s, "utf-8");
        return doPost(url, headerMap, httpEntity, null);
    }

    private static String doPost(String url, Map<String, String> headerMap, StringEntity stringEntity, List<NameValuePair> nameValuePairList) {
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        // 设置请求头信息
        if (null != headerMap) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (null != stringEntity) {
            httpPost.setEntity(stringEntity);
        }
        if (!nameValuePairList.isEmpty()) {
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(httpClient, httpResponse);
        }
        return result;
    }

    private static void close(CloseableHttpClient httpClient, CloseableHttpResponse httpResponse) {
        // 关闭资源
        if (null != httpResponse) {
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != httpClient) {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, String> headers() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }
}
