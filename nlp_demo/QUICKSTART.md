# 快速开始指南

## 5分钟快速体验

### 方法一：使用启动脚本（推荐）

```bash
# 1. 进入项目目录
cd nlp_demo

# 2. 运行启动脚本
./start.sh

# 3. 等待服务启动（首次启动需要下载依赖和模型，可能需要几分钟）
```

### 方法二：使用Maven命令

```bash
# 1. 编译项目
mvn clean package -DskipTests

# 2. 启动服务
java -Xmx4g -Xms2g -jar target/nlp-demo-1.0.0.jar
```

### 方法三：直接运行（开发模式）

```bash
mvn spring-boot:run
```

## 快速测试

### 1. 使用测试脚本

```bash
./test-api.sh
```

### 2. 使用cURL命令

#### 测试健康状态
```bash
curl http://localhost:8080/api/nlp/health
```

#### 测试完整功能
```bash
curl -X POST http://localhost:8080/api/nlp/process \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "I cant wait! 😊 Apple Inc. is great!",
    "language": "auto",
    "enableAll": true
  }'
```

#### 测试中文分词
```bash
curl -X POST http://localhost:8080/api/nlp/tokenize \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "今天天气真不错",
    "language": "zh"
  }'
```

### 3. 使用浏览器

访问帮助页面：
```
http://localhost:8080/api/nlp/help
```

## 常见使用场景

### 场景1：英文文本处理

```bash
curl -X POST http://localhost:8080/api/nlp/process \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "dont worry! Barack Obama visited California yesterday.",
    "language": "en",
    "enableAll": true
  }'
```

**返回结果包括**:
- ✅ 分词: ["do", "not", "worry", "!", "Barack", "Obama", ...]
- ✅ 纠错: "do not worry!"
- ✅ 缩写还原: "do not worry!"
- ✅ 命名实体: Barack Obama (PERSON), California (LOCATION)

### 场景2：中文文本分析

```bash
curl -X POST http://localhost:8080/api/nlp/process \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "马云创立了阿里巴巴，总部在杭州",
    "language": "zh",
    "enableAll": true
  }'
```

**返回结果包括**:
- ✅ 分词: ["马云", "创立", "了", "阿里巴巴", "，", "总部", "在", "杭州"]
- ✅ 词性标注
- ✅ 命名实体识别

### 场景3：社交媒体文本处理

```bash
curl -X POST http://localhost:8080/api/nlp/emoji \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "OMG! I am so happy today! 😊🎉👏"
  }'
```

**返回结果包括**:
- ✅ 表情符号列表及描述
- ✅ 移除表情后的文本
- ✅ 表情符号数量统计

### 场景4：拼写检查

```bash
curl -X POST http://localhost:8080/api/nlp/spell-check \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "I have a speling mistake in this sentance",
    "language": "en"
  }'
```

**返回结果包括**:
- ✅ 纠正后的文本
- ✅ 错误位置和建议

## 自定义功能选择

只启用需要的功能以提高性能：

```bash
curl -X POST http://localhost:8080/api/nlp/process \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "Sample text here",
    "language": "en",
    "enableAll": false,
    "enableTokenization": true,
    "enableNer": true
  }'
```

## 性能建议

| 文本长度 | 推荐配置 | 预期响应时间 |
|---------|---------|-------------|
| < 100字符 | 默认配置 | < 1秒 |
| 100-500字符 | 默认配置 | 1-3秒 |
| 500-1000字符 | 2GB内存 | 3-5秒 |
| > 1000字符 | 4GB内存 | 5-10秒 |

## 下一步

- 📖 查看完整 [README.md](README.md) 了解更多功能
- 🔧 调整 `application.yml` 进行个性化配置
- 📝 查看 `api-examples.json` 获取更多示例
- 🚀 集成到你的应用中

## 问题排查

### 服务无法启动？
1. 检查JDK版本 >= 17
2. 检查端口8080是否被占用
3. 检查内存是否足够（建议4GB）

### 模型加载失败？
1. 检查网络连接
2. 尝试手动下载模型文件
3. 检查磁盘空间

### 响应时间过长？
1. 首次请求会较慢（需要加载模型）
2. 减少启用的功能数量
3. 增加JVM内存配置

---

**祝使用愉快！** 🎉

