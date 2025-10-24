package com.nlp.controller;

import com.nlp.model.NlpRequest;
import com.nlp.model.NlpResponse;
import com.nlp.service.NlpService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * NLP REST控制器
 */
@RestController
@RequestMapping("/api/nlp")
@CrossOrigin(origins = "*")
public class NlpController {

    private final NlpService nlpService;

    public NlpController(NlpService nlpService) {
        this.nlpService = nlpService;
    }

    /**
     * NLP处理接口
     */
    @PostMapping("/process")
    public ResponseEntity<NlpResponse> process(@Valid @RequestBody NlpRequest request) {
        NlpResponse response = nlpService.process(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 快速分词接口
     */
    @PostMapping("/tokenize")
    public ResponseEntity<Map<String, Object>> tokenize(@RequestBody Map<String, String> request) {
        NlpRequest nlpRequest = new NlpRequest();
        nlpRequest.setKeyword(request.get("keyword"));
        nlpRequest.setLanguage(request.getOrDefault("language", "auto"));
        nlpRequest.setEnableAll(false);
        nlpRequest.setEnableTokenization(true);
        
        NlpResponse response = nlpService.process(nlpRequest);
        
        Map<String, Object> result = new HashMap<>();
        result.put("tokens", response.getTokens());
        result.put("tokenDetails", response.getTokenDetails());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 快速纠错接口
     */
    @PostMapping("/spell-check")
    public ResponseEntity<NlpResponse.SpellCheckResult> spellCheck(@RequestBody Map<String, String> request) {
        NlpRequest nlpRequest = new NlpRequest();
        nlpRequest.setKeyword(request.get("keyword"));
        nlpRequest.setLanguage(request.getOrDefault("language", "auto"));
        nlpRequest.setEnableAll(false);
        nlpRequest.setEnableSpellCheck(true);
        
        NlpResponse response = nlpService.process(nlpRequest);
        return ResponseEntity.ok(response.getSpellCheck());
    }

    /**
     * 命名实体识别接口
     */
    @PostMapping("/ner")
    public ResponseEntity<Map<String, Object>> ner(@RequestBody Map<String, String> request) {
        NlpRequest nlpRequest = new NlpRequest();
        nlpRequest.setKeyword(request.get("keyword"));
        nlpRequest.setLanguage(request.getOrDefault("language", "auto"));
        nlpRequest.setEnableAll(false);
        nlpRequest.setEnableNer(true);
        
        NlpResponse response = nlpService.process(nlpRequest);
        
        Map<String, Object> result = new HashMap<>();
        result.put("namedEntities", response.getNamedEntities());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 表情符号处理接口
     */
    @PostMapping("/emoji")
    public ResponseEntity<NlpResponse.EmojiResult> processEmoji(@RequestBody Map<String, String> request) {
        NlpRequest nlpRequest = new NlpRequest();
        nlpRequest.setKeyword(request.get("keyword"));
        nlpRequest.setEnableAll(false);
        nlpRequest.setEnableEmojiProcessing(true);
        
        NlpResponse response = nlpService.process(nlpRequest);
        return ResponseEntity.ok(response.getEmojiResult());
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "NLP Service");
        status.put("version", "1.0.0");
        return ResponseEntity.ok(status);
    }

    /**
     * API帮助文档
     */
    @GetMapping("/help")
    public ResponseEntity<Map<String, Object>> help() {
        Map<String, Object> helpInfo = new HashMap<>();
        helpInfo.put("service", "NLP工具服务");
        helpInfo.put("version", "1.0.0");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("POST /api/nlp/process", "完整的NLP处理（所有功能）");
        endpoints.put("POST /api/nlp/tokenize", "分词");
        endpoints.put("POST /api/nlp/spell-check", "拼写纠错");
        endpoints.put("POST /api/nlp/ner", "命名实体识别");
        endpoints.put("POST /api/nlp/emoji", "表情符号处理");
        endpoints.put("GET /api/nlp/health", "健康检查");
        endpoints.put("GET /api/nlp/help", "帮助文档");
        
        helpInfo.put("endpoints", endpoints);
        
        Map<String, Object> exampleRequest = new HashMap<>();
        exampleRequest.put("keyword", "I can't believe it's raining today! 😊 Apple Inc. is located in California.");
        exampleRequest.put("language", "auto");
        exampleRequest.put("enableAll", true);
        
        helpInfo.put("exampleRequest", exampleRequest);
        
        Map<String, String> features = new HashMap<>();
        features.put("tokenization", "分词（中英文）");
        features.put("spellCheck", "拼写纠错");
        features.put("normalization", "大小写归一化");
        features.put("abbreviationExpansion", "缩写还原");
        features.put("emojiProcessing", "表情符号处理");
        features.put("ner", "命名实体识别");
        
        helpInfo.put("features", features);
        
        return ResponseEntity.ok(helpInfo);
    }
}

