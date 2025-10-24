# NLP工具服务

一个基于Spring Boot的自然语言处理(NLP)工具，集成了多个开源NLP组件，提供分词、纠错、大小写归一化、缩写还原、表情符号处理和命名实体识别等功能。

## 功能特性

- ✅ **分词（Tokenization）**：支持中英文分词
- ✅ **词性标注（POS Tagging）**：识别词性
- ✅ **拼写纠错（Spell Check）**：自动检测并纠正拼写错误
- ✅ **大小写归一化****（Case Normalization）**：统一文本大小写
- ✅ **缩写还原（Abbreviation Expansion）**：将缩写词还原为完整形式
- ✅ **表情符号处理（Emoji Processing）**：识别、提取和移除表情符号
- ✅ **命名实体识别（NER）**：识别人名、地名、机构名等实体
- ✅ **多语言支持**：支持中文和英文，自动语言检测

## 技术栈

- **Spring Boot 3.2.0** - Web框架
- **Stanford CoreNLP 4.5.5** - 英文和中文NLP处理
- **HanLP 1.8.4** - 中文分词和词性标注
- **LanguageTool 6.3** - 拼写和语法检查
- **Emoji-Java 5.1.1** - 表情符号处理
- **Maven** - 项目管理工具
- **Java 17** - 编程语言

## 快速开始

### 环境要求

- JDK 17 或更高版本
- Maven 3.6+
- 至少 4GB 可用内存（用于加载NLP模型）

### 安装运行

1. **克隆或下载项目**

```bash
cd nlp_demo
```

2. **构建项目**

```bash
mvn clean package
```

3. **运行应用**

```bash
mvn spring-boot:run
```

或者运行打包后的jar文件：

```bash
java -jar target/nlp-demo-1.0.0.jar
```

4. **访问服务**

应用启动后，访问：
- API服务：http://localhost:8080
- 帮助文档：http://localhost:8080/api/nlp/help
- 健康检查：http://localhost:8080/api/nlp/health

## API接口文档

### 1. 完整NLP处理

**接口**: `POST /api/nlp/process`

**功能**: 执行所有NLP处理功能

**请求示例**:

```json
{
  "keyword": "I can't believe it's raining today! 😊 Apple Inc. is located in California.",
  "language": "auto",
  "enableAll": true,
  "enableTokenization": true,
  "enableSpellCheck": true,
  "enableNormalization": true,
  "enableAbbreviationExpansion": true,
  "enableEmojiProcessing": true,
  "enableNer": true
}
```

**参数说明**:
- `keyword` (必填): 要处理的文本
- `language` (可选): 语言类型，可选值 `zh`(中文)、`en`(英文)、`auto`(自动检测)，默认 `auto`
- `enableAll` (可选): 是否启用所有功能，默认 `true`
- `enableTokenization` (可选): 是否启用分词
- `enableSpellCheck` (可选): 是否启用拼写检查
- `enableNormalization` (可选): 是否启用大小写归一化
- `enableAbbreviationExpansion` (可选): 是否启用缩写还原
- `enableEmojiProcessing` (可选): 是否启用表情符号处理
- `enableNer` (可选): 是否启用命名实体识别

**响应示例**:

```json
{
  "originalText": "I can't believe it's raining today! 😊 Apple Inc. is located in California.",
  "detectedLanguage": "en",
  "tokens": ["I", "ca", "n't", "believe", "it", "'s", "raining", "today", "!", "Apple", "Inc.", "is", "located", "in", "California", "."],
  "tokenDetails": [
    {"word": "I", "pos": "PRP", "ner": "O"},
    {"word": "ca", "pos": "MD", "ner": "O"},
    {"word": "n't", "pos": "RB", "ner": "O"}
  ],
  "spellCheck": {
    "correctedText": "I cannot believe it is raining today! 😊 Apple Inc. is located in California.",
    "errors": [],
    "hasErrors": false
  },
  "normalizedText": "i can't believe it's raining today! 😊 apple inc. is located in california.",
  "expandedText": "I cannot believe it is raining today! 😊 Apple Inc. is located in California.",
  "emojiResult": {
    "textWithoutEmojis": "I can't believe it's raining today!  Apple Inc. is located in California.",
    "emojis": [
      {
        "emoji": "😊",
        "description": "smiling face with smiling eyes",
        "unicode": "😊"
      }
    ],
    "emojiCount": 1
  },
  "namedEntities": [
    {
      "text": "Apple Inc.",
      "type": "ORGANIZATION",
      "startPosition": 42,
      "endPosition": 52
    },
    {
      "text": "California",
      "type": "LOCATION",
      "startPosition": 67,
      "endPosition": 77
    }
  ],
  "processingTime": 1250
}
```

### 2. 分词接口

**接口**: `POST /api/nlp/tokenize`

**功能**: 对文本进行分词和词性标注

**请求示例**:

```json
{
  "keyword": "今天天气真不错",
  "language": "zh"
}
```

**响应示例**:

```json
{
  "tokens": ["今天", "天气", "真", "不错"],
  "tokenDetails": [
    {"word": "今天", "pos": "t", "ner": ""},
    {"word": "天气", "pos": "n", "ner": ""},
    {"word": "真", "pos": "d", "ner": ""},
    {"word": "不错", "pos": "a", "ner": ""}
  ]
}
```

### 3. 拼写纠错接口

**接口**: `POST /api/nlp/spell-check`

**功能**: 检查并纠正拼写错误

**请求示例**:

```json
{
  "keyword": "I have a speling mistake here",
  "language": "en"
}
```

**响应示例**:

```json
{
  "correctedText": "I have a spelling mistake here",
  "errors": [
    {
      "original": "speling",
      "suggestions": ["spelling", "spieling", "peeling"],
      "message": "Possible spelling mistake found.",
      "position": 9
    }
  ],
  "hasErrors": true
}
```

### 4. 命名实体识别接口

**接口**: `POST /api/nlp/ner`

**功能**: 识别文本中的命名实体

**请求示例**:

```json
{
  "keyword": "Barack Obama was born in Hawaii and became the President of the United States.",
  "language": "en"
}
```

**响应示例**:

```json
{
  "namedEntities": [
    {
      "text": "Barack Obama",
      "type": "PERSON",
      "startPosition": 0,
      "endPosition": 12
    },
    {
      "text": "Hawaii",
      "type": "LOCATION",
      "startPosition": 25,
      "endPosition": 31
    },
    {
      "text": "United States",
      "type": "LOCATION",
      "startPosition": 67,
      "endPosition": 80
    }
  ]
}
```

### 5. 表情符号处理接口

**接口**: `POST /api/nlp/emoji`

**功能**: 识别和处理表情符号

**请求示例**:

```json
{
  "keyword": "Hello! 😊 How are you? 👋 I'm happy! 🎉"
}
```

**响应示例**:

```json
{
  "textWithoutEmojis": "Hello!  How are you?  I'm happy! ",
  "emojis": [
    {
      "emoji": "😊",
      "description": "smiling face with smiling eyes",
      "unicode": "😊"
    },
    {
      "emoji": "👋",
      "description": "waving hand",
      "unicode": "👋"
    },
    {
      "emoji": "🎉",
      "description": "party popper",
      "unicode": "🎉"
    }
  ],
  "emojiCount": 3
}
```

### 6. 健康检查接口

**接口**: `GET /api/nlp/health`

**功能**: 检查服务健康状态

**响应示例**:

```json
{
  "status": "UP",
  "service": "NLP Service",
  "version": "1.0.0"
}
```

### 7. 帮助文档接口

**接口**: `GET /api/nlp/help`

**功能**: 获取API帮助信息

## 使用示例

### 使用cURL

```bash
# 完整NLP处理
curl -X POST http://localhost:8080/api/nlp/process \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "I cant wait to see you tmrw! 😊",
    "language": "auto",
    "enableAll": true
  }'

# 中文分词
curl -X POST http://localhost:8080/api/nlp/tokenize \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "我爱自然语言处理",
    "language": "zh"
  }'

# 拼写检查
curl -X POST http://localhost:8080/api/nlp/spell-check \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "Ths is a tst",
    "language": "en"
  }'
```

### 使用Postman

1. 导入 `api-examples.json` 文件（如果提供）
2. 选择对应的请求
3. 点击 "Send" 发送请求

### 使用Java代码

```java
RestTemplate restTemplate = new RestTemplate();
String url = "http://localhost:8080/api/nlp/process";

NlpRequest request = new NlpRequest();
request.setKeyword("Hello world!");
request.setLanguage("en");
request.setEnableAll(true);

NlpResponse response = restTemplate.postForObject(url, request, NlpResponse.class);
System.out.println(response);
```

## 配置说明

在 `application.yml` 中可以配置以下参数：

```yaml
server:
  port: 8080  # 服务端口

nlp:
  language-detection-threshold: 0.5  # 语言检测阈值
  enable-cache: true  # 是否启用缓存
  max-text-length: 10000  # 最大文本长度
```

## 性能优化建议

1. **内存配置**: 建议启动时配置足够的内存
   ```bash
   java -Xmx4g -Xms2g -jar nlp-demo-1.0.0.jar
   ```

2. **并发处理**: 默认配置支持适度并发，高并发场景建议调整线程池配置

3. **缓存策略**: 对于重复文本处理，建议启用缓存

4. **模型预加载**: 首次请求可能较慢，建议应用启动后进行预热

## 注意事项

1. Stanford CoreNLP模型文件较大，首次启动需要下载，请耐心等待
2. 中文NER需要下载中文模型（约200MB）
3. 处理长文本时可能需要较长时间
4. 建议在生产环境中配置更大的内存
5. LanguageTool规则库较大，建议根据实际需求选择性加载

## 常见问题

### 1. 启动时内存不足？

**解决方案**: 增加JVM内存配置
```bash
java -Xmx4g -jar nlp-demo-1.0.0.jar
```

### 2. 模型加载失败？

**解决方案**: 
- 检查网络连接，确保可以下载模型文件
- 手动下载模型文件放到对应目录
- 检查磁盘空间是否充足

### 3. 中文分词效果不理想？

**解决方案**: 
- 可以尝试添加自定义词典
- 调整HanLP配置参数
- 使用更适合业务场景的分词模式

### 4. API响应时间较长？

**解决方案**:
- 首次请求需要加载模型，后续会快很多
- 启用缓存功能
- 减少不必要的功能开关
- 增加服务器配置

## 扩展功能

如需添加更多NLP功能，可以考虑：

- 情感分析
- 文本摘要
- 关键词提取
- 文本相似度计算
- 机器翻译
- 语音识别集成

## 许可证

本项目基于 MIT 许可证开源。

## 贡献

欢迎提交 Issue 和 Pull Request！

## 联系方式

如有问题或建议，请联系开发团队。

---

**享受NLP之旅！** 🚀

