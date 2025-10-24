package com.example.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * GraphQL Demo应用主类
 * 
 * @author GraphQL Demo
 */
@SpringBootApplication
public class GraphQLDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphQLDemoApplication.class, args);
        System.out.println("\n==============================================");
        System.out.println("GraphQL Demo应用启动成功!");
        System.out.println("GraphiQL界面: http://localhost:8080/graphiql");
        System.out.println("GraphQL端点: http://localhost:8080/graphql");
        System.out.println("H2控制台: http://localhost:8080/h2-console");
        System.out.println("==============================================\n");
    }
}

