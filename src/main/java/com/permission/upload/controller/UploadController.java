package com.permission.upload.controller;

import com.permission.security.accesslimit.AccessLimit;
import com.permission.security.accesslimit.LimitType;
import com.permission.upload.service.UploadService;
import com.permission.utils.global.result.GlobalResult;
import com.permission.utils.global.result.GlobalResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * created by xiashilong
 * 2017-12-05 11:49
 **/
@RestController
@RequestMapping(value = "/uploadController")
public class UploadController {

    private final UploadService uploadService;

    @Autowired
    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }


    /**
     * 文件上传
     *
     * @param request    请求
     * @param folderName 文件夹
     * @return GlobalResult
     * @throws Exception e
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/open/uploads")
    public GlobalResult uploads(HttpServletRequest request, String folderName) throws Exception {
        return uploadService.uploadFiles(request, folderName);
    }

//    /**
//     * 小程序文件上传
//     *
//     * @param request    请求
//     * @param folderName 文件夹
//     * @return GlobalResult
//     * @throws Exception e
//     */
//    @AccessLimit(type = LimitType.MODIFY)
//    @PostMapping(value = "/open/appletUploadFiles")
//    public GlobalResult appletUploadFiles(HttpServletRequest request, String folderName) throws Exception {
//        return uploadService.appletUploadFiles(request, folderName);
//    }

    /**
     * 自定义文件名上传
     *
     * @param request    请求
     * @param folderName 文件夹名
     * @return GlobalResult
     * @throws Exception e
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/open/uploadCustomizeFileName")
    public GlobalResult uploadCustomizeFileName(HttpServletRequest request, String folderName) throws Exception {
        return uploadService.uploadCustomizeFileName(request, folderName);
    }

    /**
     * 删除文件
     *
     * @param filePathList 文件路径集合
     * @return GlobalResult
     * @throws Exception e
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping(value = "/open/delete", produces = "application/json")
    public GlobalResult delete(@RequestBody List<String> filePathList) throws Exception {
        uploadService.deleteFiles(filePathList);
        return GlobalResultUtil.success();
    }

    /**
     * base64文件上传
     *
     * @param base64     base64字符串
     * @param folderName 文件夹名
     * @return GlobalResult
     * @throws Exception e
     */
    @AccessLimit(type = LimitType.MODIFY)
    @PostMapping("/open/uploadBase64")
    public GlobalResult uploadBase64(String base64, String folderName) throws Exception {
        return uploadService.uploadBase64(base64, folderName);
    }

}
