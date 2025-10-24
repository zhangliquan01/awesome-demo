package com.example.plugin.complex.util;

import com.example.plugin.complex.model.DataRecord;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 数据验证工具类
 */
public class DataValidator {
    
    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5A-Za-z0-9\\s_-]+$");
    
    /**
     * 验证单个数据记录
     */
    public static List<String> validateRecord(DataRecord record) {
        List<String> errors = new ArrayList<>();
        
        if (record == null) {
            errors.add("数据记录不能为空");
            return errors;
        }
        
        // 验证ID
        if (StringUtils.isBlank(record.getId())) {
            errors.add("ID不能为空");
        } else if (!ID_PATTERN.matcher(record.getId()).matches()) {
            errors.add("ID格式无效，只能包含字母、数字、下划线和连字符");
        }
        
        // 验证名称
        if (StringUtils.isBlank(record.getName())) {
            errors.add("名称不能为空");
        } else if (record.getName().length() > 100) {
            errors.add("名称长度不能超过100个字符");
        } else if (!NAME_PATTERN.matcher(record.getName()).matches()) {
            errors.add("名称包含非法字符");
        }
        
        // 验证数值
        if (record.getValue() == null) {
            errors.add("数值不能为空");
        } else if (record.getValue().isNaN() || record.getValue().isInfinite()) {
            errors.add("数值必须是有效的数字");
        } else if (record.getValue() < 0) {
            errors.add("数值不能为负数");
        }
        
        // 验证分类
        if (StringUtils.isBlank(record.getCategory())) {
            errors.add("分类不能为空");
        } else if (record.getCategory().length() > 50) {
            errors.add("分类长度不能超过50个字符");
        }
        
        // 验证时间戳
        if (record.getTimestamp() == null) {
            errors.add("时间戳不能为空");
        }
        
        // 验证标签
        if (record.getTags() != null) {
            for (String tag : record.getTags()) {
                if (StringUtils.isBlank(tag)) {
                    errors.add("标签不能为空");
                } else if (tag.length() > 30) {
                    errors.add("标签长度不能超过30个字符: " + tag);
                }
            }
        }
        
        return errors;
    }
    
    /**
     * 验证数据记录列表
     */
    public static List<String> validateRecords(List<DataRecord> records) {
        List<String> allErrors = new ArrayList<>();
        
        if (records == null || records.isEmpty()) {
            allErrors.add("数据记录列表不能为空");
            return allErrors;
        }
        
        if (records.size() > 10000) {
            allErrors.add("数据记录数量不能超过10000条");
        }
        
        for (int i = 0; i < records.size(); i++) {
            List<String> recordErrors = validateRecord(records.get(i));
            for (String error : recordErrors) {
                allErrors.add("记录[" + i + "]: " + error);
            }
        }
        
        return allErrors;
    }
    
    /**
     * 检查数据完整性
     */
    public static boolean isDataComplete(DataRecord record) {
        return record != null 
            && StringUtils.isNotBlank(record.getId())
            && StringUtils.isNotBlank(record.getName())
            && record.getValue() != null
            && StringUtils.isNotBlank(record.getCategory())
            && record.getTimestamp() != null;
    }
    
    /**
     * 清理和标准化数据
     */
    public static DataRecord cleanAndNormalize(DataRecord record) {
        if (record == null) {
            return null;
        }
        
        DataRecord cleaned = new DataRecord();
        
        // 清理ID
        if (record.getId() != null) {
            cleaned.setId(record.getId().trim().toLowerCase());
        }
        
        // 清理名称
        if (record.getName() != null) {
            cleaned.setName(record.getName().trim());
        }
        
        // 标准化数值
        if (record.getValue() != null) {
            // 保留两位小数
            cleaned.setValue(Math.round(record.getValue() * 100.0) / 100.0);
        }
        
        // 清理分类
        if (record.getCategory() != null) {
            cleaned.setCategory(record.getCategory().trim().toLowerCase());
        }
        
        // 保持时间戳
        cleaned.setTimestamp(record.getTimestamp());
        
        // 清理标签
        if (record.getTags() != null) {
            String[] cleanedTags = new String[record.getTags().length];
            for (int i = 0; i < record.getTags().length; i++) {
                if (record.getTags()[i] != null) {
                    cleanedTags[i] = record.getTags()[i].trim().toLowerCase();
                }
            }
            cleaned.setTags(cleanedTags);
        }
        
        // 保持元数据
        cleaned.setMetadata(record.getMetadata());
        
        return cleaned;
    }
}
