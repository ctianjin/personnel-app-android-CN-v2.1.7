# APK 构建指南

## 方式 1: Android Studio（最简单）

### 步骤

1. **打开 Android Studio**

2. **导入项目**
   ```
   File → Open → 选择 personnel-app-android 目录
   ```

3. **等待 Gradle 同步完成**
   - 首次同步可能需要几分钟
   - 确保网络连接正常

4. **构建 Debug APK**
   ```
   Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```

5. **构建 Release APK（需要签名）**
   ```
   Build → Generate Signed Bundle / APK
   → 选择 APK
   → 选择 release
   → 输入签名信息
   → Finish
   ```

6. **APK 位置**
   - Debug: `app/build/outputs/apk/debug/app-debug.apk`
   - Release: `app/build/outputs/apk/release/app-release.apk`

---

## 方式 2: 命令行（需要 Android SDK）

### 前置条件

- JDK 17+
- Android SDK
- ANDROID_HOME 环境变量

### 步骤

```bash
# 1. 进入项目目录
cd personnel-app-android

# 2. 赋予 Gradle 执行权限
chmod +x gradlew

# 3. 构建 Debug APK
./gradlew assembleDebug

# 4. 构建 Release APK（需要先配置签名）
./gradlew assembleRelease
```

### 配置签名

创建 `local.properties` 文件：

```properties
RELEASE_STORE_FILE=/absolute/path/to/personnel-app-key.jks
RELEASE_STORE_PASSWORD=your_keystore_password
RELEASE_KEY_ALIAS=personnel-app
RELEASE_KEY_PASSWORD=your_key_password
```

---

## 方式 3: 使用构建脚本

```bash
# 1. 进入项目目录
cd personnel-app-android

# 2. 运行构建脚本
./build_apk.sh

# 3. 按提示选择构建类型
```

---

## 方式 4: GitHub Actions 自动构建

### 创建工作流

创建 `.github/workflows/build.yml`：

```yaml
name: Build APK

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Setup Android SDK
        uses: android-actions/setup-android@v2
      
      - name: Build Debug APK
        run: ./gradlew assembleDebug
      
      - name: Upload APK
        uses: actions/upload-artifact@v3
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/app-debug.apk
```

### 自动构建

每次 push 到 GitHub 会自动构建 APK，可在 Actions 页面下载。

---

## 故障排除

### Q: Gradle 同步失败

**解决方案**：
1. 检查网络连接
2. 使用国内镜像（见下方）
3. 删除 `.gradle` 目录重试

### Q: 找不到 Android SDK

**解决方案**：
```bash
# 设置环境变量
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

### Q: 签名失败

**解决方案**：
1. 检查 `local.properties` 路径是否正确
2. 使用绝对路径
3. 检查密钥库密码是否正确

### Q: 构建内存不足

**解决方案**：
在 `gradle.properties` 中添加：
```properties
org.gradle.jvmargs=-Xmx4096m
```

---

## 使用国内镜像（加速）

### 修改 `build.gradle`（项目根目录）

```gradle
allprojects {
    repositories {
        // 阿里云镜像
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://maven.aliyun.com/repository/public' }
        maven { url 'https://maven.aliyun.com/repository/gradle-plugin' }
        
        google()
        mavenCentral()
    }
}
```

### 修改 `settings.gradle`

```gradle
pluginManagement {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://maven.aliyun.com/repository/public' }
        maven { url 'https://maven.aliyun.com/repository/gradle-plugin' }
        
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
```

---

## APK 安装

### 方式 1: USB 传输

1. 手机连接电脑
2. 传输 APK 到手机
3. 在手机上安装

### 方式 2: ADB 安装

```bash
adb install app-debug.apk
```

### 方式 3: 二维码下载

1. 将 APK 上传到服务器或云存储
2. 生成下载二维码
3. 手机扫码下载安装

### 方式 4: 通过 OpenClaw 发送

```bash
# 使用 OpenClaw message 工具发送
```

---

## 版本说明

| 版本 | 类型 | 签名 | 大小 | 用途 |
|------|------|------|------|------|
| Debug | 调试版 | 自动签名 | ~15MB | 开发测试 |
| Release | 发布版 | 需要签名 | ~10MB | 正式发布 |

---

## 安全提示

1. **不要分享 Debug APK** - 包含调试信息
2. **保护签名密钥** - 不要提交到 Git
3. **验证 APK 签名** - 发布前验证
4. **测试后再发布** - 确保功能正常

---

**构建愉快！🎉**
