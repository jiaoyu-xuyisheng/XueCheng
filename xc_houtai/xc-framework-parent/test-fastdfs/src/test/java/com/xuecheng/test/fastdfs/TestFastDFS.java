package com.xuecheng.test.fastdfs;

import org.csource.fastdfs.*;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {

    @Value("${demo.mypath}")
    private String MyPath;//文件服务器地址
    String filePath="D:\\rar\\jiuxian\\jiuxian\\img\\goodsimg\\show28.jpg";

    //上传文件
    @Test
    public void testUpload(){

        try {
            //加载fastdfs-client.properties配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义TrackerClient，用于请求TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            //连接tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Stroage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建stroageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //向stroage服务器上传文件
            //本地文件的路径
            String filePath="D:\\rar\\jiuxian\\jiuxian\\img\\goodsimg\\show28.jpg";
            //上传成功后拿到文件Id
            String fileId = storageClient1.upload_file1(filePath, "jpg", null);
            System.out.println(fileId);
            //group1/M00/00/01/wKhlQVuhU3eADb4pAAAawU0ID2Q159.png

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }

    }




    //文件下载
    @Test
    public void testDownload(){

        try {
            //加载fastdfs-client.properties配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义TrackerClient，用于请求TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            //连接tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Stroage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建stroageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //向stroage服务器上传文件
            byte[] bytes = storageClient1.download_file1("group1/M00/00/00/wKgZpl-noWOAenI0AAFeYIbGiJs430.jpg");
            //本地文件的路径
            FileOutputStream fileOutputStream = new FileOutputStream(new File("E:\\mypic.jpg"));
            fileOutputStream.write(bytes);
            System.out.println("下载成功");
            //上传成功后拿到文件Id
            //group1/M00/00/01/wKhlQVuhU3eADb4pAAAawU0ID2Q159.png

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("下载失败");
        }

    }

}
