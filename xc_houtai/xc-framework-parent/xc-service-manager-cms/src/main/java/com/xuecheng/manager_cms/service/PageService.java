package com.xuecheng.manager_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manager_cms.config.RabbitmqConfig;
import com.xuecheng.manager_cms.dao.CmsPageRepository;
import com.xuecheng.manager_cms.dao.CmsSiteRepository;
import com.xuecheng.manager_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import freemarker.template.Configuration;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {

    @Autowired
    CmsPageRepository cmsPageRepository;

    /**
     * 页面列表展示
     * @param page
     * @param size
     * @param queryPageRequest
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){
        //查询条件
        ExampleMatcher exampleMather=ExampleMatcher.matching()
                .withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        //条件值
        CmsPage cmsPage=new CmsPage();
        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //创建条件实例
        Example<CmsPage> example=Example.of(cmsPage,exampleMather);

        if(page<=0){page=1;}
        page=page-1;
        if(size<=0){size=10;}
        Pageable pageable=PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());
        QueryResponseResult queryResponseResult= new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }


    /**
     * 添加页面
     * @param cmsPage
     * @return
     */
    public CmsPageResult add(CmsPage cmsPage){

        if(cmsPage == null){
            //抛出异常，非法参数异常..指定异常信息的内容
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_PARAME_IS_WROOG);
        }

        CmsPage cmsPage1=cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(
                cmsPage.getPageName(),cmsPage.getSiteId(),cmsPage.getPageWebPath()
        );
        if(cmsPage1!=null){
            //抛出异常，页面已经存在
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);
        CmsPageResult cmsPageResult= new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        return cmsPageResult;
    }


    /**
     * 根据id查询对象
     * @param id
     * @return
     */
    public CmsPage getById(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        //如果对象不为空
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    public CmsPageResult edit(String id,CmsPage cmsPage){
        CmsPage one = getById(id);
        if(one!=null){
            //准备更新数据
            //设置要修改的数据
            //更新模板id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            one.setDataUrl(cmsPage.getDataUrl());
            //提交修改
            cmsPageRepository.save(one);
            return new CmsPageResult(CommonCode.SUCCESS,one);
        }
        //修改失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }


    public ResponseResult deleteById(String id){
        CmsPage one = this.getById(id);
        if(one!=null){
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);

    }

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    RabbitTemplate rabbitTemplate;

    //通过页面ID实现页面静态化
    public String getPageHtml(String pageId){
        //获取页面模型数据
        Map model=getModelByPageId(pageId);
        if(model==null){ ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL); }
        //获取页面模板
        String templateContent= getTemplateByPageId(pageId);
        if(StringUtils.isEmpty(templateContent)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        String html=generateHtml(templateContent,model);
        if(StringUtils.isEmpty(html)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }




    //获取页面模型数据
    private Map getModelByPageId(String pageId) {
        CmsPage cmsPage = getById(pageId);
        if(cmsPage==null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //取出dataUrl
        String dataUrl = cmsPage.getDataUrl();
        if(StringUtils.isEmpty(dataUrl)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //通过dataUrl得到数据，并封装在Map中，其实这个dataurl就是端口加路径加cms_config的id
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body =forEntity.getBody();
        return body;
    }


    //获取页面模板
    private String getTemplateByPageId(String pageId)  {
        CmsPage page = getById(pageId);//查询页面信息
        if(page==null){ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);}
        String templateId = page.getTemplateId();//得到页面模板ID
        if(StringUtils.isEmpty(templateId)){ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);}//没有模板
        Optional<CmsTemplate> option = cmsTemplateRepository.findById(templateId);
        if (option.isPresent()) {//option不为空时
            CmsTemplate cmsTemplate = option.get();
            String templateFileId = cmsTemplate.getTemplateFileId();//得到模板文件ID
            GridFSFile gsfile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));//得到模板内容
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gsfile.getObjectId());
            GridFsResource gridFsResource = new GridFsResource(gsfile, gridFSDownloadStream);
            try{
                String s = IOUtils.toString(gridFsResource.getInputStream(), "UTF-8");
                return s;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }


    //执行静态化
    private String generateHtml(String templateContent, Map model) {
        try {
        Configuration configuration = new Configuration(Configuration.getVersion());//生成配制类
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();//模板加载器
        stringTemplateLoader.putTemplate("template",templateContent);
        configuration.setTemplateLoader(stringTemplateLoader);//配置模板加载器
        Template template = configuration.getTemplate("template");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        return html;
        } catch (Exception e ) {
            e.printStackTrace();
        }
        return null;
    }


    //页面发布
    public ResponseResult post(String pageId){
        //执行页面静态化
        String pageHtml = this.getPageHtml(pageId);
        //将页面静态化文件存储到GridFs中
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        //向MQ发消息
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //向mq 发送消息
    private void sendPostPage(String pageId){
        //得到页面信息
        CmsPage cmsPage = this.getById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //创建消息对象
        Map<String,String> msg = new HashMap<>();
        msg.put("pageId",pageId);
        //转成json串
        String jsonString = JSON.toJSONString(msg);
        //发送给mq
        //站点id
        String siteId = cmsPage.getSiteId();
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,jsonString);
    }
    //保存html到GridFS
    private CmsPage saveHtml(String pageId,String htmlContent){
        //先得到页面信息
        CmsPage cmsPage = this.getById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        ObjectId objectId = null;
        try {
            //将htmlContent内容转成输入流
            InputStream inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            //将html文件内容保存到GridFS
            objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将html文件id更新到cmsPage中
        cmsPage.setHtmlFileId(objectId.toHexString());
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }
    
    
    
    


}
