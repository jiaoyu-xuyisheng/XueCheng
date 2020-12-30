package com.xuecheng.manager_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.netflix.discovery.converters.Auto;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manager_cms.dao.CmsPageRepository;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CmsPageRepository  cmsPageRepository;

    @Test
    public void testFindAll(){
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }


    @Test
    public void testFindPage(){
        int page = 1;
        int size = 5;
        Pageable pageable= PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }

    @Test
    public void  testUpdate(){
        Optional<CmsPage> byId = cmsPageRepository.findById("5a795ac7dd573c04508f3a56");
        if(byId.isPresent()) {
            CmsPage cmsPage = byId.get();
            cmsPage.setPageAliase("test01");//修改值
            CmsPage save = cmsPageRepository.save(cmsPage);//保存对象
            System.out.println(save);
        }

    }

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Test
    public void testGridFs() throws FileNotFoundException {
        File file = new File("E:/index_banner.ftl");
        FileInputStream fis=new FileInputStream(file);
        ObjectId lbtID = gridFsTemplate.store(fis, "轮播图测试11");
        String s = lbtID.toString();
        System.out.println(s);
    }

    @Autowired
    GridFSBucket  gridFSBucket;

    //下载文件
    @Test
    public void queryFile() throws IOException {
        //文件id
        String fileId="5ec21b4876cfa843d4e313b8";
        //根据ID查询文件
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(file.getObjectId());
        //创建gridFsResource,用于获取流对象
        GridFsResource gridFsResource = new GridFsResource(file, gridFSDownloadStream);
       //获取流中的数据
        String s = IOUtils.toString(gridFsResource.getInputStream(),"UTF-8");
        System.out.println(s);
    }

    @Test
    public void testDelFile() throws IOException {
        //根据文件id删除fs.files和fs.chunks中的记录
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is("5ec21b4876cfa843d4e313b8")));
    }
}
