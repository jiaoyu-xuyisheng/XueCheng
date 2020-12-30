package com.xuecheng.manager_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manager_cms.service.PageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

@Controller
public class CmsPagePreviewController extends BaseController {

    @Autowired
    PageService pageService;

    @RequestMapping(value="/cms/preview/{pageId}",method= RequestMethod.GET)
    public void preview(@PathVariable("pageId") String pageId){
        String html = pageService.getPageHtml(pageId);
        if(StringUtils.isNotEmpty(html)){
            try {
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(html.getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



}
