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
                .packagesToScan("com.ahuralearn.user.controller")
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

    // 如果后续 AhuraLearn 增加了“AI问答服务”，可以再加一个 Bean 分组：
    // @Bean
    // public GroupedOpenApi aiApi() {
    //     return GroupedOpenApi.builder().group("AI智能服务").pathsToMatch("/ai/**").build();
    // }
}