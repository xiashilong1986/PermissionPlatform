package com.permission.upload.service.impl;

import com.permission.upload.service.UploadService;
import com.permission.utils.global.exception.GlobalException;
import com.permission.utils.global.result.GlobalResult;
import com.permission.utils.global.result.GlobalResultUtil;
import com.permission.utils.global.result.ResultEnum;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;

/**
 * created by xiashilong
 * 2017-12-05 11:40
 **/
@Service
@Slf4j
public class UploadServiceImpl implements UploadService {

    private final AsyncUpload asyncUpload;

    private final MultipartProperties multipartProperties;

    @Autowired
    public UploadServiceImpl(AsyncUpload asyncUpload, MultipartProperties multipartProperties) {
        this.asyncUpload = asyncUpload;
        this.multipartProperties = multipartProperties;
    }

    /**
     * 文件上传
     *
     * @param request    请求
     * @param folderName 文件夹
     * @return GlobalResult
     * @throws Exception e
     */
    @Override
    public GlobalResult uploadFiles(HttpServletRequest request, String folderName) throws Exception {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request)
                .getFiles("file");
        if (files.isEmpty()) {
            throw new GlobalException(ResultEnum.NO_FILE);
        }
        List<String> filesName = new LinkedList<>();
        for (MultipartFile file : files) {
            filesName.add(uploadFile(file, folderName));
        }
        return GlobalResultUtil.success(ResultEnum.SUCCESS, filesName);
    }

//    /**
//     * 小程序文件上传
//     *
//     * @param request    请求
//     * @param folderName 文件夹
//     * @return GlobalResult
//     * @throws Exception e
//     */
//    @Override
//    public GlobalResult appletUploadFiles(HttpServletRequest request, String folderName) throws Exception {
//        List<MultipartFile> files = ((MultipartHttpServletRequest) request)
//                .getFiles("file");
//        if (files.isEmpty()) {
//            throw new GlobalException(ResultEnum.NO_FILE);
//        }
//        List<String> filesName = new LinkedList<>();
//        for (MultipartFile file : files) {
//            boolean b = WeChatUtil.appletCheckPhoto(file);
//            if (!b) {
//                return GlobalResultUtil.fail(100, "上传的图片违规");
//            }
//            filesName.add(uploadFile(file, folderName));
//        }
//        return GlobalResultUtil.success(ResultEnum.SUCCESS, filesName);
//    }

    /**
     * 自定义文件名上传
     *
     * @param request    请求
     * @param folderName 文件夹名
     * @throws Exception e
     */
    @Override
    public GlobalResult uploadCustomizeFileName(HttpServletRequest request, String folderName) throws Exception {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request)
                .getFiles("file");
        if (files.isEmpty()) {
            throw new GlobalException(ResultEnum.NO_FILE);
        }
        //返回对应文件上传是否成功
        List<Map<String, Object>> returnList = new ArrayList<>();
        for (MultipartFile file : files) {
            Map<String, Object> map = new HashMap<>();
            //验证,返回文件全名
            String verification = verification(file);
            //文件名
            map.put("fileName", verification);
            //文件夹绝对路径
            String folderPath = multipartProperties.getLocation() + File.separator + folderName;
            //创建文件夹
            createFolder(folderPath);
            //上传文件
            Future<Boolean> booleanFuture = asyncUpload.multipartFileUpload(file, new File(folderPath + File.separator + verification));
            //是否上传成功
            map.put("done", booleanFuture.get());
            returnList.add(map);
        }
        return GlobalResultUtil.success(returnList);
    }

    /**
     * 删除文件
     *
     * @param filePathList 文件路径集合
     * @throws IOException io
     */
    @Override
    public void deleteFiles(List<String> filePathList) throws IOException {
        if (!filePathList.isEmpty()) {
            String[] args;
            String filePath = createDeletePath(filePathList);
            if (System.getProperties().getProperty("os.name").contains("Windows")) {
                args = new String[]{"cmd.exe", "/c", String.format("del %s /q /s", filePath)};
            } else {
                args = new String[]{"sh", "-c", String.format("rm -f -r %s", filePath)};
            }
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(args);
        }
    }

    /**
     * 创建删除路径
     *
     * @param filePathList 前端需删除的文件
     * @return 脚本删除路径
     */
    private String createDeletePath(List<String> filePathList) {
        StringBuilder sb = new StringBuilder(multipartProperties.getLocation() + File.separator + filePathList.get(0));
        for (int i = 1; i < filePathList.size(); i++) {
            sb.append(" ").append(multipartProperties.getLocation()).append(File.separator).append(filePathList.get(i));
        }
        log.info("删除的文件 : " + sb.toString());
        return sb.toString();
    }

    /**
     * 单文件上传
     *
     * @param file       文件
     * @param folderName 文件夹
     * @return String
     */
    private String uploadFile(MultipartFile file, String folderName) throws Exception {
        return createFile(file, folderName);
    }


    /**
     * 创建文件夹
     *
     * @param FolderName 文件夹名
     */
    private void createFolder(String FolderName) {
        File file = new File(FolderName);
        // 如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /**
     * 创建文件
     *
     * @param file       文件
     * @param folderName 文件夹名
     * @return String 上传后的文件名
     * @throws Exception e
     */
    private String createFile(MultipartFile file, String folderName) throws Exception {
        //验证
        String uploadFileName = verification(file);
        // 文件夹绝对路径
        String folderPath = multipartProperties.getLocation() + File.separator + folderName;
        //创建文件夹
        createFolder(folderPath);
        //文件名
        String fileName = fileName() + extFile(uploadFileName);
        //文件绝对路径
        String filePath = folderPath + File.separator + fileName;
        //上传文件
        uploadFile(file.getBytes(), filePath);
        return folderName + File.separator + fileName;
    }

    /**
     * 文件扩展名
     *
     * @param fileName f
     * @return r
     */
    private String extFile(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 文件名
     *
     * @return r
     */
    private String fileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(new Date()) + (int) ((Math.random() * 9 + 1) * 100);
    }

    /**
     * 上传
     *
     * @param file f
     * @throws Exception e
     */
    private void uploadFile(byte[] file, String filePath) throws Exception {
        @Cleanup FileOutputStream out = new FileOutputStream(filePath);
        out.write(file);
    }

    /**
     * 验证
     */
    private String verification(MultipartFile file) {
        String uploadFileName = file.getOriginalFilename();
        if (file.isEmpty() || !uploadFileName.contains(".")) {
            throw new GlobalException(ResultEnum.NO_FILE);
        }
        if (file.getSize() > multipartProperties.getMaxFileSize().toBytes()) {
            throw new GlobalException(ResultEnum.TO_BIG_FILE);
        }
        return uploadFileName;
    }
}
