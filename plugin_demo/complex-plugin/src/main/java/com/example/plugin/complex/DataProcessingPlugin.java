package com.example.plugin.complex;

import com.example.plugin.api.Plugin;
import com.example.plugin.complex.model.DataRecord;
import com.example.plugin.complex.model.ProcessingResult;
import com.example.plugin.complex.service.DataAnalysisService;
import com.example.plugin.complex.service.DataTransformService;
import com.example.plugin.complex.util.DataValidator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 复杂数据处理插件
 * 
 * 支持的操作：
 * - validate: 数据验证
 * - clean: 数据清洗
 * - transform: 数据转换
 * - analyze: 数据分析
 * - report: 生成报告
 */
public class DataProcessingPlugin implements Plugin {
    
    private final ObjectMapper objectMapper;
    private final DataTransformService transformService;
    private final DataAnalysisService analysisService;
    private boolean started = false;
    
    public DataProcessingPlugin() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.transformService = new DataTransformService();
        this.analysisService = new DataAnalysisService();
    }
    
    @Override
    public String getName() {
        return "Data Processing Plugin";
    }
    
    @Override
    public String getVersion() {
        return "2.0.0";
    }
    
    @Override
    public String getDescription() {
        return "高级数据处理插件，提供数据验证、清洗、转换、分析和报告生成功能";
    }
    
    @Override
    public void start() {
        this.started = true;
        System.out.println("数据处理插件已启动 - 版本: " + getVersion());
        System.out.println("支持的操作: validate, clean, transform, analyze, report");
    }
    
    @Override
    public void stop() {
        this.started = false;
        System.out.println("数据处理插件已停止");
    }
    
    @Override
    public Object execute(Object input) {
        if (!started) {
            throw new RuntimeException("插件未启动");
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            ProcessingResult result = new ProcessingResult();
            
            if (input == null) {
                return generateUsageGuide();
            }
            
            // 解析输入参数
            JsonNode inputNode = parseInput(input);
            String operation = getOperation(inputNode);
            
            switch (operation.toLowerCase()) {
                case "validate":
                    result = validateData(inputNode);
                    break;
                case "clean":
                    result = cleanData(inputNode);
                    break;
                case "transform":
                    result = transformData(inputNode);
                    break;
                case "analyze":
                    result = analyzeData(inputNode);
                    break;
                case "report":
                    result = generateReport(inputNode);
                    break;
                case "demo":
                    result = generateDemoData();
                    break;
                default:
                    result.setSuccess(false);
                    result.setMessage("不支持的操作: " + operation + "。支持的操作: validate, clean, transform, analyze, report, demo");
            }
            
            long endTime = System.currentTimeMillis();
            result.setProcessingTimeMs(endTime - startTime);
            
            return result;
            
        } catch (Exception e) {
            ProcessingResult errorResult = new ProcessingResult(false, "处理失败: " + e.getMessage());
            errorResult.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            errorResult.setErrors(Arrays.asList(e.getMessage()));
            return errorResult;
        }
    }
    
    /**
     * 数据验证操作
     */
    private ProcessingResult validateData(JsonNode inputNode) throws Exception {
        ProcessingResult result = new ProcessingResult();
        
        List<DataRecord> records = parseDataRecords(inputNode);
        result.setInputCount(records.size());
        
        List<String> allErrors = new ArrayList<>();
        List<String> allWarnings = new ArrayList<>();
        
        for (int i = 0; i < records.size(); i++) {
            List<String> recordErrors = DataValidator.validateRecord(records.get(i));
            for (String error : recordErrors) {
                allErrors.add("记录[" + i + "]: " + error);
            }
            
            // 添加警告
            if (!DataValidator.isDataComplete(records.get(i))) {
                allWarnings.add("记录[" + i + "]: 数据不完整");
            }
        }
        
        result.setErrors(allErrors);
        result.setWarnings(allWarnings);
        result.setOutputCount(records.size());
        result.setSuccess(allErrors.isEmpty());
        result.setMessage(allErrors.isEmpty() ? 
            "数据验证通过，共验证 " + records.size() + " 条记录" : 
            "数据验证失败，发现 " + allErrors.size() + " 个错误");
        
        // 添加验证统计
        Map<String, Object> validationStats = new HashMap<>();
        validationStats.put("totalRecords", records.size());
        validationStats.put("validRecords", records.size() - allErrors.size());
        validationStats.put("errorCount", allErrors.size());
        validationStats.put("warningCount", allWarnings.size());
        result.setStatistics(validationStats);
        
        return result;
    }
    
    /**
     * 数据清洗操作
     */
    private ProcessingResult cleanData(JsonNode inputNode) throws Exception {
        ProcessingResult result = new ProcessingResult();
        
        List<DataRecord> records = parseDataRecords(inputNode);
        result.setInputCount(records.size());
        
        // 执行清洗操作
        List<DataRecord> cleanedRecords = transformService.cleanData(records);
        List<DataRecord> deduplicatedRecords = transformService.deduplicateData(cleanedRecords);
        
        result.setData(deduplicatedRecords);
        result.setOutputCount(deduplicatedRecords.size());
        result.setMessage("数据清洗完成，从 " + records.size() + " 条记录清洗为 " + deduplicatedRecords.size() + " 条有效记录");
        
        // 添加清洗统计
        Map<String, Object> cleaningStats = new HashMap<>();
        cleaningStats.put("originalCount", records.size());
        cleaningStats.put("cleanedCount", cleanedRecords.size());
        cleaningStats.put("finalCount", deduplicatedRecords.size());
        cleaningStats.put("removedCount", records.size() - deduplicatedRecords.size());
        cleaningStats.put("removalRate", (double) (records.size() - deduplicatedRecords.size()) / records.size());
        result.setStatistics(cleaningStats);
        
        return result;
    }
    
    /**
     * 数据转换操作
     */
    private ProcessingResult transformData(JsonNode inputNode) throws Exception {
        ProcessingResult result = new ProcessingResult();
        
        List<DataRecord> records = parseDataRecords(inputNode);
        result.setInputCount(records.size());
        
        // 获取转换参数
        String transformType = inputNode.has("transformType") ? 
            inputNode.get("transformType").asText() : "sort";
        
        List<DataRecord> transformedRecords;
        
        switch (transformType.toLowerCase()) {
            case "sort":
                String sortBy = inputNode.has("sortBy") ? inputNode.get("sortBy").asText() : "value";
                boolean ascending = !inputNode.has("ascending") || inputNode.get("ascending").asBoolean();
                transformedRecords = transformService.sortData(records, sortBy, ascending);
                break;
                
            case "filter":
                Map<String, Object> filters = parseFilters(inputNode);
                transformedRecords = transformService.filterData(records, filters);
                break;
                
            case "aggregate":
                String groupBy = inputNode.has("groupBy") ? inputNode.get("groupBy").asText() : "category";
                String aggregateFunction = inputNode.has("aggregateFunction") ? 
                    inputNode.get("aggregateFunction").asText() : "avg";
                transformedRecords = transformService.aggregateData(records, groupBy, aggregateFunction);
                break;
                
            case "normalize":
                String normalizationType = inputNode.has("normalizationType") ? 
                    inputNode.get("normalizationType").asText() : "min_max";
                transformedRecords = transformService.normalizeData(records, normalizationType);
                break;
                
            default:
                throw new IllegalArgumentException("不支持的转换类型: " + transformType);
        }
        
        result.setData(transformedRecords);
        result.setOutputCount(transformedRecords.size());
        result.setMessage("数据转换完成，使用 " + transformType + " 方式处理了 " + records.size() + " 条记录");
        
        // 添加转换统计
        Map<String, Object> transformStats = new HashMap<>();
        transformStats.put("transformType", transformType);
        transformStats.put("inputCount", records.size());
        transformStats.put("outputCount", transformedRecords.size());
        result.setStatistics(transformStats);
        
        return result;
    }
    
    /**
     * 数据分析操作
     */
    private ProcessingResult analyzeData(JsonNode inputNode) throws Exception {
        ProcessingResult result = new ProcessingResult();
        
        List<DataRecord> records = parseDataRecords(inputNode);
        result.setInputCount(records.size());
        
        // 执行分析
        Map<String, Object> analysis = analysisService.performFullAnalysis(records);
        
        result.setStatistics(analysis);
        result.setOutputCount(records.size());
        result.setMessage("数据分析完成，分析了 " + records.size() + " 条记录");
        
        return result;
    }
    
    /**
     * 生成报告操作
     */
    private ProcessingResult generateReport(JsonNode inputNode) throws Exception {
        ProcessingResult result = new ProcessingResult();
        
        List<DataRecord> records = parseDataRecords(inputNode);
        result.setInputCount(records.size());
        
        String reportFormat = inputNode.has("format") ? 
            inputNode.get("format").asText() : "summary";
        
        Map<String, Object> report = analysisService.generateReport(records, reportFormat);
        
        result.setStatistics(report);
        result.setOutputCount(records.size());
        result.setMessage("报告生成完成，格式: " + reportFormat);
        
        return result;
    }
    
    /**
     * 生成演示数据
     */
    private ProcessingResult generateDemoData() {
        ProcessingResult result = new ProcessingResult();
        
        List<DataRecord> demoRecords = createDemoData();
        
        result.setData(demoRecords);
        result.setInputCount(0);
        result.setOutputCount(demoRecords.size());
        result.setMessage("生成了 " + demoRecords.size() + " 条演示数据");
        
        Map<String, Object> demoStats = new HashMap<>();
        demoStats.put("recordCount", demoRecords.size());
        demoStats.put("categories", demoRecords.stream()
            .map(DataRecord::getCategory)
            .distinct()
            .collect(Collectors.toList()));
        result.setStatistics(demoStats);
        
        return result;
    }
    
    // 辅助方法
    
    private JsonNode parseInput(Object input) throws Exception {
        if (input instanceof String) {
            return objectMapper.readTree((String) input);
        } else {
            return objectMapper.valueToTree(input);
        }
    }
    
    private String getOperation(JsonNode inputNode) {
        return inputNode.has("operation") ? 
            inputNode.get("operation").asText() : "analyze";
    }
    
    private List<DataRecord> parseDataRecords(JsonNode inputNode) throws Exception {
        if (!inputNode.has("data")) {
            throw new IllegalArgumentException("缺少 'data' 字段");
        }
        
        JsonNode dataNode = inputNode.get("data");
        return objectMapper.convertValue(dataNode, new TypeReference<List<DataRecord>>() {});
    }
    
    private Map<String, Object> parseFilters(JsonNode inputNode) {
        Map<String, Object> filters = new HashMap<>();
        
        if (inputNode.has("filters")) {
            JsonNode filtersNode = inputNode.get("filters");
            filtersNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                
                if (value.isTextual()) {
                    filters.put(key, value.asText());
                } else if (value.isNumber()) {
                    filters.put(key, value.asDouble());
                } else if (value.isBoolean()) {
                    filters.put(key, value.asBoolean());
                }
            });
        }
        
        return filters;
    }
    
    private List<DataRecord> createDemoData() {
        List<DataRecord> demoData = new ArrayList<>();
        Random random = new Random();
        String[] categories = {"销售", "市场", "技术", "客服", "财务"};
        String[] names = {"产品A", "产品B", "产品C", "服务X", "服务Y", "项目1", "项目2"};
        
        for (int i = 1; i <= 20; i++) {
            DataRecord record = new DataRecord();
            record.setId("demo_" + String.format("%03d", i));
            record.setName(names[random.nextInt(names.length)] + "_" + i);
            record.setValue(10.0 + random.nextDouble() * 90.0); // 10-100之间的随机数
            record.setCategory(categories[random.nextInt(categories.length)]);
            record.setTimestamp(LocalDateTime.now().minusDays(random.nextInt(30)));
            record.setTags(new String[]{"demo", "sample", categories[random.nextInt(categories.length)]});
            
            // 添加一些元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("source", "demo_generator");
            metadata.put("priority", random.nextInt(5) + 1);
            metadata.put("region", random.nextBoolean() ? "北方" : "南方");
            record.setMetadata(metadata);
            
            demoData.add(record);
        }
        
        return demoData;
    }
    
    private Map<String, Object> generateUsageGuide() {
        Map<String, Object> guide = new HashMap<>();
        
        guide.put("pluginName", getName());
        guide.put("version", getVersion());
        guide.put("description", getDescription());
        
        Map<String, Object> operations = new HashMap<>();
        
        // 验证操作示例
        Map<String, Object> validateExample = new HashMap<>();
        validateExample.put("description", "验证数据的完整性和正确性");
        validateExample.put("example", Map.of(
            "operation", "validate",
            "data", Arrays.asList(
                Map.of("id", "001", "name", "测试数据", "value", 100.0, "category", "测试")
            )
        ));
        operations.put("validate", validateExample);
        
        // 清洗操作示例
        Map<String, Object> cleanExample = new HashMap<>();
        cleanExample.put("description", "清洗和去重数据");
        cleanExample.put("example", Map.of(
            "operation", "clean",
            "data", Arrays.asList(
                Map.of("id", "001", "name", "测试数据", "value", 100.0, "category", "测试")
            )
        ));
        operations.put("clean", cleanExample);
        
        // 转换操作示例
        Map<String, Object> transformExample = new HashMap<>();
        transformExample.put("description", "数据转换（排序、过滤、聚合、标准化）");
        transformExample.put("example", Map.of(
            "operation", "transform",
            "transformType", "sort",
            "sortBy", "value",
            "ascending", true,
            "data", Arrays.asList(
                Map.of("id", "001", "name", "测试数据", "value", 100.0, "category", "测试")
            )
        ));
        operations.put("transform", transformExample);
        
        // 分析操作示例
        Map<String, Object> analyzeExample = new HashMap<>();
        analyzeExample.put("description", "执行完整的数据分析");
        analyzeExample.put("example", Map.of(
            "operation", "analyze",
            "data", Arrays.asList(
                Map.of("id", "001", "name", "测试数据", "value", 100.0, "category", "测试")
            )
        ));
        operations.put("analyze", analyzeExample);
        
        // 报告操作示例
        Map<String, Object> reportExample = new HashMap<>();
        reportExample.put("description", "生成数据报告");
        reportExample.put("example", Map.of(
            "operation", "report",
            "format", "summary",
            "data", Arrays.asList(
                Map.of("id", "001", "name", "测试数据", "value", 100.0, "category", "测试")
            )
        ));
        operations.put("report", reportExample);
        
        // 演示数据示例
        Map<String, Object> demoExample = new HashMap<>();
        demoExample.put("description", "生成演示数据");
        demoExample.put("example", Map.of("operation", "demo"));
        operations.put("demo", demoExample);
        
        guide.put("operations", operations);
        
        return guide;
    }
}
