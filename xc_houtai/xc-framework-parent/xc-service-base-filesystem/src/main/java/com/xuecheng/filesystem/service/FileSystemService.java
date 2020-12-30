package com.xuecheng.filesystem.service;


import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileSystemService {


    @Autowired
    private FileSystemRepository fileSystemRepository;

    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    private int connect_timeout_in_seconds;

    @Value("${xuecheng.fastdfs.tracker_servers}")
    private String tracker_servers;

    @Value("${xuecheng.fastdfs.charset}")
    private String charset;

    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    private int network_timeout_in_seconds;

    @Value("${xuecheng.fastdfs.port}")
    private int port;


    public UploadFileResult upload(MultipartFile multipartFile,String filetag,String businesskey,String metadata){
        //1.将文件上传到fastDfs中，得到一个文件Id
        String fileId = fdfsUpload(multipartFile);
        //2.将文件的信息，存到mongodb中
        FileSystem fileSystem = new FileSystem();
        fileSystem.setFileId(fileId);
        fileSystem.setFilePath(fileId);
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setFileType(filetag);
        fileSystem.setFileType(multipartFile.getContentType());
        if(StringUtils.isNotEmpty(metadata)){
            JSON.parseObject(metadata, Map.class);
        }
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        fileSystemRepository.save(fileSystem);
        //3返回结果
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);

    }

    private String fdfsUpload(MultipartFile multipartFile){
        initFdfsConfig();

        try {
            if(multipartFile==null){
                ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
            }
            TrackerClient trackerClient = new TrackerClient();
            //连接tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Stroage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建stroageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);

          //得到上传的字节
            byte[] bytes = multipartFile.getBytes();
            //得到文件的名称
            String name = multipartFile.getOriginalFilename();
            //得到扩照名;
            String ext_name = name.substring(name.lastIndexOf(".") + 1);

            String fileId = storageClient1.upload_file1(bytes, ext_name, null);

            return fileId;
        } catch (IOException | MyException e) {
            e.printStackTrace();
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);

        }
         return null;

    }

    private void initFdfsConfig(){
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_tracker_http_port(port);


        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.cast(FileSystemCode.FS_INIT_FASTDFS_ERROR);
        }
    }
}
