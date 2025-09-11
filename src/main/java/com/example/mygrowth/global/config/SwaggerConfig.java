package com.example.mygrowth.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger로 구성하는 API 문서의 제목, 버전, 설명에 대해 설정(전체적인 API 문서의 설정 담당)
 * OpenAPI 설정
 * Swagger UI 설정
 */

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("MyGrowth API Document")
                .version("v0.0.1")
                .description("MyGrowth API입니다.");
        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
