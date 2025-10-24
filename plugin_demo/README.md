# 🔌 SpringBoot热插拔插件系统

一个基于SpringBoot的热插拔插件系统演示项目，支持动态加载、卸载和管理Java插件。

## 📋 项目结构

```
hot-plugin-demo/
├── plugin-api/          # 插件API定义模块
├── main-app/           # 主应用程序模块
├── sample-plugin/      # 简单示例插件模块
├── complex-plugin/     # 复杂数据处理插件模块
├── pom.xml            # 父级Maven配置
└── README.md          # 项目说明文档
```

## ✨ 功能特性

- 🔄 **热插拔支持**: 无需重启应用即可动态加载/卸载插件
- 🌐 **Web管理界面**: 提供美观的插件管理Web界面
- 📡 **REST API**: 完整的插件管理REST API
- 🔧 **插件执行**: 支持在线执行插件功能
- 📦 **文件上传**: 支持通过Web界面上传JAR插件
- 🔍 **插件扫描**: 自动扫描plugins目录中的JAR文件
- 📊 **状态管理**: 实时显示插件启用/禁用状态

## 🚀 快速开始

### 1. 环境要求

- Java 11+
- Maven 3.6+

### 2. 构建项目

```bash
# 克隆项目
git clone <项目地址>
cd halo_demo

# 编译整个项目
mvn clean compile

# 打包所有模块
mvn clean package

# 或者使用提供的构建脚本
chmod +x build.sh
./build.sh
```

### 3. 启动应用

```bash
# 启动主应用
cd main-app
mvn spring-boot:run

# 或者运行JAR文件
java -jar target/main-app-1.0.0.jar
```

### 4. 访问应用

- **Web管理界面**: http://localhost:8080
- **插件API**: http://localhost:8080/api/plugins
- **健康检查**: http://localhost:8080/actuator/health

## 📦 插件开发

### 1. 创建插件项目

创建一个新的Maven项目，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>plugin-api</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### 2. 实现插件接口

```java
package com.example.plugin.custom;

import com.example.plugin.api.Plugin;

public class MyCustomPlugin implements Plugin {
    
    @Override
    public String getName() {
        return "My Custom Plugin";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public String getDescription() {
        return "这是我的自定义插件";
    }
    
    @Override
    public void start() {
        System.out.println("自定义插件已启动");
    }
    
    @Override
    public void stop() {
        System.out.println("自定义插件已停止");
    }
    
    @Override
    public Object execute(Object input) {
        // 实现插件逻辑
        return "Hello from custom plugin!";
    }
}
```

### 3. 打包插件

```bash
mvn clean package
```

### 4. 部署插件

将生成的JAR文件复制到主应用的`plugins`目录，或通过Web界面上传。

## 🎯 示例插件

项目包含三个示例插件：

### 1. 计算器插件 (CalculatorPlugin)

支持基本数学运算的简单插件。

**使用方法**:
```json
{
  "operation": "add",
  "a": 10,
  "b": 5
}
```

**支持的操作**:
- `add`: 加法
- `subtract`: 减法
- `multiply`: 乘法
- `divide`: 除法

### 2. 问候插件 (GreetingPlugin)

生成个性化问候语的插件。

**使用方法**:
```json
{
  "name": "张三",
  "language": "zh"
}
```

**支持的语言**:
- `zh`: 中文
- `en`: 英文

### 3. 数据处理插件 (DataProcessingPlugin) ⭐

这是一个复杂的企业级插件，展示了多文件、多组件的插件架构。

**主要功能**:
- 🔍 **数据验证**: 完整性检查、格式验证、质量评分
- 🧹 **数据清洗**: 去重、标准化、异常值处理
- 🔄 **数据转换**: 排序、过滤、聚合、标准化
- 📊 **数据分析**: 统计分析、趋势分析、异常检测
- 📋 **报告生成**: 多格式报告输出

**使用示例**:

生成演示数据:
```json
{
  "operation": "demo"
}
```

数据分析:
```json
{
  "operation": "analyze",
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

数据转换:
```json
{
  "operation": "transform",
  "transformType": "sort",
  "sortBy": "value",
  "ascending": false,
  "data": [...]
}
```

**插件架构**:
```
complex-plugin/
├── DataProcessingPlugin.java          # 主插件类
├── model/                             # 数据模型
├── service/                          # 业务服务
└── util/                             # 工具类
```

## 🔧 API文档

### 获取所有插件
```http
GET /api/plugins
```

### 获取指定插件
```http
GET /api/plugins/{pluginName}
```

### 执行插件
```http
POST /api/plugins/{pluginName}/execute
Content-Type: application/json

{
  "参数": "值"
}
```

### 启用插件
```http
POST /api/plugins/{pluginName}/enable
```

### 禁用插件
```http
POST /api/plugins/{pluginName}/disable
```

### 上传插件
```http
POST /api/plugins/upload
Content-Type: multipart/form-data

file: <JAR文件>
```

### 扫描插件目录
```http
POST /api/plugins/scan
```

## 🏗️ 架构设计

### 核心组件

1. **Plugin接口**: 定义插件的基本契约
2. **PluginManager**: 负责插件的生命周期管理
3. **PluginController**: 提供REST API接口
4. **WebController**: 提供Web管理界面

### 类加载机制

- 使用`URLClassLoader`实现插件的动态加载
- 每个插件使用独立的类加载器，避免类冲突
- 支持插件的热替换和卸载

### 安全考虑

- 插件运行在受限的环境中
- 建议在生产环境中添加插件签名验证
- 可以通过SecurityManager限制插件权限

## 🔍 故障排除

### 常见问题

1. **插件加载失败**
   - 检查JAR文件是否包含有效的插件类
   - 确认插件类实现了Plugin接口
   - 查看应用日志获取详细错误信息

2. **插件执行异常**
   - 检查输入参数格式是否正确
   - 确认插件已正确启用
   - 查看插件的execute方法实现

3. **Web界面无法访问**
   - 确认应用已正常启动
   - 检查端口8080是否被占用
   - 查看防火墙设置

### 日志配置

应用使用SLF4J进行日志记录，可以通过修改`application.yml`调整日志级别：

```yaml
logging:
  level:
    com.example: DEBUG
```

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- 项目Issues: [GitHub Issues](项目地址/issues)
- 邮箱: your-email@example.com

---

**享受热插拔插件系统带来的便利！** 🎉
