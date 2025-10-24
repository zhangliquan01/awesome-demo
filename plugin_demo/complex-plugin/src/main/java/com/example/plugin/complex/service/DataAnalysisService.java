package com.example.plugin.complex.service;

import com.example.plugin.complex.model.DataRecord;
import com.example.plugin.complex.util.StatisticsCalculator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据分析服务
 */
public class DataAnalysisService {
    
    /**
     * 执行完整的数据分析
     */
    public Map<String, Object> performFullAnalysis(List<DataRecord> records) {
        Map<String, Object> analysis = new HashMap<>();
        
        if (records == null || records.isEmpty()) {
            analysis.put("error", "没有数据可供分析");
            return analysis;
        }
        
        // 基础统计
        analysis.put("basicStatistics", StatisticsCalculator.calculateBasicStatistics(records));
        
        // 分类统计
        analysis.put("categoryStatistics", StatisticsCalculator.calculateCategoryStatistics(records));
        
        // 时间序列统计
        analysis.put("timeSeriesStatistics", StatisticsCalculator.calculateTimeSeriesStatistics(records));
        
        // 异常值检测
        analysis.put("outlierAnalysis", StatisticsCalculator.detectOutliers(records));
        
        // 相关性分析
        analysis.put("correlationAnalysis", StatisticsCalculator.calculateCorrelations(records));
        
        // 数据质量分析
        analysis.put("dataQuality", analyzeDataQuality(records));
        
        // 趋势分析
        analysis.put("trendAnalysis", analyzeTrends(records));
        
        return analysis;
    }
    
    /**
     * 数据质量分析
     */
    public Map<String, Object> analyzeDataQuality(List<DataRecord> records) {
        Map<String, Object> quality = new HashMap<>();
        
        if (records == null || records.isEmpty()) {
            quality.put("totalRecords", 0);
            return quality;
        }
        
        int totalRecords = records.size();
        
        // 完整性分析
        long completeRecords = records.stream()
            .filter(r -> r.getId() != null && r.getName() != null && 
                        r.getValue() != null && r.getCategory() != null && 
                        r.getTimestamp() != null)
            .count();
        
        // 空值统计
        long nullIds = records.stream().filter(r -> r.getId() == null).count();
        long nullNames = records.stream().filter(r -> r.getName() == null).count();
        long nullValues = records.stream().filter(r -> r.getValue() == null).count();
        long nullCategories = records.stream().filter(r -> r.getCategory() == null).count();
        long nullTimestamps = records.stream().filter(r -> r.getTimestamp() == null).count();
        
        // 重复数据检测
        Set<String> uniqueIds = new HashSet<>();
        long duplicateIds = records.stream()
            .filter(r -> r.getId() != null)
            .map(DataRecord::getId)
            .filter(id -> !uniqueIds.add(id))
            .count();
        
        // 数据一致性检查
        Map<String, Object> consistency = analyzeConsistency(records);
        
        quality.put("totalRecords", totalRecords);
        quality.put("completeRecords", completeRecords);
        quality.put("completenessRate", (double) completeRecords / totalRecords);
        
        Map<String, Long> nullCounts = new HashMap<>();
        nullCounts.put("nullIds", nullIds);
        nullCounts.put("nullNames", nullNames);
        nullCounts.put("nullValues", nullValues);
        nullCounts.put("nullCategories", nullCategories);
        nullCounts.put("nullTimestamps", nullTimestamps);
        quality.put("nullCounts", nullCounts);
        
        quality.put("uniqueIds", uniqueIds.size());
        quality.put("duplicateIds", duplicateIds);
        quality.put("consistency", consistency);
        
        // 计算质量分数 (0-100)
        double qualityScore = calculateQualityScore(totalRecords, completeRecords, duplicateIds, consistency);
        quality.put("qualityScore", qualityScore);
        
        return quality;
    }
    
    /**
     * 趋势分析
     */
    public Map<String, Object> analyzeTrends(List<DataRecord> records) {
        Map<String, Object> trends = new HashMap<>();
        
        if (records == null || records.size() < 2) {
            trends.put("error", "数据不足，无法进行趋势分析");
            return trends;
        }
        
        // 按时间排序
        List<DataRecord> sortedRecords = records.stream()
            .filter(r -> r.getTimestamp() != null && r.getValue() != null)
            .sorted(Comparator.comparing(DataRecord::getTimestamp))
            .collect(Collectors.toList());
        
        if (sortedRecords.size() < 2) {
            trends.put("error", "有效数据不足，无法进行趋势分析");
            return trends;
        }
        
        // 计算总体趋势
        double firstValue = sortedRecords.get(0).getValue();
        double lastValue = sortedRecords.get(sortedRecords.size() - 1).getValue();
        double overallChange = lastValue - firstValue;
        double overallChangePercent = firstValue != 0 ? (overallChange / firstValue) * 100 : 0;
        
        trends.put("overallTrend", overallChange > 0 ? "上升" : overallChange < 0 ? "下降" : "平稳");
        trends.put("overallChange", overallChange);
        trends.put("overallChangePercent", overallChangePercent);
        trends.put("firstValue", firstValue);
        trends.put("lastValue", lastValue);
        
        // 计算移动平均
        List<Double> movingAverages = calculateMovingAverage(sortedRecords, 3);
        trends.put("movingAverages", movingAverages);
        
        // 波动性分析
        double volatility = calculateVolatility(sortedRecords);
        trends.put("volatility", volatility);
        
        // 按分类的趋势分析
        Map<String, Map<String, Object>> categoryTrends = analyzeCategoryTrends(records);
        trends.put("categoryTrends", categoryTrends);
        
        return trends;
    }
    
    /**
     * 生成数据报告
     */
    public Map<String, Object> generateReport(List<DataRecord> records, String format) {
        Map<String, Object> report = new HashMap<>();
        
        if (records == null || records.isEmpty()) {
            report.put("error", "没有数据可供生成报告");
            return report;
        }
        
        Map<String, Object> analysis = performFullAnalysis(records);
        
        switch (format.toLowerCase()) {
            case "summary":
                report = generateSummaryReport(records, analysis);
                break;
            case "detailed":
                report = generateDetailedReport(records, analysis);
                break;
            case "csv":
                report = generateCSVReport(records);
                break;
            default:
                report = generateSummaryReport(records, analysis);
        }
        
        return report;
    }
    
    // 私有辅助方法
    
    private Map<String, Object> analyzeConsistency(List<DataRecord> records) {
        Map<String, Object> consistency = new HashMap<>();
        
        // 检查数值范围的一致性
        List<Double> values = records.stream()
            .filter(r -> r.getValue() != null)
            .map(DataRecord::getValue)
            .collect(Collectors.toList());
        
        if (!values.isEmpty()) {
            double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            double range = max - min;
            
            // 检查是否有异常的大值或小值
            long extremeValues = values.stream()
                .filter(v -> v < min + range * 0.05 || v > max - range * 0.05)
                .count();
            
            consistency.put("valueRange", Map.of("min", min, "max", max, "range", range));
            consistency.put("extremeValues", extremeValues);
        }
        
        // 检查分类的一致性
        Map<String, Long> categoryFrequency = records.stream()
            .filter(r -> r.getCategory() != null)
            .collect(Collectors.groupingBy(DataRecord::getCategory, Collectors.counting()));
        
        consistency.put("categoryDistribution", categoryFrequency);
        consistency.put("categoryCount", categoryFrequency.size());
        
        return consistency;
    }
    
    private double calculateQualityScore(int total, long complete, long duplicates, Map<String, Object> consistency) {
        if (total == 0) return 0;
        
        // 完整性权重 50%
        double completenessScore = ((double) complete / total) * 50;
        
        // 唯一性权重 30%
        double uniquenessScore = (1.0 - (double) duplicates / total) * 30;
        
        // 一致性权重 20%
        double consistencyScore = 20; // 简化处理，实际应该基于consistency数据计算
        
        return Math.max(0, Math.min(100, completenessScore + uniquenessScore + consistencyScore));
    }
    
    private List<Double> calculateMovingAverage(List<DataRecord> sortedRecords, int window) {
        List<Double> movingAverages = new ArrayList<>();
        
        for (int i = 0; i <= sortedRecords.size() - window; i++) {
            double sum = 0;
            for (int j = i; j < i + window; j++) {
                sum += sortedRecords.get(j).getValue();
            }
            movingAverages.add(sum / window);
        }
        
        return movingAverages;
    }
    
    private double calculateVolatility(List<DataRecord> sortedRecords) {
        if (sortedRecords.size() < 2) return 0;
        
        List<Double> changes = new ArrayList<>();
        for (int i = 1; i < sortedRecords.size(); i++) {
            double change = sortedRecords.get(i).getValue() - sortedRecords.get(i - 1).getValue();
            changes.add(Math.abs(change));
        }
        
        return changes.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }
    
    private Map<String, Map<String, Object>> analyzeCategoryTrends(List<DataRecord> records) {
        Map<String, Map<String, Object>> categoryTrends = new HashMap<>();
        
        Map<String, List<DataRecord>> groupedByCategory = records.stream()
            .filter(r -> r.getCategory() != null)
            .collect(Collectors.groupingBy(DataRecord::getCategory));
        
        for (Map.Entry<String, List<DataRecord>> entry : groupedByCategory.entrySet()) {
            String category = entry.getKey();
            List<DataRecord> categoryRecords = entry.getValue();
            
            if (categoryRecords.size() >= 2) {
                Map<String, Object> categoryTrend = analyzeTrends(categoryRecords);
                categoryTrends.put(category, categoryTrend);
            }
        }
        
        return categoryTrends;
    }
    
    private Map<String, Object> generateSummaryReport(List<DataRecord> records, Map<String, Object> analysis) {
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("reportType", "summary");
        summary.put("generatedAt", new Date());
        summary.put("recordCount", records.size());
        
        // 提取关键统计信息
        Map<String, Object> basicStats = (Map<String, Object>) analysis.get("basicStatistics");
        if (basicStats != null) {
            summary.put("totalValue", basicStats.get("sum"));
            summary.put("averageValue", basicStats.get("mean"));
            summary.put("minValue", basicStats.get("min"));
            summary.put("maxValue", basicStats.get("max"));
        }
        
        // 数据质量摘要
        Map<String, Object> quality = (Map<String, Object>) analysis.get("dataQuality");
        if (quality != null) {
            summary.put("qualityScore", quality.get("qualityScore"));
            summary.put("completenessRate", quality.get("completenessRate"));
        }
        
        return summary;
    }
    
    private Map<String, Object> generateDetailedReport(List<DataRecord> records, Map<String, Object> analysis) {
        Map<String, Object> detailed = new HashMap<>();
        
        detailed.put("reportType", "detailed");
        detailed.put("generatedAt", new Date());
        detailed.put("fullAnalysis", analysis);
        detailed.put("sampleData", records.stream().limit(10).collect(Collectors.toList()));
        
        return detailed;
    }
    
    private Map<String, Object> generateCSVReport(List<DataRecord> records) {
        Map<String, Object> csvReport = new HashMap<>();
        
        try {
            StringWriter writer = new StringWriter();
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                "ID", "Name", "Value", "Category", "Timestamp"
            ));
            
            for (DataRecord record : records) {
                printer.printRecord(
                    record.getId(),
                    record.getName(),
                    record.getValue(),
                    record.getCategory(),
                    record.getTimestamp() != null ? 
                        record.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : ""
                );
            }
            
            printer.close();
            
            csvReport.put("reportType", "csv");
            csvReport.put("content", writer.toString());
            csvReport.put("recordCount", records.size());
            
        } catch (Exception e) {
            csvReport.put("error", "生成CSV报告失败: " + e.getMessage());
        }
        
        return csvReport;
    }
}
