# Maven 与 Java 版本兼容性指南

## Maven 版本与 Java 版本对应关系

| Maven 版本 | 支持的 Java 版本 | 推荐的 Java 版本 |
|-----------|----------------|----------------|
| Maven 3.9.x | Java 8 - 21 | Java 17, 21 |
| Maven 3.8.x | Java 7 - 19 | Java 11, 17 |
| Maven 3.6.x | Java 7 - 15 | Java 8, 11 |
| Maven 3.5.x | Java 7 - 14 | Java 8 |
| Maven 3.3.x | Java 7 - 13 | Java 8 |

## 本项目配置

### 当前使用版本
- **Java 版本**: 17
- **Maven 版本**: 3.8.1+ (推荐 3.9.x)
- **Spring Boot 版本**: 3.2.0 (要求 Java 17+)

### Maven Compiler Plugin 版本

本项目使用 **maven-compiler-plugin 3.11.0**，这是支持 Java 17 的版本。

#### 关键配置说明

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <source>17</source>          <!-- 源代码兼容性 -->
        <target>17</target>          <!-- 目标字节码版本 -->
        <encoding>UTF-8</encoding>   <!-- 源文件编码 -->
        <release>17</release>        <!-- 推荐使用 release 替代 source/target -->
    </configuration>
</plugin>
```

#### 版本对应关系

| maven-compiler-plugin 版本 | 支持的最高 Java 版本 |
|---------------------------|-------------------|
| 3.11.0 | Java 21 |
| 3.10.x | Java 19 |
| 3.9.x | Java 17 |
| 3.8.x | Java 17 |
| 3.6.x | Java 10 |

## 常见问题解决

### 问题 1: "错误: 不支持发行版本 17"

**原因**:
- Maven 版本过低（< 3.8.1）
- maven-compiler-plugin 版本过低（< 3.8.0）
- 系统 Java 版本低于 17

**解决方案**:

1. **检查 Java 版本**
```bash
java -version
```
应该显示 Java 17 或更高版本。

2. **检查 Maven 版本**
```bash
mvn -version
```
应该显示 Maven 3.8.1 或更高版本。

3. **更新 pom.xml**（已完成）
```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>17</source>
                <target>17</target>
                <release>17</release>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 问题 2: Maven 未安装

**检查是否安装**:
```bash
mvn -version
```

如果显示 "command not found"，说明未安装 Maven。

**安装方法**:

#### macOS (使用 Homebrew)
```bash
brew install maven
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt-get update
sudo apt-get install maven
```

#### Linux (CentOS/RHEL)
```bash
sudo yum install maven
```

#### 手动安装
1. 从 [Maven官网](https://maven.apache.org/download.cgi) 下载最新版本
2. 解压到指定目录，例如 `/opt/maven`
3. 配置环境变量：
```bash
export MAVEN_HOME=/opt/maven
export PATH=$MAVEN_HOME/bin:$PATH
```

### 问题 3: Maven 使用了错误的 Java 版本

**检查 Maven 使用的 Java**:
```bash
mvn -version
```
输出会显示 Maven 使用的 Java 版本。

**解决方案**:

设置 `JAVA_HOME` 环境变量：

#### macOS/Linux
```bash
# 临时设置
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# 永久设置（添加到 ~/.zshrc 或 ~/.bash_profile）
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc
```

#### 验证
```bash
echo $JAVA_HOME
mvn -version
```

### 问题 4: 编译时内存不足

**症状**: OutOfMemoryError 或 Java heap space

**解决方案**:

创建或编辑 `.mvn/jvm.config` 文件：
```bash
mkdir -p .mvn
echo "-Xmx4g -Xms2g" > .mvn/jvm.config
```

或者在命令行中设置：
```bash
export MAVEN_OPTS="-Xmx4g -Xms2g"
mvn clean package
```

## 推荐配置

### 最佳实践

1. **使用最新的稳定版本**
   - Maven: 3.9.x
   - maven-compiler-plugin: 3.11.0

2. **使用 `release` 参数**（推荐）
```xml
<configuration>
    <release>17</release>
</configuration>
```
而不是同时使用 `source` 和 `target`。

3. **明确指定编码**
```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

4. **在 CI/CD 中固定版本**
使用 Maven Wrapper:
```bash
mvn wrapper:wrapper -Dmaven=3.9.5
```

## 验证配置

### 测试编译

```bash
# 清理并编译
mvn clean compile

# 运行测试
mvn test

# 打包
mvn package
```

### 验证 Java 版本

查看编译后的 class 文件版本：
```bash
javap -v target/classes/com/nlp/NlpDemoApplication.class | grep "major"
```
输出应该显示 `major version: 61`（对应 Java 17）

## 升级指南

### 从 Java 11 升级到 Java 17

1. 更新 `pom.xml` 中的版本：
```xml
<properties>
    <java.version>17</java.version>
</properties>
```

2. 更新 Spring Boot 版本到 3.x（如果还在 2.x）

3. 检查依赖兼容性

4. 重新编译和测试

### 从 Maven 3.6.x 升级到 3.9.x

1. 备份 `settings.xml`

2. 安装新版本：
```bash
brew upgrade maven  # macOS
```

3. 验证：
```bash
mvn -version
```

4. 测试构建：
```bash
mvn clean verify
```

## 参考资源

- [Maven 官方文档](https://maven.apache.org/)
- [Maven Compiler Plugin 文档](https://maven.apache.org/plugins/maven-compiler-plugin/)
- [Java 版本历史](https://www.oracle.com/java/technologies/java-se-support-roadmap.html)
- [Spring Boot 版本要求](https://docs.spring.io/spring-boot/system-requirements.html)

## 本项目要求总结

✅ **Java**: 17 或更高版本  
✅ **Maven**: 3.8.1 或更高版本（推荐 3.9.x）  
✅ **maven-compiler-plugin**: 3.11.0  
✅ **Spring Boot**: 3.2.0 (要求 Java 17+)  

---

**配置已更新完成，可以正常编译了！**

