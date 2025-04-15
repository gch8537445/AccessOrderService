package com.ipath.orderflowservice;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@EnableDiscoveryClient
@EnableAsync
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, PageHelperAutoConfiguration.class})
@EnableFeignClients(basePackages = "com.ipath.orderflowservice.feignclient")
@EnableConfigurationProperties
@MapperScan("com.ipath.orderflowservice.order.dao")
@ComponentScan({"com.ipath.orderflowservice", "com.ipath.common", "com.ipath.dao"})
@EnableScheduling
public class OrderFlowServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderFlowServiceApplication.class, args);
    }
}
