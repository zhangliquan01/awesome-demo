package com.example.graphql.dto;

import lombok.Data;

/**
 * 更新图书输入DTO
 */
@Data
public class UpdateBookInput {
    private Long id;
    private String title;
    private String isbn;
    private String description;
    private Long authorId;
    private Double price;
    private Integer publishYear;
}

