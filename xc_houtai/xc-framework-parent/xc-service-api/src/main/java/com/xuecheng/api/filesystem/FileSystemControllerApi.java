package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "文件管理接口",description = "文件管理接口，提供增删改查")
public interface FileSystemControllerApi {

    //上传文件
   @ApiOperation("上传文件接口")
    public UploadFileResult upload(MultipartFile multipartFile,
                                   String filetag,//文件标签
                                   String businesskey,//业务标实
                                   String metadata//文件信息
                                   );

}
