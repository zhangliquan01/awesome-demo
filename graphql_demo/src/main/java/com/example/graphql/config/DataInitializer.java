package com.example.graphql.config;

import com.example.graphql.entity.Author;
import com.example.graphql.entity.Book;
import com.example.graphql.repository.AuthorRepository;
import com.example.graphql.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器 - 用于演示
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    
    @Override
    public void run(String... args) {
        // 创建作者
        Author author1 = new Author("张三", "zhangsan@example.com", "资深Java开发者，专注于Spring生态系统");
        Author author2 = new Author("李四", "lisi@example.com", "全栈工程师，热爱前端和后端技术");
        Author author3 = new Author("王五", "wangwu@example.com", "架构师，擅长微服务和分布式系统设计");
        
        authorRepository.save(author1);
        authorRepository.save(author2);
        authorRepository.save(author3);
        
        // 创建图书
        Book book1 = new Book(
            "Spring Boot实战",
            "978-7-111-12345-1",
            "深入浅出讲解Spring Boot框架的实战应用",
            author1,
            89.00,
            2023
        );
        
        Book book2 = new Book(
            "GraphQL权威指南",
            "978-7-111-12345-2",
            "全面介绍GraphQL的原理和最佳实践",
            author1,
            99.00,
            2023
        );
        
        Book book3 = new Book(
            "微服务架构设计",
            "978-7-111-12345-3",
            "微服务架构的设计模式和实践经验",
            author3,
            108.00,
            2022
        );
        
        Book book4 = new Book(
            "React前端开发",
            "978-7-111-12345-4",
            "现代React开发技术和工程化实践",
            author2,
            79.00,
            2024
        );
        
        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        
        System.out.println("初始化数据完成！");
        System.out.println("- 已创建 " + authorRepository.count() + " 位作者");
        System.out.println("- 已创建 " + bookRepository.count() + " 本图书");
    }
}

