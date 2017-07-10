package com.dmall.service.Impl;

import com.dmall.service.IFileService;
import com.dmall.utils.FtpUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path) {
//        重新拼接文件名
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf("."));
        String uploadFileName = UUID.randomUUID().toString() + fileExtensionName;
        logger.info("开始上传，上传文件名：{}，路径：{}，新文件名：{}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);

        try {
//            上传文件
            file.transferTo(targetFile);

//            上传至Ftp 服务器
            FtpUtil.uploadFile(Lists.newArrayList(targetFile));
            logger.info("连接结束");
//            删除 upload 源文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常:{}",e.getMessage());
            return null;
        }
        return targetFile.getName();
    }

}
