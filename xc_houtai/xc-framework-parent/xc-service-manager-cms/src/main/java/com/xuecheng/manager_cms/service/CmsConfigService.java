package com.xuecheng.manager_cms.service;


import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manager_cms.dao.CmsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CmsConfigService {

    @Autowired
    CmsConfigRepository cmsConfigRepository;

    public CmsConfig getConfigById(String id){
        Optional<CmsConfig> option = cmsConfigRepository.findById(id);
        if(option.isPresent()){
            CmsConfig cmsConfig = option.get();
            return cmsConfig;
        }
        return null;
    }
}
