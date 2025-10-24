package com.example.graphql.dto;

import lombok.Data;

/**
 * 创建作者输入DTO
 */
@Data
public class CreateAuthorInput {
    private String name;
    private String email;
    private String bio;
}

