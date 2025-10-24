#!/bin/bash

echo "=========================================="
echo "  NLP工具服务启动脚本"
echo "=========================================="
echo ""

# 检查Java版本
echo "检查Java环境..."
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java，请先安装JDK 17或更高版本"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "错误: Java版本过低，需要JDK 17或更高版本"
    exit 1
fi

echo "Java版本检查通过"
echo ""

# 检查Maven
echo "检查Maven环境..."
if ! command -v mvn &> /dev/null; then
    echo "警告: 未找到Maven，将尝试使用已编译的jar文件"
    
    if [ -f "target/nlp-demo-1.0.0.jar" ]; then
        echo "找到已编译的jar文件，准备启动..."
        java -Xmx4g -Xms2g -jar target/nlp-demo-1.0.0.jar
    else
        echo "错误: 未找到编译后的jar文件，请先安装Maven并执行 mvn clean package"
        exit 1
    fi
else
    echo "Maven检查通过"
    echo ""
    
    # 编译并运行
    echo "开始编译项目..."
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "编译成功，启动服务..."
        echo "内存配置: -Xmx4g -Xms2g"
        echo ""
        java -Xmx4g -Xms2g -jar target/nlp-demo-1.0.0.jar
    else
        echo "编译失败，请检查错误信息"
        exit 1
    fi
fi

