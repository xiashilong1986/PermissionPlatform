package com.permission.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Configuration
public class UploadPathConfiguration implements WebMvcConfigurer {

    private final MultipartProperties multipartProperties;

    @Autowired
    public UploadPathConfiguration(MultipartProperties multipartProperties) {
        this.multipartProperties = multipartProperties;
    }

    /**
     * * @Description: 对文件的路径进行配置, 创建一个虚拟路径/Path/** ，即只要在<img src="/Path/picName.jpg" />便可以直接引用图片
     * 这是图片的物理路径  "file:/+本地图片的地址"
     *
     * @Date： Create in 14:08 2017/12/20
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String filePath = "file:" + multipartProperties.getLocation() + File.separator;
        registry.addResourceHandler("/auth/permission/files/**")
                .addResourceLocations(filePath)
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic()); //增加浏览器缓存
    }
}