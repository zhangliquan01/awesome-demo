# 复杂数据处理插件

这是一个功能丰富的数据处理插件，展示了如何构建包含多个文件和组件的复杂插件。

## 📁 项目结构

```
complex-plugin/
├── src/main/java/com/example/plugin/complex/
│   ├── DataProcessingPlugin.java          # 主插件类
│   ├── model/                             # 数据模型
│   │   ├── DataRecord.java               # 数据记录模型
│   │   └── ProcessingResult.java         # 处理结果模型
│   ├── service/                          # 业务服务
│   │   ├── DataTransformService.java     # 数据转换服务
│   │   └── DataAnalysisService.java      # 数据分析服务
│   └── util/                             # 工具类
│       ├── DataValidator.java            # 数据验证工具
│       └── StatisticsCalculator.java     # 统计计算工具
├── src/main/resources/
│   ├── plugin.properties                 # 插件配置文件
│   └── META-INF/MANIFEST.MF             # 插件清单文件
└── pom.xml                              # Maven配置
```

## ✨ 功能特性

### 🔍 数据验证 (validate)
- 数据完整性检查
- 格式验证
- 业务规则验证
- 数据质量评分

### 🧹 数据清洗 (clean)
- 空值处理
- 数据标准化
- 重复数据去除
- 异常值处理

### 🔄 数据转换 (transform)
- **排序**: 按字段排序（升序/降序）
- **过滤**: 基于条件过滤数据
- **聚合**: 分组聚合计算
- **标准化**: Min-Max、Z-Score、小数定标

### 📊 数据分析 (analyze)
- 基础统计信息（均值、中位数、标准差等）
- 分类统计分析
- 时间序列分析
- 异常值检测
- 相关性分析
- 趋势分析

### 📋 报告生成 (report)
- 摘要报告
- 详细报告
- CSV格式导出
- 自定义报告模板

## 🚀 使用方法

### 1. 数据验证示例

```json
{
  "operation": "validate",
  "data": [
    {
      "id": "001",
      "name": "产品A",
      "value": 100.5,
      "category": "销售",
      "timestamp": "2024-01-01T10:00:00"
    }
  ]
}
```

### 2. 数据清洗示例

```json
{
  "operation": "clean",
  "data": [
    {
      "id": "001",
      "name": "  产品A  ",
      "value": 100.567,
      "category": "SALES",
      "timestamp": "2024-01-01T10:00:00"
    }
  ]
}
```

### 3. 数据转换示例

#### 排序
```json
{
  "operation": "transform",
  "transformType": "sort",
  "sortBy": "value",
  "ascending": false,
  "data": [...]
}
```

#### 过滤
```json
{
  "operation": "transform",
  "transformType": "filter",
  "filters": {
    "category": "销售",
    "min_value": 50,
    "max_value": 200
  },
  "data": [...]
}
```

#### 聚合
```json
{
  "operation": "transform",
  "transformType": "aggregate",
  "groupBy": "category",
  "aggregateFunction": "avg",
  "data": [...]
}
```

#### 标准化
```json
{
  "operation": "transform",
  "transformType": "normalize",
  "normalizationType": "min_max",
  "data": [...]
}
```

### 4. 数据分析示例

```json
{
  "operation": "analyze",
  "data": [...]
}
```

### 5. 报告生成示例

```json
{
  "operation": "report",
  "format": "summary",
  "data": [...]
}
```

### 6. 生成演示数据

```json
{
  "operation": "demo"
}
```

## 📈 输出格式

插件返回统一的 `ProcessingResult` 格式：

```json
{
  "success": true,
  "message": "处理完成",
  "processedAt": "2024-01-01T10:00:00",
  "inputCount": 100,
  "outputCount": 95,
  "processingTimeMs": 150,
  "data": [...],
  "statistics": {...},
  "errors": [],
  "warnings": []
}
```

## 🔧 配置选项

插件支持通过 `plugin.properties` 文件进行配置：

- `data.max.records`: 最大处理记录数（默认10000）
- `data.processing.timeout`: 处理超时时间（默认300秒）
- `statistics.enable.advanced`: 启用高级统计（默认true）
- `report.formats`: 支持的报告格式
- `cache.enable`: 启用缓存（默认false）

## 📊 性能特性

- 支持大数据集处理（最多10000条记录）
- 内存优化的流式处理
- 并行计算支持
- 可配置的超时控制
- 详细的性能指标

## 🛠️ 技术栈

- **核心**: Java 11+
- **JSON处理**: Jackson
- **数学计算**: Apache Commons Math
- **CSV处理**: Apache Commons CSV
- **工具库**: Apache Commons Lang
- **构建工具**: Maven + Shade Plugin

## 📝 开发说明

这个插件展示了如何构建复杂的、多组件的插件系统：

1. **模块化设计**: 将功能拆分为多个服务和工具类
2. **数据模型**: 定义清晰的数据结构
3. **服务层**: 实现具体的业务逻辑
4. **工具类**: 提供可复用的工具方法
5. **配置管理**: 支持外部配置
6. **错误处理**: 完善的异常处理机制
7. **性能监控**: 内置性能指标收集

## 🔍 扩展建议

1. **添加更多数据源**: 支持数据库、文件等数据源
2. **机器学习集成**: 添加预测和分类功能
3. **可视化支持**: 生成图表和可视化报告
4. **流式处理**: 支持实时数据流处理
5. **分布式计算**: 支持大规模数据处理
6. **插件热更新**: 支持插件的动态更新

这个复杂插件示例展示了如何构建企业级的插件系统，可以作为开发更复杂插件的参考模板。
