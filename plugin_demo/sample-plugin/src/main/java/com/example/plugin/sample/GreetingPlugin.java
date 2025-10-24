package com.example.plugin.sample;

import com.example.plugin.api.Plugin;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 问候插件示例
 */
public class GreetingPlugin implements Plugin {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private boolean started = false;
    
    @Override
    public String getName() {
        return "Greeting Plugin";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public String getDescription() {
        return "一个友好的问候插件，可以生成个性化的问候语";
    }
    
    @Override
    public void start() {
        this.started = true;
        System.out.println("问候插件已启动");
    }
    
    @Override
    public void stop() {
        this.started = false;
        System.out.println("问候插件已停止");
    }
    
    @Override
    public Object execute(Object input) {
        if (!started) {
            throw new RuntimeException("插件未启动");
        }
        
        try {
            Map<String, Object> result = new HashMap<>();
            String name = "朋友";
            String language = "zh";
            
            if (input != null) {
                // 解析输入参数
                JsonNode jsonInput;
                if (input instanceof String) {
                    String inputStr = (String) input;
                    if (!inputStr.trim().startsWith("{")) {
                        // 如果不是JSON格式，直接作为名字使用
                        name = inputStr.trim();
                    } else {
                        jsonInput = objectMapper.readTree(inputStr);
                        if (jsonInput.has("name")) {
                            name = jsonInput.get("name").asText();
                        }
                        if (jsonInput.has("language")) {
                            language = jsonInput.get("language").asText();
                        }
                    }
                } else {
                    jsonInput = objectMapper.valueToTree(input);
                    if (jsonInput.has("name")) {
                        name = jsonInput.get("name").asText();
                    }
                    if (jsonInput.has("language")) {
                        language = jsonInput.get("language").asText();
                    }
                }
            }
            
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // 根据时间生成问候语
            String greeting = generateGreeting(name, language, now.getHour());
            
            result.put("greeting", greeting);
            result.put("name", name);
            result.put("language", language);
            result.put("timestamp", timestamp);
            result.put("hour", now.getHour());
            
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", true);
            errorResult.put("message", "问候生成失败: " + e.getMessage());
            errorResult.put("usage", "格式: {\"name\": \"您的名字\", \"language\": \"zh|en\"} 或直接输入名字");
            return errorResult;
        }
    }
    
    private String generateGreeting(String name, String language, int hour) {
        String timeGreeting;
        String mainGreeting;
        
        if ("en".equalsIgnoreCase(language)) {
            // 英文问候
            if (hour < 6) {
                timeGreeting = "Good night";
            } else if (hour < 12) {
                timeGreeting = "Good morning";
            } else if (hour < 18) {
                timeGreeting = "Good afternoon";
            } else {
                timeGreeting = "Good evening";
            }
            mainGreeting = String.format("%s, %s! Welcome to the Hot Plugin System!", timeGreeting, name);
        } else {
            // 中文问候
            if (hour < 6) {
                timeGreeting = "夜深了";
            } else if (hour < 9) {
                timeGreeting = "早上好";
            } else if (hour < 12) {
                timeGreeting = "上午好";
            } else if (hour < 14) {
                timeGreeting = "中午好";
            } else if (hour < 18) {
                timeGreeting = "下午好";
            } else {
                timeGreeting = "晚上好";
            }
            mainGreeting = String.format("%s，%s！欢迎使用热插拔插件系统！", timeGreeting, name);
        }
        
        return mainGreeting;
    }
}
