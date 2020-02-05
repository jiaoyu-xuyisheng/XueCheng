package com.xuecheng.manager_cms;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;


@EntityScan("com.xuecheng.framework.domain.cms")
@ComponentScan(basePackages={"com.xuecheng.api"})
@ComponentScan(basePackages={"com.xuecheng.manager_cms"})
@SpringBootApplication
public class ManageCmsApplication {

    public static void main(String[] args){
        SpringApplication.run(ManageCmsApplication.class,args);
    }
}
