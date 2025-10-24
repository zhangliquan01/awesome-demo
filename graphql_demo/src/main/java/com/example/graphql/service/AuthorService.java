package com.example.graphql.service;

import com.example.graphql.entity.Author;
import com.example.graphql.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 作者服务层
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthorService {
    
    private final AuthorRepository authorRepository;
    
    /**
     * 获取所有作者
     */
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }
    
    /**
     * 根据ID获取作者
     */
    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }
    
    /**
     * 根据名称搜索作者
     */
    public List<Author> searchAuthorsByName(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * 创建作者
     */
    public Author createAuthor(String name, String email, String bio) {
        // 检查邮箱是否已存在
        if (authorRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("邮箱已被使用: " + email);
        }
        
        Author author = new Author(name, email, bio);
        return authorRepository.save(author);
    }
    
    /**
     * 更新作者
     */
    public Author updateAuthor(Long id, String name, String email, String bio) {
        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("作者不存在，ID: " + id));
        
        // 如果邮箱有变化，检查新邮箱是否已被使用
        if (email != null && !email.equals(author.getEmail())) {
            if (authorRepository.findByEmail(email).isPresent()) {
                throw new RuntimeException("邮箱已被使用: " + email);
            }
            author.setEmail(email);
        }
        
        if (name != null) {
            author.setName(name);
        }
        
        if (bio != null) {
            author.setBio(bio);
        }
        
        return authorRepository.save(author);
    }
    
    /**
     * 删除作者
     */
    public boolean deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("作者不存在，ID: " + id);
        }
        authorRepository.deleteById(id);
        return true;
    }
}

