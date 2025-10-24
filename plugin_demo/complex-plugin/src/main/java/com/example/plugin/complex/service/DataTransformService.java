package com.example.plugin.complex.service;

import com.example.plugin.complex.model.DataRecord;
import com.example.plugin.complex.util.DataValidator;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据转换服务
 */
public class DataTransformService {
    
    /**
     * 数据清洗
     */
    public List<DataRecord> cleanData(List<DataRecord> records) {
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        
        return records.stream()
            .filter(Objects::nonNull)
            .map(DataValidator::cleanAndNormalize)
            .filter(DataValidator::isDataComplete)
            .collect(Collectors.toList());
    }
    
    /**
     * 数据去重
     */
    public List<DataRecord> deduplicateData(List<DataRecord> records) {
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 基于ID去重
        Map<String, DataRecord> uniqueRecords = new LinkedHashMap<>();
        for (DataRecord record : records) {
            if (record != null && StringUtils.isNotBlank(record.getId())) {
                // 如果已存在相同ID，保留时间戳更新的记录
                DataRecord existing = uniqueRecords.get(record.getId());
                if (existing == null || 
                    (record.getTimestamp() != null && existing.getTimestamp() != null && 
                     record.getTimestamp().isAfter(existing.getTimestamp()))) {
                    uniqueRecords.put(record.getId(), record);
                }
            }
        }
        
        return new ArrayList<>(uniqueRecords.values());
    }
    
    /**
     * 数据过滤
     */
    public List<DataRecord> filterData(List<DataRecord> records, Map<String, Object> filters) {
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        
        if (filters == null || filters.isEmpty()) {
            return new ArrayList<>(records);
        }
        
        return records.stream()
            .filter(record -> matchesFilters(record, filters))
            .collect(Collectors.toList());
    }
    
    /**
     * 数据排序
     */
    public List<DataRecord> sortData(List<DataRecord> records, String sortBy, boolean ascending) {
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        
        Comparator<DataRecord> comparator;
        
        switch (sortBy.toLowerCase()) {
            case "id":
                comparator = Comparator.comparing(DataRecord::getId, Comparator.nullsLast(String::compareTo));
                break;
            case "name":
                comparator = Comparator.comparing(DataRecord::getName, Comparator.nullsLast(String::compareTo));
                break;
            case "value":
                comparator = Comparator.comparing(DataRecord::getValue, Comparator.nullsLast(Double::compareTo));
                break;
            case "category":
                comparator = Comparator.comparing(DataRecord::getCategory, Comparator.nullsLast(String::compareTo));
                break;
            case "timestamp":
                comparator = Comparator.comparing(DataRecord::getTimestamp, Comparator.nullsLast(LocalDateTime::compareTo));
                break;
            default:
                comparator = Comparator.comparing(DataRecord::getId, Comparator.nullsLast(String::compareTo));
        }
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        return records.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
    }
    
    /**
     * 数据分组
     */
    public Map<String, List<DataRecord>> groupData(List<DataRecord> records, String groupBy) {
        if (records == null || records.isEmpty()) {
            return new HashMap<>();
        }
        
        switch (groupBy.toLowerCase()) {
            case "category":
                return records.stream()
                    .filter(r -> r.getCategory() != null)
                    .collect(Collectors.groupingBy(DataRecord::getCategory));
            
            case "date":
                return records.stream()
                    .filter(r -> r.getTimestamp() != null)
                    .collect(Collectors.groupingBy(r -> 
                        r.getTimestamp().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));
            
            case "value_range":
                return records.stream()
                    .filter(r -> r.getValue() != null)
                    .collect(Collectors.groupingBy(this::getValueRange));
            
            default:
                return Map.of("all", new ArrayList<>(records));
        }
    }
    
    /**
     * 数据聚合
     */
    public List<DataRecord> aggregateData(List<DataRecord> records, String groupBy, String aggregateFunction) {
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        
        Map<String, List<DataRecord>> grouped = groupData(records, groupBy);
        List<DataRecord> aggregated = new ArrayList<>();
        
        for (Map.Entry<String, List<DataRecord>> entry : grouped.entrySet()) {
            String groupKey = entry.getKey();
            List<DataRecord> groupRecords = entry.getValue();
            
            DataRecord aggregatedRecord = performAggregation(groupKey, groupRecords, aggregateFunction);
            if (aggregatedRecord != null) {
                aggregated.add(aggregatedRecord);
            }
        }
        
        return aggregated;
    }
    
    /**
     * 数据标准化
     */
    public List<DataRecord> normalizeData(List<DataRecord> records, String normalizationType) {
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<DataRecord> normalized = new ArrayList<>();
        
        switch (normalizationType.toLowerCase()) {
            case "min_max":
                normalized = normalizeMinMax(records);
                break;
            case "z_score":
                normalized = normalizeZScore(records);
                break;
            case "decimal_scaling":
                normalized = normalizeDecimalScaling(records);
                break;
            default:
                normalized = new ArrayList<>(records);
        }
        
        return normalized;
    }
    
    // 私有辅助方法
    
    private boolean matchesFilters(DataRecord record, Map<String, Object> filters) {
        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            String field = filter.getKey();
            Object value = filter.getValue();
            
            if (!matchesFilter(record, field, value)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean matchesFilter(DataRecord record, String field, Object value) {
        switch (field.toLowerCase()) {
            case "category":
                return Objects.equals(record.getCategory(), value);
            case "min_value":
                return record.getValue() != null && record.getValue() >= ((Number) value).doubleValue();
            case "max_value":
                return record.getValue() != null && record.getValue() <= ((Number) value).doubleValue();
            case "name_contains":
                return record.getName() != null && record.getName().toLowerCase().contains(value.toString().toLowerCase());
            default:
                return true;
        }
    }
    
    private String getValueRange(DataRecord record) {
        if (record.getValue() == null) {
            return "unknown";
        }
        
        double value = record.getValue();
        if (value < 10) return "0-10";
        if (value < 50) return "10-50";
        if (value < 100) return "50-100";
        if (value < 500) return "100-500";
        return "500+";
    }
    
    private DataRecord performAggregation(String groupKey, List<DataRecord> records, String function) {
        if (records.isEmpty()) {
            return null;
        }
        
        DataRecord result = new DataRecord();
        result.setId("agg_" + groupKey);
        result.setName("Aggregated: " + groupKey);
        result.setCategory(groupKey);
        result.setTimestamp(LocalDateTime.now());
        
        List<Double> values = records.stream()
            .filter(r -> r.getValue() != null)
            .map(DataRecord::getValue)
            .collect(Collectors.toList());
        
        if (values.isEmpty()) {
            result.setValue(0.0);
            return result;
        }
        
        switch (function.toLowerCase()) {
            case "sum":
                result.setValue(values.stream().mapToDouble(Double::doubleValue).sum());
                break;
            case "avg":
            case "average":
                result.setValue(values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
                break;
            case "min":
                result.setValue(values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));
                break;
            case "max":
                result.setValue(values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));
                break;
            case "count":
                result.setValue((double) records.size());
                break;
            default:
                result.setValue(values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
        }
        
        return result;
    }
    
    private List<DataRecord> normalizeMinMax(List<DataRecord> records) {
        List<Double> values = records.stream()
            .filter(r -> r.getValue() != null)
            .map(DataRecord::getValue)
            .collect(Collectors.toList());
        
        if (values.isEmpty()) {
            return new ArrayList<>(records);
        }
        
        double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        double range = max - min;
        
        if (range == 0) {
            return new ArrayList<>(records);
        }
        
        return records.stream()
            .map(record -> {
                DataRecord normalized = new DataRecord();
                normalized.setId(record.getId());
                normalized.setName(record.getName());
                normalized.setCategory(record.getCategory());
                normalized.setTimestamp(record.getTimestamp());
                normalized.setTags(record.getTags());
                normalized.setMetadata(record.getMetadata());
                
                if (record.getValue() != null) {
                    double normalizedValue = (record.getValue() - min) / range;
                    normalized.setValue(normalizedValue);
                } else {
                    normalized.setValue(record.getValue());
                }
                
                return normalized;
            })
            .collect(Collectors.toList());
    }
    
    private List<DataRecord> normalizeZScore(List<DataRecord> records) {
        List<Double> values = records.stream()
            .filter(r -> r.getValue() != null)
            .map(DataRecord::getValue)
            .collect(Collectors.toList());
        
        if (values.isEmpty()) {
            return new ArrayList<>(records);
        }
        
        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = values.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .average()
            .orElse(0.0);
        double stdDev = Math.sqrt(variance);
        
        if (stdDev == 0) {
            return new ArrayList<>(records);
        }
        
        return records.stream()
            .map(record -> {
                DataRecord normalized = new DataRecord();
                normalized.setId(record.getId());
                normalized.setName(record.getName());
                normalized.setCategory(record.getCategory());
                normalized.setTimestamp(record.getTimestamp());
                normalized.setTags(record.getTags());
                normalized.setMetadata(record.getMetadata());
                
                if (record.getValue() != null) {
                    double normalizedValue = (record.getValue() - mean) / stdDev;
                    normalized.setValue(normalizedValue);
                } else {
                    normalized.setValue(record.getValue());
                }
                
                return normalized;
            })
            .collect(Collectors.toList());
    }
    
    private List<DataRecord> normalizeDecimalScaling(List<DataRecord> records) {
        List<Double> values = records.stream()
            .filter(r -> r.getValue() != null)
            .map(DataRecord::getValue)
            .collect(Collectors.toList());
        
        if (values.isEmpty()) {
            return new ArrayList<>(records);
        }
        
        double maxAbsValue = values.stream()
            .mapToDouble(Math::abs)
            .max()
            .orElse(1.0);
        
        int j = (int) Math.ceil(Math.log10(maxAbsValue));
        double divisor = Math.pow(10, j);
        
        return records.stream()
            .map(record -> {
                DataRecord normalized = new DataRecord();
                normalized.setId(record.getId());
                normalized.setName(record.getName());
                normalized.setCategory(record.getCategory());
                normalized.setTimestamp(record.getTimestamp());
                normalized.setTags(record.getTags());
                normalized.setMetadata(record.getMetadata());
                
                if (record.getValue() != null) {
                    double normalizedValue = record.getValue() / divisor;
                    normalized.setValue(normalizedValue);
                } else {
                    normalized.setValue(record.getValue());
                }
                
                return normalized;
            })
            .collect(Collectors.toList());
    }
}
