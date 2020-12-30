package com.xuecheng.manager_cms.controller;


import com.xuecheng.api.cms.CmsTemplateControllerApi;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manager_cms.service.CmsSiteService;
import com.xuecheng.manager_cms.service.CmsTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模板管理
 */
@RestController
@RequestMapping("/cms/template")
public class CmsTemplateController implements CmsTemplateControllerApi {

    @Autowired
    CmsTemplateService cmsTemplateService;

    @GetMapping("/list/{page}/{size}")
    @Override
    public QueryResponseResult findCmsTemplateList(@PathVariable("page") int page, @PathVariable("size") int size){
        return cmsTemplateService.findCmsTemplateList(page,size);
    }
}
