package com.permission.upload.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;

/**
 * **********************
 * * Author: StillWarm  *
 * * Date: 2020-01-10   *
 * * Time: 15:31        *
 * * to: xm             *
 * **********************
 * 多线程上传文件
 **/
@Async("upload")
@Slf4j
@Component
class AsyncUpload {

    public Future<Boolean> multipartFileUpload(MultipartFile multipartFile, File file) throws IOException {
        log.info("线程:" + Thread.currentThread().getName() + "--正在上传文件 : " + multipartFile.getOriginalFilename());
        //如果重复上传先删除原来的文件
        if (file.exists()) {
            file.delete();
        }
        multipartFile.transferTo(file);
        return new AsyncResult<>(file.exists()); //验证文件是否上传成功
    }
}
