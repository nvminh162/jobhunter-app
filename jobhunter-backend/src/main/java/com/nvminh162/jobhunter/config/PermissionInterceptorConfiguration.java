package com.nvminh162.jobhunter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * Mục đích của việc khai báo @Bean
 * Để sử dụng Dependencies injection (khởi tạo ứng dụng sẽ tạo class gán vào ...)
 * (Không cần khởi tạo đối tượng nhiều lần)
 */

/*
 * Can thiệp trước khi chạy vào Controller phải đi qua bước Interceptor
 */

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
    @Value("${api.version}")
    private String apiVersion;

    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Không check interceptor
        String[] whiteList = {
                "/",
                "/storage/**",
                "/api/" + apiVersion + "/auth/**",
                "/api/" + apiVersion + "/companies/**",
                "/api/" + apiVersion + "/jobs/**",
                "/api/" + apiVersion + "/skills/**",
                "/api/" + apiVersion + "/files"
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}
