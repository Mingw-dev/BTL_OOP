package com.example.ilms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String COOKIE_AUTH_NAME = "CookieAuth";

    private static final String ACCESS_TOKEN_NAME = "access-token";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hệ thống quản lý học tập thông minh tích hợp trí tuệ nhân tạo API")
                        .version("1.0")
                        .description("Tài liệu API hệ thống quản lý học tập thông minh tích hợp trí tuệ nhân tạo"))

                .addSecurityItem(new SecurityRequirement().addList(COOKIE_AUTH_NAME))
                .components(new Components()
                        .addSecuritySchemes(COOKIE_AUTH_NAME,
                                new SecurityScheme()
                                        .name(ACCESS_TOKEN_NAME)
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .description("Nhập giá trị JWT Token được lưu trong cookie: " + ACCESS_TOKEN_NAME)));
    }
}