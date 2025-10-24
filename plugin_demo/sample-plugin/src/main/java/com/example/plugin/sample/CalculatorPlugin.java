package com.example.plugin.sample;

import com.example.plugin.api.Plugin;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 计算器插件示例
 */
public class CalculatorPlugin implements Plugin {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private boolean started = false;
    
    @Override
    public String getName() {
        return "Calculator Plugin";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public String getDescription() {
        return "一个简单的计算器插件，支持基本的数学运算";
    }
    
    @Override
    public void start() {
        this.started = true;
        System.out.println("计算器插件已启动");
    }
    
    @Override
    public void stop() {
        this.started = false;
        System.out.println("计算器插件已停止");
    }
    
    @Override
    public Object execute(Object input) {
        if (!started) {
            throw new RuntimeException("插件未启动");
        }
        
        try {
            Map<String, Object> result = new HashMap<>();
            
            if (input == null) {
                // 如果没有输入，返回使用说明
                result.put("message", "计算器插件使用说明");
                result.put("usage", "发送JSON格式: {\"operation\": \"add|subtract|multiply|divide\", \"a\": 数字1, \"b\": 数字2}");
                result.put("example", "{\"operation\": \"add\", \"a\": 10, \"b\": 5}");
                return result;
            }
            
            // 解析输入参数
            JsonNode jsonInput;
            if (input instanceof String) {
                jsonInput = objectMapper.readTree((String) input);
            } else {
                jsonInput = objectMapper.valueToTree(input);
            }
            
            String operation = jsonInput.get("operation").asText();
            double a = jsonInput.get("a").asDouble();
            double b = jsonInput.get("b").asDouble();
            
            double calculationResult;
            switch (operation.toLowerCase()) {
                case "add":
                    calculationResult = a + b;
                    break;
                case "subtract":
                    calculationResult = a - b;
                    break;
                case "multiply":
                    calculationResult = a * b;
                    break;
                case "divide":
                    if (b == 0) {
                        throw new RuntimeException("除数不能为零");
                    }
                    calculationResult = a / b;
                    break;
                default:
                    throw new RuntimeException("不支持的操作: " + operation + "。支持的操作: add, subtract, multiply, divide");
            }
            
            result.put("operation", operation);
            result.put("operand1", a);
            result.put("operand2", b);
            result.put("result", calculationResult);
            result.put("expression", String.format("%.2f %s %.2f = %.2f", a, getOperationSymbol(operation), b, calculationResult));
            
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", true);
            errorResult.put("message", "计算失败: " + e.getMessage());
            errorResult.put("usage", "正确格式: {\"operation\": \"add|subtract|multiply|divide\", \"a\": 数字1, \"b\": 数字2}");
            return errorResult;
        }
    }
    
    private String getOperationSymbol(String operation) {
        switch (operation.toLowerCase()) {
            case "add": return "+";
            case "subtract": return "-";
            case "multiply": return "×";
            case "divide": return "÷";
            default: return operation;
        }
    }
}
