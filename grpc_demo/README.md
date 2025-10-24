# gRPC Hello World (C++)

这是一个基于gRPC的C++ Hello World示例项目，包含以下组件：

- **gRPC服务器**: 提供Hello World服务
- **gRPC客户端**: 连接到gRPC服务器并提供HTTP接口

## 项目结构

```
grpc_hello_world/
├── proto/                 # Protocol Buffer定义文件
│   └── hello.proto        # HelloService服务定义
├── src/                   # 源代码
│   ├── server.cpp         # gRPC服务器实现
│   └── client.cpp         # gRPC客户端 + HTTP服务器实现
├── scripts/               # 脚本文件
│   └── run_example.sh.in  # 运行示例脚本模板
├── build/                 # 构建目录
├── CMakeLists.txt         # CMake构建配置
├── build.sh               # 构建脚本
└── README.md              # 本文件
```

## 功能特性

### gRPC服务器
- 提供 `SayHello` 服务：简单的问候响应
- 提供 `SayHelloStream` 服务：流式问候响应（返回多条消息）
- 监听端口：50051

### gRPC客户端 + HTTP服务器
- 作为gRPC客户端连接到gRPC服务器
- 同时提供HTTP接口，将HTTP请求转换为gRPC调用
- HTTP接口：
  - `GET /` - 主页面，显示可用接口
  - `GET /hello?name=姓名` - 简单问候接口
  - `GET /hello-stream?name=姓名` - 流式问候接口
- 默认HTTP端口：8080

## 依赖要求

- **CMake** >= 3.16
- **C++17** 编译器 (GCC, Clang)
- **Protocol Buffers** (protobuf)
- **gRPC** C++库
- **pkg-config**

### macOS安装依赖

```bash
# 使用Homebrew安装
brew install cmake protobuf grpc pkg-config
```

### Ubuntu安装依赖

```bash
# 安装基础工具
sudo apt-get update
sudo apt-get install -y cmake build-essential pkg-config

# 安装protobuf
sudo apt-get install -y libprotobuf-dev protobuf-compiler

# 安装gRPC (需要从源码编译)
# 详细步骤请参考gRPC官方文档
```

## 构建和运行

### 1. 构建项目

```bash
# 使用构建脚本
./build.sh

# 或手动构建
mkdir build
cd build
cmake .. -DCMAKE_BUILD_TYPE=Release
make -j$(nproc)
```

### 2. 运行服务器

在终端1中运行：

```bash
cd build
./hello_server
```

服务器将在端口50051上监听gRPC请求。

### 3. 运行客户端

在终端2中运行：

```bash
cd build
./hello_client [grpc_server_addr] [http_port]

# 示例
./hello_client                           # 默认连接localhost:50051，HTTP端口8080
./hello_client localhost:50051 8080      # 显式指定参数
```

### 4. 使用便捷脚本

构建完成后，您可以使用生成的运行脚本：

```bash
cd build

# 启动服务器
./run_example.sh server

# 启动客户端（在另一个终端）
./run_example.sh client

# 运行测试
./run_example.sh test
```

## 使用HTTP接口

客户端启动后，您可以通过HTTP接口访问gRPC服务：

### 1. 访问主页
```bash
curl http://localhost:8080/
```
或在浏览器中打开：http://localhost:8080

### 2. 简单问候接口
```bash
curl "http://localhost:8080/hello?name=张三"
```

响应示例：
```json
{
  "message": "Hello 张三!",
  "method": "simple"
}
```

### 3. 流式问候接口
```bash
curl "http://localhost:8080/hello-stream?name=李四"
```

响应示例：
```json
{
  "messages": [
    "Hello 李四 #1",
    "Hello 李四 #2",
    "Hello 李四 #3",
    "Hello 李四 #4",
    "Hello 李四 #5"
  ],
  "method": "stream"
}
```

## 架构说明

```
HTTP客户端 → HTTP服务器 → gRPC客户端 → gRPC服务器
    ↓              ↓             ↓           ↓
  浏览器/curl    client.cpp   client.cpp  server.cpp
```

1. **HTTP客户端**（浏览器/curl等）发送HTTP请求到HTTP服务器
2. **HTTP服务器**（在client.cpp中实现）解析HTTP请求
3. **gRPC客户端**（同样在client.cpp中）将请求转换为gRPC调用
4. **gRPC服务器**（server.cpp）处理gRPC请求并返回响应
5. 响应沿相反路径返回，最终以JSON格式返回给HTTP客户端

## 开发说明

### 修改proto文件

1. 编辑 `proto/hello.proto`
2. 重新构建项目
3. CMake会自动重新生成protobuf和gRPC代码

### 扩展功能

- 在 `server.cpp` 中添加新的gRPC服务方法
- 在 `client.cpp` 中添加对应的客户端调用和HTTP接口
- 更新 `proto/hello.proto` 文件定义新的消息类型

## 故障排除

### 1. 构建失败
- 确保所有依赖都已正确安装
- 检查CMake版本是否满足要求
- 查看详细错误信息

### 2. 连接失败
- 确保gRPC服务器已启动并监听正确端口
- 检查防火墙设置
- 确认网络连接

### 3. HTTP接口无响应
- 确保客户端已成功连接到gRPC服务器
- 检查HTTP端口是否被占用
- 查看客户端控制台输出的错误信息

## 许可证

本项目仅用于学习和演示目的。