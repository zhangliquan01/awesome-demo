package com.example.graphql.dto;

import lombok.Data;

/**
 * 创建图书输入DTO
 */
@Data
public class CreateBookInput {
    private String title;
    private String isbn;
    private String description;
    private Long authorId;
    private Double price;
    private Integer publishYear;
}

