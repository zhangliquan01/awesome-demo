package com.example.graphql.resolver;

import com.example.graphql.dto.CreateAuthorInput;
import com.example.graphql.dto.CreateBookInput;
import com.example.graphql.dto.UpdateAuthorInput;
import com.example.graphql.dto.UpdateBookInput;
import com.example.graphql.entity.Author;
import com.example.graphql.entity.Book;
import com.example.graphql.service.AuthorService;
import com.example.graphql.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL变更解析器
 */
@Controller
@RequiredArgsConstructor
public class MutationResolver {
    
    private final AuthorService authorService;
    private final BookService bookService;
    
    /**
     * 创建作者
     */
    @MutationMapping
    public Author createAuthor(@Argument CreateAuthorInput input) {
        return authorService.createAuthor(
            input.getName(),
            input.getEmail(),
            input.getBio()
        );
    }
    
    /**
     * 更新作者
     */
    @MutationMapping
    public Author updateAuthor(@Argument UpdateAuthorInput input) {
        return authorService.updateAuthor(
            input.getId(),
            input.getName(),
            input.getEmail(),
            input.getBio()
        );
    }
    
    /**
     * 删除作者
     */
    @MutationMapping
    public Boolean deleteAuthor(@Argument Long id) {
        return authorService.deleteAuthor(id);
    }
    
    /**
     * 创建图书
     */
    @MutationMapping
    public Book createBook(@Argument CreateBookInput input) {
        return bookService.createBook(
            input.getTitle(),
            input.getIsbn(),
            input.getDescription(),
            input.getAuthorId(),
            input.getPrice(),
            input.getPublishYear()
        );
    }
    
    /**
     * 更新图书
     */
    @MutationMapping
    public Book updateBook(@Argument UpdateBookInput input) {
        return bookService.updateBook(
            input.getId(),
            input.getTitle(),
            input.getIsbn(),
            input.getDescription(),
            input.getAuthorId(),
            input.getPrice(),
            input.getPublishYear()
        );
    }
    
    /**
     * 删除图书
     */
    @MutationMapping
    public Boolean deleteBook(@Argument Long id) {
        return bookService.deleteBook(id);
    }
}

