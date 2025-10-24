package com.nlp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * NLP请求对象
 */
@Data
public class NlpRequest {
    
    /**
     * 输入的关键词或文本
     */
    @NotBlank(message = "关键词不能为空")
    @Size(max = 10000, message = "文本长度不能超过10000字符")
    private String keyword;
    
    /**
     * 语言类型 (可选: zh-中文, en-英文, auto-自动检测)
     */
    private String language = "auto";
    
    /**
     * 是否启用所有功能 (默认true)
     */
    private Boolean enableAll = true;
    
    /**
     * 是否启用分词
     */
    private Boolean enableTokenization = true;
    
    /**
     * 是否启用纠错
     */
    private Boolean enableSpellCheck = true;
    
    /**
     * 是否启用大小写归一化
     */
    private Boolean enableNormalization = true;
    
    /**
     * 是否启用缩写还原
     */
    private Boolean enableAbbreviationExpansion = true;
    
    /**
     * 是否启用表情符号处理
     */
    private Boolean enableEmojiProcessing = true;
    
    /**
     * 是否启用命名实体识别
     */
    private Boolean enableNer = true;
}

