package com.nlp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * NLP工具应用程序主类
 */
@SpringBootApplication
public class NlpDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NlpDemoApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("NLP工具服务启动成功！");
        System.out.println("访问地址: http://localhost:8080");
        System.out.println("API文档: http://localhost:8080/api/nlp/help");
        System.out.println("===========================================\n");
    }
}

