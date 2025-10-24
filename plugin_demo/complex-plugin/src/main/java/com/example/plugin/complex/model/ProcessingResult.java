package com.example.plugin.complex.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据处理结果模型
 */
public class ProcessingResult {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("processedAt")
    private LocalDateTime processedAt;
    
    @JsonProperty("inputCount")
    private int inputCount;
    
    @JsonProperty("outputCount")
    private int outputCount;
    
    @JsonProperty("processingTimeMs")
    private long processingTimeMs;
    
    @JsonProperty("data")
    private List<DataRecord> data;
    
    @JsonProperty("statistics")
    private Map<String, Object> statistics;
    
    @JsonProperty("errors")
    private List<String> errors;
    
    @JsonProperty("warnings")
    private List<String> warnings;
    
    public ProcessingResult() {
        this.processedAt = LocalDateTime.now();
        this.success = true;
    }
    
    public ProcessingResult(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
    
    public int getInputCount() {
        return inputCount;
    }
    
    public void setInputCount(int inputCount) {
        this.inputCount = inputCount;
    }
    
    public int getOutputCount() {
        return outputCount;
    }
    
    public void setOutputCount(int outputCount) {
        this.outputCount = outputCount;
    }
    
    public long getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
    
    public List<DataRecord> getData() {
        return data;
    }
    
    public void setData(List<DataRecord> data) {
        this.data = data;
    }
    
    public Map<String, Object> getStatistics() {
        return statistics;
    }
    
    public void setStatistics(Map<String, Object> statistics) {
        this.statistics = statistics;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public List<String> getWarnings() {
        return warnings;
    }
    
    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}
