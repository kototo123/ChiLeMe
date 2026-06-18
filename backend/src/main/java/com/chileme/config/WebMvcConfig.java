package com.chileme.config;

import com.chileme.common.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtUtils jwtUtils;

    private static final List<String> EXCLUDE_PATHS = List.of(
            "/user/login", "/user/wx-login", "/ai/**"
    );

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                String path = request.getRequestURI();
                if (EXCLUDE_PATHS.stream().anyMatch(path::contains)) {
                    return true;
                }
                String token = request.getHeader("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    try {
                        Long userId = jwtUtils.getUserId(token.substring(7));
                        request.setAttribute("userId", userId);
                    } catch (Exception ignored) {
                    }
                }
                return true;
            }
        }).addPathPatterns("/**");
    }
}
