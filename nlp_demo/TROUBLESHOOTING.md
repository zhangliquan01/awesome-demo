# 故障排除指南

本文档列出了常见问题及解决方案。

## 编译错误

### 错误 1: "不支持发行版本 17"

**错误信息**:
```
[ERROR] Fatal error compiling: 错误: 不支持发行版本 17
```

**原因**: Maven compiler plugin版本过低或配置不正确

**解决方案**: ✅ 已修复
- 已在 `pom.xml` 中添加了明确的 `maven-compiler-plugin` 配置
- 使用版本 3.11.0，完全支持 Java 17

**要求**: Maven 3.8.1 或更高版本

---

### 错误 2: HanLP API "方法引用无效 - 找不到符号 word()"

**错误信息**:
```
[ERROR] 方法引用无效
  找不到符号: 方法 word()
  位置: 类 com.hankcs.hanlp.seg.common.Term
```

**原因**: HanLP的`Term`类使用公共字段而不是getter方法

**解决方案**: ✅ 已修复
- 将 `Term::word` 改为 `term -> term.word`
- HanLP使用公共字段访问：`term.word` 和 `term.nature`

**修复代码**:
```java
// 错误写法
terms.stream().map(Term::word)

// 正确写法
terms.stream().map(term -> term.word)
```

---

### 错误 3: Stanford CoreNLP "annotator 'ner' requires annotation 'LemmaAnnotation'"

**错误信息**:
```
Caused by: java.lang.IllegalArgumentException: 
annotator "ner" requires annotation "LemmaAnnotation". 
The usual requirements for this annotator are: tokenize,pos,lemma
```

**原因**: 
- Stanford CoreNLP的NER依赖于lemma注解器
- 中文模型对lemma和NER支持有限

**解决方案**: ✅ 已修复
- 中文NER改用HanLP实现（基于词性标注判断）
- 中文Stanford CoreNLP只使用 `tokenize,ssplit,pos`
- 英文Stanford CoreNLP使用完整管道 `tokenize,ssplit,pos,lemma,ner`

**配置**:
```java
// 中文配置（不使用NER）
props.setProperty("annotators", "tokenize,ssplit,pos");

// 英文配置（完整NER）
props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
```

---

## 运行时错误

### 错误 4: OutOfMemoryError / Java heap space

**错误信息**:
```
java.lang.OutOfMemoryError: Java heap space
```

**原因**: NLP模型文件较大，默认内存不足

**解决方案**:

1. **增加JVM内存**:
```bash
java -Xmx4g -Xms2g -jar target/nlp-demo-1.0.0.jar
```

2. **使用启动脚本** (已自动配置4GB):
```bash
./start.sh
```

3. **Maven运行时增加内存**:
```bash
export MAVEN_OPTS="-Xmx4g -Xms2g"
mvn spring-boot:run
```

---

### 错误 5: 模型文件下载失败

**错误信息**:
```
Could not resolve dependencies for project...
Failed to download stanford-corenlp models
```

**原因**: 
- 网络连接问题
- Maven仓库访问受限
- 磁盘空间不足

**解决方案**:

1. **检查网络连接**:
```bash
ping repo1.maven.org
```

2. **清理Maven缓存并重试**:
```bash
mvn clean
rm -rf ~/.m2/repository/edu/stanford/nlp
mvn package -U
```

3. **使用国内镜像** (编辑 `~/.m2/settings.xml`):
```xml
<mirror>
  <id>aliyun</id>
  <mirrorOf>central</mirrorOf>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

4. **检查磁盘空间**:
```bash
df -h
```
需要至少1GB空闲空间。

---

### 错误 6: Port 8080 already in use

**错误信息**:
```
Port 8080 was already in use
```

**原因**: 端口被其他程序占用

**解决方案**:

1. **查找占用端口的进程**:
```bash
lsof -i :8080
```

2. **终止该进程**:
```bash
kill -9 <PID>
```

3. **或者更改端口** (编辑 `application.yml`):
```yaml
server:
  port: 8081
```

---

## 依赖问题

### 错误 7: Maven未安装

**错误信息**:
```
command not found: mvn
```

**解决方案**:

**macOS**:
```bash
brew install maven
```

**Linux (Ubuntu/Debian)**:
```bash
sudo apt-get update
sudo apt-get install maven
```

**验证**:
```bash
mvn -version
```

---

### 错误 8: Java版本不匹配

**错误信息**:
```
Unsupported class file major version 61
```

**原因**: 运行时Java版本低于编译版本

**解决方案**:

1. **检查Java版本**:
```bash
java -version
```
必须是Java 17或更高版本。

2. **设置JAVA_HOME**:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

3. **永久设置** (添加到 `~/.zshrc` 或 `~/.bash_profile`):
```bash
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc
```

---

## API使用问题

### 问题 9: 响应时间过长

**症状**: 首次API请求需要很长时间

**原因**: 首次加载NLP模型需要时间（3-5秒）

**解决方案**:

1. **预热服务** - 启动后发送测试请求:
```bash
curl http://localhost:8080/api/nlp/health
```

2. **只启用需要的功能**:
```json
{
  "keyword": "text",
  "enableAll": false,
  "enableTokenization": true,
  "enableNer": false
}
```

3. **使用专用端点**:
```bash
# 只分词（更快）
curl -X POST http://localhost:8080/api/nlp/tokenize
```

---

### 问题 10: 中文实体识别不准确

**症状**: 中文NER结果不理想

**原因**: HanLP基于词性标注的NER有局限性

**解决方案**:

1. **使用更标准的文本**
2. **自定义词典** (可扩展)
3. **考虑使用专业NER模型** (如BERT-NER)

**当前实现**:
- `nr` → PERSON (人名)
- `ns` → LOCATION (地名)
- `nt` → ORGANIZATION (机构)
- `nz` → ORGANIZATION (其他专名)

---

## 性能优化

### 优化 1: 启用缓存

编辑 `application.yml`:
```yaml
nlp:
  enable-cache: true
```

### 优化 2: 调整线程数

编辑 `NlpConfig.java`:
```java
props.setProperty("threads", "8"); // 根据CPU核心数调整
```

### 优化 3: 批量处理

避免频繁的单次调用，考虑批量处理。

---

## 常见警告

### 警告 1: SLF4J 警告

**警告信息**:
```
SLF4J: Multiple bindings were found
```

**影响**: 无影响，可以忽略

**解决** (可选): 在 `pom.xml` 中排除重复的日志依赖。

---

### 警告 2: Illegal reflective access

**警告信息**:
```
WARNING: Illegal reflective access by ...
```

**影响**: 无影响，Java 17的兼容性警告

**解决**: 等待依赖库更新，或添加JVM参数:
```bash
--add-opens java.base/java.lang=ALL-UNNAMED
```

---

## 调试技巧

### 技巧 1: 启用详细日志

编辑 `application.yml`:
```yaml
logging:
  level:
    com.nlp: DEBUG
    edu.stanford.nlp: DEBUG
```

### 技巧 2: 测试单个组件

使用专用端点测试：
- `/api/nlp/tokenize` - 只测试分词
- `/api/nlp/spell-check` - 只测试拼写检查
- `/api/nlp/ner` - 只测试NER

### 技巧 3: 查看模型加载情况

启动日志中会显示模型加载信息，注意观察。

---

## 获取帮助

如果以上方案都无法解决问题：

1. **查看完整日志**:
```bash
mvn spring-boot:run > app.log 2>&1
```

2. **查看API帮助**:
```bash
curl http://localhost:8080/api/nlp/help
```

3. **查看健康状态**:
```bash
curl http://localhost:8080/api/nlp/health
```

4. **检查系统要求**:
- Java 17+
- Maven 3.8.1+
- 4GB+ RAM
- 1GB+ 磁盘空间

---

## 已知限制

1. **中文NER**: 基于词性标注，不如深度学习模型准确
2. **模型大小**: Stanford CoreNLP模型较大（300-500MB）
3. **首次加载**: 需要3-5秒加载时间
4. **内存占用**: 运行时需要2-4GB内存

---

## 更新日志

- ✅ 修复了 Maven compiler plugin 配置
- ✅ 修复了 HanLP API 使用问题
- ✅ 优化了中文NER实现（改用HanLP）
- ✅ 移除了 Stanford CoreNLP 中文 lemma 依赖

---

**如果遇到新问题，请查阅其他文档或提交Issue。**


