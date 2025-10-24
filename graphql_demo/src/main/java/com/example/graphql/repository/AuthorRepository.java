package com.example.graphql.repository;

import com.example.graphql.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 作者数据访问层
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    /**
     * 根据邮箱查找作者
     */
    Optional<Author> findByEmail(String email);
    
    /**
     * 根据名称模糊查询作者
     */
    List<Author> findByNameContainingIgnoreCase(String name);
}

