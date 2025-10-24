package com.example.graphql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图书实体类
 */
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String isbn;
    
    @Column(length = 1000)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private Integer publishYear;
    
    public Book(String title, String isbn, String description, Author author, Double price, Integer publishYear) {
        this.title = title;
        this.isbn = isbn;
        this.description = description;
        this.author = author;
        this.price = price;
        this.publishYear = publishYear;
    }
}

