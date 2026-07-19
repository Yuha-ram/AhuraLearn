package com.ahuralearn.common.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AhuraLearn API documentation")
                        .version("1.0.0")
                        .description("AhuraLearn Application")
                        .contact(new Contact().name("HU ZONGHAN"))
                );
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("Auth Module")
                .packagesToScan("com.ahuralearn.auth.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("User Module")
                // 指定要扫描的 Controller 所在的包路径，请替换为你实际的包名
                // profile 包是 My Information 页面的接口，也属于 user 模块
                .packagesToScan("com.ahuralearn.user.controller", "com.ahuralearn.profile.controller")
                // 也可以按路径匹配扫描，例如只扫描 /user/** 的接口
                // .pathsToMatch("/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi cmsApi() {
        return GroupedOpenApi.builder()
                .group("CMS Module")
                .packagesToScan("com.ahuralearn.cms.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi courseApi() {
        return GroupedOpenApi.builder()
                .group("Course Module")
                .packagesToScan("com.ahuralearn.course.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi learningApi() {
        return GroupedOpenApi.builder()
                .group("Learning Module")
                .packagesToScan("com.ahuralearn.learning.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi mediaApi() {
        return GroupedOpenApi.builder()
                .group("Media Module")
                // file 包是文档上传 / AI 摘要的接口，和 media 同属一个模块
                .packagesToScan("com.ahuralearn.media.controller", "com.ahuralearn.file.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi assistantApi() {
        return GroupedOpenApi.builder()
                .group("Assistant Module")
                .packagesToScan("com.ahuralearn.assistant.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi aiApi() {
        return GroupedOpenApi.builder()
                .group("AI Module")
                .packagesToScan("com.ahuralearn.ai.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi gameApi() {
        return GroupedOpenApi.builder()
                .group("Game Module")
                .packagesToScan("com.ahuralearn.game.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi reportApi() {
        return GroupedOpenApi.builder()
                .group("Report Module")
                .packagesToScan("com.ahuralearn.report.controller")
                .build();
    }
}