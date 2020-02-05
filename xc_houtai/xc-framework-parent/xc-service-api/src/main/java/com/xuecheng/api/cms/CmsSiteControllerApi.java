package com.xuecheng.api.cms;

import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value="cms_site页面管理接口",description = "cms_site页面管理接口，提供页面的增、删、改、查")
public interface CmsSiteControllerApi {
    @ApiOperation("页面列表") @ApiImplicitParams({ @ApiImplicitParam(name="page",value = "页 码",required=true,paramType="path",dataType="int"), @ApiImplicitParam(name="size",value = "每页记录 数",required=true,paramType="path",dataType="int") })
    public QueryResponseResult findList(int page, int size);
}
