package com.example.graphql.service;

import com.example.graphql.entity.Author;
import com.example.graphql.entity.Book;
import com.example.graphql.repository.AuthorRepository;
import com.example.graphql.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 图书服务层
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    
    /**
     * 获取所有图书
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    /**
     * 根据ID获取图书
     */
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    
    /**
     * 根据标题搜索图书
     */
    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     * 根据作者ID获取图书
     */
    public List<Book> getBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }
    
    /**
     * 创建图书
     */
    public Book createBook(String title, String isbn, String description, 
                          Long authorId, Double price, Integer publishYear) {
        // 检查ISBN是否已存在
        if (bookRepository.findByIsbn(isbn).isPresent()) {
            throw new RuntimeException("ISBN已存在: " + isbn);
        }
        
        // 获取作者
        Author author = authorRepository.findById(authorId)
            .orElseThrow(() -> new RuntimeException("作者不存在，ID: " + authorId));
        
        Book book = new Book(title, isbn, description, author, price, publishYear);
        return bookRepository.save(book);
    }
    
    /**
     * 更新图书
     */
    public Book updateBook(Long id, String title, String isbn, String description,
                          Long authorId, Double price, Integer publishYear) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("图书不存在，ID: " + id));
        
        if (title != null) {
            book.setTitle(title);
        }
        
        // 如果ISBN有变化，检查新ISBN是否已被使用
        if (isbn != null && !isbn.equals(book.getIsbn())) {
            if (bookRepository.findByIsbn(isbn).isPresent()) {
                throw new RuntimeException("ISBN已存在: " + isbn);
            }
            book.setIsbn(isbn);
        }
        
        if (description != null) {
            book.setDescription(description);
        }
        
        if (authorId != null) {
            Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("作者不存在，ID: " + authorId));
            book.setAuthor(author);
        }
        
        if (price != null) {
            book.setPrice(price);
        }
        
        if (publishYear != null) {
            book.setPublishYear(publishYear);
        }
        
        return bookRepository.save(book);
    }
    
    /**
     * 删除图书
     */
    public boolean deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("图书不存在，ID: " + id);
        }
        bookRepository.deleteById(id);
        return true;
    }
}

