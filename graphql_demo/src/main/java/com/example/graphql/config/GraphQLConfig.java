package com.example.graphql.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * GraphQL配置类
 * 修复GraphiQL重定向问题
 */
@Configuration
public class GraphQLConfig implements WebMvcConfigurer {
    
    /**
     * 配置CORS，解决跨域问题
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
    
    /**
     * 注册GraphiQL过滤器，拦截重定向
     * 这是修复重定向问题的关键！
     */
    @Bean
    public FilterRegistrationBean<GraphiQLRedirectFilter> graphiqlRedirectFilter() {
        FilterRegistrationBean<GraphiQLRedirectFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new GraphiQLRedirectFilter());
        registrationBean.addUrlPatterns("/graphiql");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE); // 最高优先级
        return registrationBean;
    }
    
    /**
     * GraphiQL重定向过滤器
     * 拦截对/graphiql的请求，直接转发到带参数的路径，避免重定向
     */
    static class GraphiQLRedirectFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, 
                                       HttpServletResponse response, 
                                       FilterChain filterChain) throws ServletException, IOException {
            // 如果是访问/graphiql且没有参数
            if ("/graphiql".equals(request.getRequestURI()) && request.getQueryString() == null) {
                // 使用请求转发（内部转发），而不是重定向
                request.getRequestDispatcher("/graphiql?path=/graphql").forward(request, response);
                return;
            }
            filterChain.doFilter(request, response);
        }
    }
}

