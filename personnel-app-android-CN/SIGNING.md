# APK 签名配置指南

## 步骤 1: 生成签名密钥库

### 方式 1: 使用 Android Studio（推荐）

1. 点击菜单 `Build` → `Generate Signed Bundle / APK`
2. 选择 `APK`，点击 `Next`
3. 点击 `Create new...` 创建新密钥库
4. 填写信息：
   - **Key store path**: `/path/to/personnel-app-key.jks`
   - **Password**: （设置强密码，至少 8 位）
   - **Alias**: `personnel-app`
   - **Validity**: `25` 年
   - **证书信息**: 填写公司/个人信息

5. 点击 `OK` 生成密钥库

### 方式 2: 使用命令行

```bash
# 生成密钥库
keytool -genkey -v \
  -keystore personnel-app-key.jks \
  -alias personnel-app \
  -keyalg RSA \
  -keysize 2048 \
  -validity 9125

# 按提示输入：
# - 密钥库密码
# - 姓名（组织名称）
# - 组织单位（部门）
# - 组织（公司）
# - 城市
# - 省份
# - 国家代码
```

## 步骤 2: 配置签名参数

### 安全方式：使用 local.properties（推荐）

在项目根目录创建 `local.properties` 文件（**不提交到 Git**）：

```properties
RELEASE_STORE_FILE=/absolute/path/to/personnel-app-key.jks
RELEASE_STORE_PASSWORD=your_keystore_password
RELEASE_KEY_ALIAS=personnel-app
RELEASE_KEY_PASSWORD=your_key_password
```

### 或者：使用 gradle.properties（不推荐）

```properties
# 注意：不要将此文件提交到版本控制！
RELEASE_STORE_FILE=/absolute/path/to/personnel-app-key.jks
RELEASE_STORE_PASSWORD=your_keystore_password
RELEASE_KEY_ALIAS=personnel-app
RELEASE_KEY_PASSWORD=your_key_password
```

## 步骤 3: 更新 build.gradle

已在 `app/build.gradle` 中配置签名：

```gradle
android {
    ...
    signingConfigs {
        release {
            storeFile file(RELEASE_STORE_FILE)
            storePassword RELEASE_STORE_PASSWORD
            keyAlias RELEASE_KEY_ALIAS
            keyPassword RELEASE_KEY_PASSWORD
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles ...
        }
    }
}
```

## 步骤 4: 构建签名 APK

### 命令行方式

```bash
# 清理并构建 Release 版
./gradlew clean assembleRelease

# APK 输出位置：
# app/build/outputs/apk/release/app-release.apk
```

### Android Studio 方式

1. 点击菜单 `Build` → `Generate Signed Bundle / APK`
2. 选择 `APK`
3. 选择 `release` 构建类型
4. 点击 `Finish`

## 步骤 5: 验证签名

```bash
# 验证 APK 签名
apksigner verify --verbose app-release.apk

# 查看签名详情
apksigner verify --print-certs app-release.apk
```

## 步骤 6: 安全建议

### ⚠️ 重要：保护密钥库

1. **不要提交到 Git**
   ```bash
   # .gitignore
   *.jks
   *.keystore
   local.properties
   ```

2. **备份密钥库**
   - 备份到安全位置（加密云存储/硬件钱包）
   - 记录密码到密码管理器
   - 密钥丢失 = 无法更新应用

3. **限制访问**
   - 仅授权人员可访问
   - 使用强密码（12 位以上，包含大小写、数字、符号）

### 密钥库信息查看

```bash
# 查看密钥库详情
keytool -list -v -keystore personnel-app-key.jks

# 查看证书指纹（用于应用签名验证）
keytool -list -v -keystore personnel-app-key.jks -alias personnel-app
```

## 故障排除

### Q: 签名失败 "Keystore was tampered with, or password was incorrect"
A: 检查密码是否正确，或密钥库文件是否损坏

### Q: 找不到密钥库文件
A: 使用绝对路径，或确保路径正确

### Q: 构建成功但 APK 未签名
A: 检查 `build.gradle` 中 `signingConfig` 是否正确配置

## 发布检查清单

- [ ] 密钥库已生成并备份
- [ ] 签名参数已配置
- [ ] Release APK 已构建
- [ ] APK 已验证签名
- [ ] 密钥库已从项目目录移除
- [ ] local.properties 已添加到 .gitignore
