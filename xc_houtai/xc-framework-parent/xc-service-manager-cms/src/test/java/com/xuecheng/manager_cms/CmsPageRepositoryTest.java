package com.xuecheng.manager_cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manager_cms.dao.CmsPageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

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
}
