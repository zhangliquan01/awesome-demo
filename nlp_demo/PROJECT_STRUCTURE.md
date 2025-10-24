# 项目结构说明

## 目录结构

```
nlp_demo/
├── pom.xml                          # Maven项目配置文件
├── README.md                        # 项目说明文档
├── QUICKSTART.md                    # 快速开始指南
├── PROJECT_STRUCTURE.md             # 项目结构说明（本文件）
├── .gitignore                       # Git忽略文件配置
├── api-examples.json                # API请求示例
├── start.sh                         # 启动脚本
├── test-api.sh                      # API测试脚本
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── nlp/
        │           ├── NlpDemoApplication.java      # Spring Boot主类
        │           ├── config/
        │           │   └── NlpConfig.java           # NLP配置类
        │           ├── controller/
        │           │   └── NlpController.java       # REST API控制器
        │           ├── service/
        │           │   └── NlpService.java          # NLP业务逻辑服务
        │           ├── model/
        │           │   ├── NlpRequest.java          # 请求实体类
        │           │   └── NlpResponse.java         # 响应实体类
        │           └── exception/
        │               └── GlobalExceptionHandler.java  # 全局异常处理
        └── resources/
            └── application.yml                      # 应用配置文件
```

## 核心组件说明

### 1. 主类 (NlpDemoApplication.java)

**路径**: `src/main/java/com/nlp/NlpDemoApplication.java`

**功能**:
- Spring Boot应用程序入口
- 启动时打印服务信息

**关键代码**:
```java
@SpringBootApplication
public class NlpDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(NlpDemoApplication.class, args);
    }
}
```

### 2. 配置类 (NlpConfig.java)

**路径**: `src/main/java/com/nlp/config/NlpConfig.java`

**功能**:
- 初始化Stanford CoreNLP（英文和中文）
- 初始化LanguageTool（拼写检查）
- Bean管理和依赖注入

**核心Bean**:
- `stanfordCoreNlpEnglish`: 英文NLP处理器
- `stanfordCoreNlpChinese`: 中文NLP处理器
- `languageToolEnglish`: 英文拼写检查器
- `languageToolChinese`: 中文检查器

### 3. 控制器 (NlpController.java)

**路径**: `src/main/java/com/nlp/controller/NlpController.java`

**功能**:
- 提供RESTful API接口
- 处理HTTP请求和响应
- 参数验证

**API端点**:
| 端点 | 方法 | 功能 |
|------|------|------|
| `/api/nlp/process` | POST | 完整NLP处理 |
| `/api/nlp/tokenize` | POST | 分词 |
| `/api/nlp/spell-check` | POST | 拼写检查 |
| `/api/nlp/ner` | POST | 命名实体识别 |
| `/api/nlp/emoji` | POST | 表情符号处理 |
| `/api/nlp/health` | GET | 健康检查 |
| `/api/nlp/help` | GET | 帮助文档 |

### 4. 服务层 (NlpService.java)

**路径**: `src/main/java/com/nlp/service/NlpService.java`

**功能**:
- 核心NLP业务逻辑
- 集成多个NLP库
- 文本处理和分析

**核心方法**:
- `process()`: 主处理方法
- `tokenize()`: 分词
- `checkSpelling()`: 拼写检查
- `normalizeCase()`: 大小写归一化
- `expandAbbreviations()`: 缩写还原
- `processEmojis()`: 表情符号处理
- `recognizeNamedEntities()`: 命名实体识别
- `detectLanguage()`: 语言检测

**集成的NLP工具**:
- **HanLP**: 中文分词和词性标注
- **Stanford CoreNLP**: 英文/中文NLP处理
- **LanguageTool**: 拼写和语法检查
- **Emoji-Java**: 表情符号处理

### 5. 请求实体 (NlpRequest.java)

**路径**: `src/main/java/com/nlp/model/NlpRequest.java`

**功能**:
- 定义API请求参数
- 参数验证注解

**字段说明**:
```java
- keyword: 输入文本（必填）
- language: 语言类型（可选，默认auto）
- enableAll: 启用所有功能（默认true）
- enableTokenization: 启用分词
- enableSpellCheck: 启用拼写检查
- enableNormalization: 启用归一化
- enableAbbreviationExpansion: 启用缩写还原
- enableEmojiProcessing: 启用表情处理
- enableNer: 启用命名实体识别
```

### 6. 响应实体 (NlpResponse.java)

**路径**: `src/main/java/com/nlp/model/NlpResponse.java`

**功能**:
- 定义API响应结构
- 包含所有NLP处理结果

**响应字段**:
```java
- originalText: 原始文本
- detectedLanguage: 检测到的语言
- tokens: 分词结果
- tokenDetails: 词性标注详情
- spellCheck: 拼写检查结果
- normalizedText: 归一化文本
- expandedText: 缩写还原文本
- emojiResult: 表情符号处理结果
- namedEntities: 命名实体列表
- processingTime: 处理耗时
```

### 7. 异常处理 (GlobalExceptionHandler.java)

**路径**: `src/main/java/com/nlp/exception/GlobalExceptionHandler.java`

**功能**:
- 全局异常捕获
- 统一错误响应格式
- 参数验证错误处理

### 8. 配置文件 (application.yml)

**路径**: `src/main/resources/application.yml`

**配置项**:
```yaml
server.port: 服务端口
nlp.language-detection-threshold: 语言检测阈值
nlp.enable-cache: 缓存开关
nlp.max-text-length: 最大文本长度
```

## 依赖说明

### 核心依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| spring-boot-starter-web | 3.2.0 | Web框架 |
| hanlp | 1.8.4 | 中文NLP |
| stanford-corenlp | 4.5.5 | 英文/中文NLP |
| languagetool-core | 6.3 | 拼写检查 |
| emoji-java | 5.1.1 | 表情处理 |
| commons-text | 1.11.0 | 文本工具 |
| lombok | latest | 简化代码 |

### 模型文件

- **stanford-corenlp-models**: 英文基础模型
- **stanford-corenlp-models-chinese**: 中文模型
- **language-en**: 英文语言规则
- **language-zh**: 中文语言规则

## 数据流

```
用户请求 (JSON)
    ↓
NlpController (参数验证)
    ↓
NlpService (业务处理)
    ↓
┌─────────────────┐
│  语言检测        │
├─────────────────┤
│  分词           │ → HanLP / Stanford CoreNLP
├─────────────────┤
│  词性标注        │ → HanLP / Stanford CoreNLP
├─────────────────┤
│  拼写检查        │ → LanguageTool
├─────────────────┤
│  大小写归一化    │ → 自定义实现
├─────────────────┤
│  缩写还原        │ → 自定义词典
├─────────────────┤
│  表情处理        │ → Emoji-Java
├─────────────────┤
│  命名实体识别    │ → Stanford CoreNLP
└─────────────────┘
    ↓
NlpResponse (JSON)
    ↓
返回给用户
```

## 扩展指南

### 添加新功能

1. 在 `NlpService.java` 中添加新方法
2. 在 `NlpResponse.java` 中添加结果字段
3. 在 `NlpRequest.java` 中添加开关参数
4. 在 `NlpController.java` 中添加新端点（可选）

### 添加新语言支持

1. 在 `pom.xml` 中添加对应语言的依赖
2. 在 `NlpConfig.java` 中初始化对应Bean
3. 在 `NlpService.java` 中添加语言判断逻辑

### 性能优化

1. **缓存策略**: 实现结果缓存（Redis等）
2. **异步处理**: 使用 `@Async` 注解
3. **模型优化**: 按需加载模型
4. **资源池**: 使用对象池管理NLP实例

## 测试

### 单元测试结构（可扩展）

```
src/test/java/
└── com/
    └── nlp/
        ├── service/
        │   └── NlpServiceTest.java
        ├── controller/
        │   └── NlpControllerTest.java
        └── integration/
            └── NlpIntegrationTest.java
```

### 使用测试脚本

```bash
# 运行所有API测试
./test-api.sh

# 使用Maven运行单元测试
mvn test
```

## 部署建议

### 开发环境
```bash
mvn spring-boot:run
```

### 生产环境
```bash
# 构建
mvn clean package -DskipTests

# 运行
java -Xmx4g -Xms2g -jar target/nlp-demo-1.0.0.jar

# Docker部署（可扩展）
docker build -t nlp-demo .
docker run -p 8080:8080 -m 4g nlp-demo
```

## 常见问题

### Q: 如何修改服务端口？
A: 修改 `application.yml` 中的 `server.port` 配置

### Q: 如何添加自定义词典？
A: 在 `NlpService.java` 中扩展 `ABBREVIATIONS` 映射

### Q: 如何禁用某个功能？
A: 在请求中设置对应的 `enable*` 参数为 `false`

### Q: 模型文件在哪里？
A: 首次运行时自动下载到 `~/.m2/repository/` Maven仓库

---

**更多问题请参考 [README.md](README.md) 或提交 Issue**

