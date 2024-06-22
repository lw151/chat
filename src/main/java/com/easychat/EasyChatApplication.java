package com.easychat;

import com.easychat.entity.config.AppConfig;
import com.easychat.entity.constants.Constants;
import com.easychat.spring.ApplicationContextProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.MultipartConfigElement;

/**
 * @ClassName EasychatApplication
 * @Author 程序员老罗
 * @Date 2023/12/10 21:10
 */
@EnableAsync //异步调用
@SpringBootApplication(scanBasePackages = {"com.easychat"})
@MapperScan(basePackages = {"com.easychat.mappers"}) //Mapper包扫描
@EnableTransactionManagement //开启事务
@EnableScheduling //定时任务
public class EasyChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyChatApplication.class, args);
    }

    @Bean
    @DependsOn({"applicationContextProvider"})
    MultipartConfigElement multipartConfigElement() {
        AppConfig appConfig = (AppConfig) ApplicationContextProvider.getBean("appConfig");
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation(appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP);
        return factory.createMultipartConfig();
    }
}
