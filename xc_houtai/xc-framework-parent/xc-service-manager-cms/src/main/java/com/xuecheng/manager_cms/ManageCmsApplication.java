package com.xuecheng.manager_cms;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@EnableDiscoveryClient
@EntityScan("com.xuecheng.framework.domain.cms")
@ComponentScan(basePackages={"com.xuecheng.api"})
@ComponentScan(basePackages={"com.xuecheng.manager_cms"})
@ComponentScan(basePackages={"com.xuecheng.framework"})
@SpringBootApplication
public class ManageCmsApplication {

    public static void main(String[] args){
        SpringApplication.run(ManageCmsApplication.class,args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }
}
