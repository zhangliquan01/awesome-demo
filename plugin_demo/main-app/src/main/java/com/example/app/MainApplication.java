package com.example.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 主应用程序启动类
 */
@SpringBootApplication
public class MainApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
        System.out.println("热插拔插件系统已启动！");
        System.out.println("访问 http://localhost:8080/plugins 查看插件管理界面");
    }
}
