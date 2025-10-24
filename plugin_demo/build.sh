#!/bin/bash

echo "🔨 开始构建热插拔插件系统..."

# 检查Java版本
echo "📋 检查Java版本..."
java -version

# 检查Maven版本
echo "📋 检查Maven版本..."
mvn -version

echo ""
echo "🧹 清理项目..."
mvn clean

echo ""
echo "📦 编译所有模块..."
mvn compile

echo ""
echo "🔧 打包所有模块..."
mvn package -DskipTests

echo ""
echo "📁 创建plugins目录..."
mkdir -p main-app/plugins

echo ""
echo "🔌 复制插件到plugins目录..."
cp sample-plugin/target/sample-plugin-1.0.0.jar main-app/plugins/
cp complex-plugin/target/complex-plugin-1.0.0.jar main-app/plugins/

echo ""
echo "✅ 构建完成！"
echo ""
echo "🚀 启动应用:"
echo "   cd main-app"
echo "   mvn spring-boot:run"
echo ""
echo "🌐 或者运行JAR文件:"
echo "   cd main-app"
echo "   java -jar target/main-app-1.0.0.jar"
echo ""
echo "📱 访问地址: http://localhost:8080"
echo ""
echo "📦 已安装的插件:"
echo "   - Calculator Plugin (计算器插件)"
echo "   - Greeting Plugin (问候插件)" 
echo "   - Data Processing Plugin (复杂数据处理插件)"
echo ""
echo "🎉 享受热插拔插件系统！"
