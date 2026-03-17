#!/bin/bash

# 碣石人事管理 - APK 构建脚本
# 使用前提：已安装 Android SDK 和 JDK 17+

set -e

echo "======================================"
echo "  碣石人事管理 - APK 构建脚本"
echo "======================================"

# 检查 JDK
if ! command -v java &> /dev/null; then
    echo "❌ 错误：未找到 Java，请安装 JDK 17+"
    exit 1
fi

java_version=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" -lt 17 ]; then
    echo "❌ 错误：需要 JDK 17+，当前版本：$java_version"
    exit 1
fi
echo "✅ Java 版本：$java_version"

# 检查 Android SDK
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
    echo "⚠️  警告：未设置 ANDROID_HOME 环境变量"
    echo "   请设置：export ANDROID_HOME=/path/to/android/sdk"
fi

# 进入项目目录
cd "$(dirname "$0")"
PROJECT_DIR=$(pwd)
echo "📁 项目目录：$PROJECT_DIR"

# 选择构建类型
echo ""
echo "请选择构建类型："
echo "1. Debug APK（调试版，无需签名）"
echo "2. Release APK（发布版，需要签名）"
echo "3. 清理并构建 Debug"
echo "4. 清理并构建 Release"
read -p "请输入选项 (1-4): " choice

case $choice in
    1)
        echo ""
        echo "🔨 开始构建 Debug APK..."
        ./gradlew assembleDebug
        echo ""
        echo "✅ 构建完成！"
        echo "📦 APK 位置：app/build/outputs/apk/debug/app-debug.apk"
        ls -lh app/build/outputs/apk/debug/app-debug.apk
        ;;
    2)
        echo ""
        # 检查签名配置
        if [ ! -f "local.properties" ]; then
            echo "❌ 错误：未找到 local.properties，请先配置签名"
            echo "   创建文件并添加以下内容："
            echo "   RELEASE_STORE_FILE=/path/to/key.jks"
            echo "   RELEASE_STORE_PASSWORD=your_password"
            echo "   RELEASE_KEY_ALIAS=personnel-app"
            echo "   RELEASE_KEY_PASSWORD=your_key_password"
            exit 1
        fi
        echo "🔨 开始构建 Release APK..."
        ./gradlew assembleRelease
        echo ""
        echo "✅ 构建完成！"
        echo "📦 APK 位置：app/build/outputs/apk/release/app-release.apk"
        ls -lh app/build/outputs/apk/release/app-release.apk
        ;;
    3)
        echo ""
        echo "🧹 清理项目..."
        ./gradlew clean
        echo "🔨 构建 Debug APK..."
        ./gradlew assembleDebug
        echo ""
        echo "✅ 构建完成！"
        echo "📦 APK 位置：app/build/outputs/apk/debug/app-debug.apk"
        ;;
    4)
        echo ""
        echo "🧹 清理项目..."
        ./gradlew clean
        if [ ! -f "local.properties" ]; then
            echo "❌ 错误：未找到 local.properties，请先配置签名"
            exit 1
        fi
        echo "🔨 构建 Release APK..."
        ./gradlew assembleRelease
        echo ""
        echo "✅ 构建完成！"
        echo "📦 APK 位置：app/build/outputs/apk/release/app-release.apk"
        ;;
    *)
        echo "❌ 无效选项"
        exit 1
        ;;
esac

echo ""
echo "======================================"
echo "  构建完成！"
echo "======================================"
