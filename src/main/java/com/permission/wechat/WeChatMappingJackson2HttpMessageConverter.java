package com.permission.wechat;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************
 * * Author: StillWarm  *
 * * Date: 2019-10-28   *
 * * Time: 17:23        *
 * * to: xm             *
 * **********************
 **/
class WeChatMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    WeChatMappingJackson2HttpMessageConverter() {
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        setSupportedMediaTypes(mediaTypes);
    }
}
