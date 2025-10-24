#!/bin/bash

# 构建脚本

set -e

echo "=== 构建 gRPC Hello World 项目 ==="

# 检查依赖
echo "检查依赖..."

# 检查cmake
if ! command -v cmake &> /dev/null; then
    echo "错误: 未找到cmake。请安装cmake。"
    echo "macOS: brew install cmake"
    echo "Ubuntu: sudo apt-get install cmake"
    exit 1
fi

# 检查pkg-config
if ! command -v pkg-config &> /dev/null; then
    echo "错误: 未找到pkg-config。请安装pkg-config。"
    echo "macOS: brew install pkg-config"
    echo "Ubuntu: sudo apt-get install pkg-config"
    exit 1
fi

# 检查protobuf
if ! pkg-config --exists protobuf; then
    echo "警告: 未找到protobuf。请安装protobuf。"
    echo "macOS: brew install protobuf"
    echo "Ubuntu: sudo apt-get install libprotobuf-dev protobuf-compiler"
fi

# 检查gRPC
if ! pkg-config --exists grpc++ 2>/dev/null && ! find /usr/local -name "grpc++Config.cmake" 2>/dev/null | head -1; then
    echo "警告: 未找到gRPC。请安装gRPC。"
    echo "macOS: brew install grpc"
    echo "Ubuntu: 需要从源码编译安装gRPC"
fi

# 创建构建目录
mkdir -p build
cd build

# 配置
echo "配置项目..."
cmake .. -DCMAKE_BUILD_TYPE=Release

# 构建
echo "构建项目..."
make -j$(nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 4)

echo ""
echo "=== 构建完成 ==="
echo ""
echo "运行服务器: ./hello_server"
echo "运行客户端: ./hello_client [grpc_server_addr] [http_port]"
echo ""
echo "或使用运行脚本:"
echo "./run_example.sh server    # 启动服务器"
echo "./run_example.sh client    # 启动客户端"
echo ""