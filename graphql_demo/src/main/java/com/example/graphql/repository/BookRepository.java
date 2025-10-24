package com.example.graphql.repository;

import com.example.graphql.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 图书数据访问层
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    /**
     * 根据ISBN查找图书
     */
    Optional<Book> findByIsbn(String isbn);
    
    /**
     * 根据标题模糊查询图书
     */
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    /**
     * 根据作者ID查询图书
     */
    List<Book> findByAuthorId(Long authorId);
}

