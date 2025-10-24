package com.nlp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * NLP响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NlpResponse {
    
    /**
     * 原始输入
     */
    private String originalText;
    
    /**
     * 检测到的语言
     */
    private String detectedLanguage;
    
    /**
     * 分词结果
     */
    private List<String> tokens;
    
    /**
     * 词性标注结果
     */
    private List<TokenInfo> tokenDetails;
    
    /**
     * 纠错结果
     */
    private SpellCheckResult spellCheck;
    
    /**
     * 大小写归一化结果
     */
    private String normalizedText;
    
    /**
     * 缩写还原结果
     */
    private String expandedText;
    
    /**
     * 表情符号处理结果
     */
    private EmojiResult emojiResult;
    
    /**
     * 命名实体识别结果
     */
    private List<NamedEntity> namedEntities;
    
    /**
     * 处理时间(毫秒)
     */
    private Long processingTime;
    
    /**
     * Token信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenInfo {
        private String word;
        private String pos; // 词性
        private String ner; // 命名实体标签
    }
    
    /**
     * 拼写检查结果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpellCheckResult {
        private String correctedText;
        private List<SpellError> errors;
        private Boolean hasErrors;
    }
    
    /**
     * 拼写错误
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpellError {
        private String original;
        private List<String> suggestions;
        private String message;
        private Integer position;
    }
    
    /**
     * 表情符号结果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmojiResult {
        private String textWithoutEmojis;
        private List<EmojiInfo> emojis;
        private Integer emojiCount;
    }
    
    /**
     * 表情符号信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmojiInfo {
        private String emoji;
        private String description;
        private String unicode;
    }
    
    /**
     * 命名实体
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NamedEntity {
        private String text;
        private String type; // PERSON, LOCATION, ORGANIZATION, DATE, etc.
        private Integer startPosition;
        private Integer endPosition;
    }
}

