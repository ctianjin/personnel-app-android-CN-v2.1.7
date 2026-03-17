# 碣石人事管理系统 - 快速开始指南

**版本**: v2.1.7  
**日期**: 2026-03-16  
**状态**: ✅ 已配置阿里云镜像，中国用户可直接使用

---

## 🚀 3 步构建 APK

### 步骤 1: 打开项目

1. 启动 Android Studio
2. `File` → `Open`
3. 选择 `personnel-app-android-FINAL` 文件夹
4. 点击 `OK`

### 步骤 2: 等待 Gradle 同步

**重要**: 首次同步需要 **5-15 分钟**

**同步标志**:
- 底部状态栏显示 `Gradle sync finished`
- Build 菜单不再灰色
- 没有红色错误

**如果卡住**:
- 点击右上角 🔄 图标
- 或 `File` → `Sync Project with Gradle Files`

### 步骤 3: 构建 APK

1. 点击菜单 `Build`
2. 选择 `Build Bundle(s) / APK(s)`
3. 选择 `Build APK(s)`
4. 等待构建完成

**APK 位置**: `app/build/outputs/apk/debug/app-debug.apk`

---

## ⚙️ 配置说明

### 已配置阿里云镜像

项目已配置以下镜像，无需额外配置：

```gradle
// settings.gradle
maven { url 'https://maven.aliyun.com/repository/google' }
maven { url 'https://maven.aliyun.com/repository/public' }
maven { url 'https://maven.aliyun.com/repository/gradle-plugin' }
```

### 系统要求

- **JDK**: 17+（Android Studio 会自动下载）
- **Android Studio**: Hedgehog (2023.1.1) 或更新版本
- **内存**: 建议 8GB+
- **磁盘**: 建议 10GB+ 可用空间

---

## ❓ 常见问题

### Q1: Gradle 同步一直转圈

**解决**:
1. 检查网络连接
2. 等待 10-15 分钟（首次下载依赖）
3. 如果还不行，点击 🔄 重新同步

### Q2: 提示 "JDK 17 not found"

**解决**:
1. `File` → `Settings` → `Build, Execution, Deployment` → `Gradle`
2. 点击 "Gradle JDK" 下拉框
3. 选择 "Download JDK..."
4. 选择版本 `17` → 下载

### Q3: 提示 "SDK not found"

**解决**:
1. `Tools` → `SDK Manager`
2. 安装 `Android SDK Platform 34`
3. 安装 `Android SDK Build-Tools 34`
4. 点击 `Apply`

### Q4: Build 菜单是灰色的

**原因**: Gradle 同步未完成

**解决**:
1. 等待底部显示 `Gradle sync finished`
2. 如果卡住，点击 🔄 重新同步
3. 或 `File` → `Invalidate Caches...` → 重启

### Q5: 构建失败 "Execution failed"

**解决**:
1. 查看底部 `Build` 窗口的详细错误
2. 常见原因：
   - 网络问题 → 检查网络连接
   - 内存不足 → 重启 Android Studio
   - 缺少 SDK → 安装对应 SDK

---

## 📱 安装到手机

### 方式 1: USB 连接

1. 手机开启开发者选项（设置→关于手机→连续点击"版本号"7 次）
2. 开启 USB 调试（设置→开发者选项→USB 调试）
3. USB 连接电脑
4. Android Studio 顶部会显示手机型号
5. 点击绿色▶按钮

### 方式 2: 手动安装

1. 找到 APK: `app/build/outputs/apk/debug/app-debug.apk`
2. 发送到手机（微信/QQ/USB）
3. 手机上下载安装

---

## 📊 项目结构

```
personnel-app-android-FINAL/
├── app/src/main/
│   ├── java/           (37 个 Java 文件)
│   ├── res/            (布局/颜色/主题等)
│   └── assets/         (机构/村社区数据)
├── build.gradle        (项目配置)
├── settings.gradle     (Gradle 设置)
└── README-CN.md        (本文档)
```

---

## 🎨 界面架构

```
启动页 → 一级菜单 → 二级菜单 → 三级菜单 → 四级菜单
         (首页)    (机构/村)  (人员列表) (人员详情)
```

---

## 📖 详细文档

- `README-CN.md` - 快速开始（本文档）
- `BUILD_GUIDE.md` - 完整构建指南
- `SIGNING.md` - 签名发布指南
- `TEST_GUIDE.md` - 测试指南

---

## 🆘 需要帮助？

如果遇到问题：

1. **查看错误**: 底部 `Build` 窗口
2. **搜索错误**: 复制错误信息到百度
3. **查看文档**: 阅读 `BUILD_GUIDE.md`

---

**开发者**: 秋天 · 严谨专业版  
**更新时间**: 2026-03-16

---

**✅ 已配置阿里云镜像，中国用户可直接使用！**
