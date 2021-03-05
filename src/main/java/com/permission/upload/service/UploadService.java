package com.permission.upload.service;

import com.permission.utils.global.result.GlobalResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * created by xiashilong
 * 2017-12-05 11:37
 **/
public interface UploadService {

    /**
     * 文件上传
     *
     * @param request    请求
     * @param folderName 文件夹
     * @return GlobalResult
     * @throws Exception e
     */
    GlobalResult uploadFiles(HttpServletRequest request, String folderName) throws Exception;

//    /**
//     * 小程序文件上传
//     *
//     * @param request    请求
//     * @param folderName 文件夹
//     * @return GlobalResult
//     * @throws Exception e
//     */
//    GlobalResult appletUploadFiles(HttpServletRequest request, String folderName) throws Exception;

    /**
     * 自定义文件名上传
     *
     * @param request    请求
     * @param folderName 文件夹名
     * @throws Exception e
     */
    GlobalResult uploadCustomizeFileName(HttpServletRequest request, String folderName) throws Exception;

    /**
     * 删除文件
     *
     * @param filePathList 文件路径集合
     * @throws IOException io
     */
    void deleteFiles(List<String> filePathList) throws IOException;

    /**
     * base64文件上传
     *
     * @param base64     base64字符串
     * @param folderName 文件夹名
     * @return GlobalResult
     * @throws Exception e
     */
    GlobalResult uploadBase64(String base64, String folderName) throws Exception;
}
