package com.example.graphql.dto;

import lombok.Data;

/**
 * 更新作者输入DTO
 */
@Data
public class UpdateAuthorInput {
    private Long id;
    private String name;
    private String email;
    private String bio;
}

