package com.example.plugin.complex.util;

import com.example.plugin.complex.model.DataRecord;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计计算工具类
 */
public class StatisticsCalculator {
    
    /**
     * 计算基础统计信息
     */
    public static Map<String, Object> calculateBasicStatistics(List<DataRecord> records) {
        Map<String, Object> stats = new HashMap<>();
        
        if (records == null || records.isEmpty()) {
            stats.put("count", 0);
            return stats;
        }
        
        // 提取数值
        List<Double> values = records.stream()
            .filter(r -> r.getValue() != null)
            .map(DataRecord::getValue)
            .collect(Collectors.toList());
        
        if (values.isEmpty()) {
            stats.put("count", records.size());
            stats.put("validValues", 0);
            return stats;
        }
        
        DescriptiveStatistics descriptive = new DescriptiveStatistics();
        values.forEach(descriptive::addValue);
        
        stats.put("count", records.size());
        stats.put("validValues", values.size());
        stats.put("sum", descriptive.getSum());
        stats.put("mean", descriptive.getMean());
        stats.put("median", descriptive.getPercentile(50));
        stats.put("min", descriptive.getMin());
        stats.put("max", descriptive.getMax());
        stats.put("standardDeviation", descriptive.getStandardDeviation());
        stats.put("variance", descriptive.getVariance());
        stats.put("range", descriptive.getMax() - descriptive.getMin());
        
        // 分位数
        stats.put("q1", descriptive.getPercentile(25));
        stats.put("q3", descriptive.getPercentile(75));
        stats.put("iqr", descriptive.getPercentile(75) - descriptive.getPercentile(25));
        
        return stats;
    }
    
    /**
     * 按分类计算统计信息
     */
    public static Map<String, Object> calculateCategoryStatistics(List<DataRecord> records) {
        Map<String, Object> categoryStats = new HashMap<>();
        
        if (records == null || records.isEmpty()) {
            return categoryStats;
        }
        
        // 按分类分组
        Map<String, List<DataRecord>> groupedByCategory = records.stream()
            .filter(r -> r.getCategory() != null)
            .collect(Collectors.groupingBy(DataRecord::getCategory));
        
        // 计算每个分类的统计信息
        Map<String, Map<String, Object>> categoryDetails = new HashMap<>();
        for (Map.Entry<String, List<DataRecord>> entry : groupedByCategory.entrySet()) {
            String category = entry.getKey();
            List<DataRecord> categoryRecords = entry.getValue();
            
            Map<String, Object> categoryBasicStats = calculateBasicStatistics(categoryRecords);
            categoryDetails.put(category, categoryBasicStats);
        }
        
        categoryStats.put("categories", categoryDetails);
        categoryStats.put("categoryCount", groupedByCategory.size());
        
        // 计算分类分布
        Map<String, Integer> categoryDistribution = new HashMap<>();
        for (Map.Entry<String, List<DataRecord>> entry : groupedByCategory.entrySet()) {
            categoryDistribution.put(entry.getKey(), entry.getValue().size());
        }
        categoryStats.put("distribution", categoryDistribution);
        
        return categoryStats;
    }
    
    /**
     * 计算时间序列统计
     */
    public static Map<String, Object> calculateTimeSeriesStatistics(List<DataRecord> records) {
        Map<String, Object> timeStats = new HashMap<>();
        
        if (records == null || records.isEmpty()) {
            return timeStats;
        }
        
        // 按日期分组
        Map<String, List<DataRecord>> groupedByDate = records.stream()
            .filter(r -> r.getTimestamp() != null)
            .collect(Collectors.groupingBy(r -> r.getTimestamp().toLocalDate().toString()));
        
        timeStats.put("dateCount", groupedByDate.size());
        
        // 每日统计
        Map<String, Map<String, Object>> dailyStats = new HashMap<>();
        for (Map.Entry<String, List<DataRecord>> entry : groupedByDate.entrySet()) {
            String date = entry.getKey();
            List<DataRecord> dayRecords = entry.getValue();
            
            Map<String, Object> dayStats = calculateBasicStatistics(dayRecords);
            dailyStats.put(date, dayStats);
        }
        timeStats.put("dailyStatistics", dailyStats);
        
        // 时间范围
        Optional<DataRecord> earliest = records.stream()
            .filter(r -> r.getTimestamp() != null)
            .min(Comparator.comparing(DataRecord::getTimestamp));
        
        Optional<DataRecord> latest = records.stream()
            .filter(r -> r.getTimestamp() != null)
            .max(Comparator.comparing(DataRecord::getTimestamp));
        
        if (earliest.isPresent() && latest.isPresent()) {
            timeStats.put("earliestTimestamp", earliest.get().getTimestamp());
            timeStats.put("latestTimestamp", latest.get().getTimestamp());
        }
        
        return timeStats;
    }
    
    /**
     * 检测异常值
     */
    public static Map<String, Object> detectOutliers(List<DataRecord> records) {
        Map<String, Object> outlierInfo = new HashMap<>();
        
        if (records == null || records.isEmpty()) {
            return outlierInfo;
        }
        
        List<Double> values = records.stream()
            .filter(r -> r.getValue() != null)
            .map(DataRecord::getValue)
            .collect(Collectors.toList());
        
        if (values.size() < 4) {
            outlierInfo.put("outliers", new ArrayList<>());
            outlierInfo.put("outlierCount", 0);
            return outlierInfo;
        }
        
        DescriptiveStatistics stats = new DescriptiveStatistics();
        values.forEach(stats::addValue);
        
        double q1 = stats.getPercentile(25);
        double q3 = stats.getPercentile(75);
        double iqr = q3 - q1;
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;
        
        List<DataRecord> outliers = records.stream()
            .filter(r -> r.getValue() != null)
            .filter(r -> r.getValue() < lowerBound || r.getValue() > upperBound)
            .collect(Collectors.toList());
        
        outlierInfo.put("outliers", outliers);
        outlierInfo.put("outlierCount", outliers.size());
        outlierInfo.put("lowerBound", lowerBound);
        outlierInfo.put("upperBound", upperBound);
        outlierInfo.put("q1", q1);
        outlierInfo.put("q3", q3);
        outlierInfo.put("iqr", iqr);
        
        return outlierInfo;
    }
    
    /**
     * 计算相关性分析
     */
    public static Map<String, Object> calculateCorrelations(List<DataRecord> records) {
        Map<String, Object> correlationInfo = new HashMap<>();
        
        if (records == null || records.isEmpty()) {
            return correlationInfo;
        }
        
        // 按分类分组并计算分类间的数值相关性
        Map<String, List<Double>> categoryValues = records.stream()
            .filter(r -> r.getCategory() != null && r.getValue() != null)
            .collect(Collectors.groupingBy(
                DataRecord::getCategory,
                Collectors.mapping(DataRecord::getValue, Collectors.toList())
            ));
        
        correlationInfo.put("categoryValueDistribution", categoryValues);
        
        // 计算每个分类的平均值
        Map<String, Double> categoryAverages = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : categoryValues.entrySet()) {
            double average = entry.getValue().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
            categoryAverages.put(entry.getKey(), average);
        }
        correlationInfo.put("categoryAverages", categoryAverages);
        
        return correlationInfo;
    }
}
