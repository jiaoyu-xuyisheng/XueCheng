package com.xuecheng.manager_cms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@RequestMapping("/page/template")
public class TemplateController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/banner")
    public String index_banner(Map<String,Object> map)
    {
        String url="http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f";
        ResponseEntity<Map> entity = restTemplate.getForEntity(url, Map.class);
        Map body = entity.getBody();
        map.putAll(body);
        return "index_banner";
    }
}
