package com.example.graphql.resolver;

import com.example.graphql.entity.Author;
import com.example.graphql.entity.Book;
import com.example.graphql.service.AuthorService;
import com.example.graphql.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL查询解析器
 */
@Controller
@RequiredArgsConstructor
public class QueryResolver {
    
    private final AuthorService authorService;
    private final BookService bookService;
    
    /**
     * 获取所有作者
     */
    @QueryMapping
    public List<Author> allAuthors() {
        return authorService.getAllAuthors();
    }
    
    /**
     * 根据ID获取作者
     */
    @QueryMapping
    public Author authorById(@Argument Long id) {
        return authorService.getAuthorById(id)
            .orElse(null);
    }
    
    /**
     * 根据名称搜索作者
     */
    @QueryMapping
    public List<Author> authorsByName(@Argument String name) {
        return authorService.searchAuthorsByName(name);
    }
    
    /**
     * 获取所有图书
     */
    @QueryMapping
    public List<Book> allBooks() {
        return bookService.getAllBooks();
    }
    
    /**
     * 根据ID获取图书
     */
    @QueryMapping
    public Book bookById(@Argument Long id) {
        return bookService.getBookById(id)
            .orElse(null);
    }
    
    /**
     * 根据标题搜索图书
     */
    @QueryMapping
    public List<Book> booksByTitle(@Argument String title) {
        return bookService.searchBooksByTitle(title);
    }
    
    /**
     * 根据作者ID获取图书
     */
    @QueryMapping
    public List<Book> booksByAuthor(@Argument Long authorId) {
        return bookService.getBooksByAuthor(authorId);
    }
}

