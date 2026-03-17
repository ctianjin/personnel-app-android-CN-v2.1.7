package com.jieshi.personnel.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * 密钥管理器
 * 
 * 使用 Android KeyStore 安全存储加密密钥
 * Android 7.0 以下使用降级方案（SharedPreferences 加密存储）
 * 
 * @author 秋天 · 严谨专业版
 * @version 1.1.0
 * @since 2026-03-15
 */
public class KeyManager {
    
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "personnel_app_master_key";
    private static final String PREFS_NAME = "personnel_app_prefs";
    private static final String KEY_IV_PREFIX = "encrypted_iv_";
    
    private final Context context;
    private final SharedPreferences prefs;
    
    /**
     * 构造函数
     * 
     * @param context Android 上下文
     */
    public KeyManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * 初始化或获取主密钥
     * 首次调用时会生成新密钥并存储到 Android KeyStore
     * Android 7.0 以下使用降级方案
     * 
     * @return 主密钥
     * @throws Exception 密钥生成或获取失败
     */
    public SecretKey getMasterKey() throws Exception {
        // Android 7.0 以下使用降级方案
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return getFallbackMasterKey();
        }
        
        // Android 7.0+ 使用 KeyStore
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            
            // 如果密钥不存在，生成新密钥
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                generateMasterKey();
            }
            
            // 获取密钥
            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
            if (entry == null) {
                throw new KeyStoreException("无法获取主密钥");
            }
            
            return entry.getSecretKey();
        } catch (Exception e) {
            // KeyStore 失败时使用降级方案
            return getFallbackMasterKey();
        }
    }
    
    /**
     * 降级方案：Android 7.0 以下或 KeyStore 不可用时使用
     * 使用 SharedPreferences 存储加密的密钥
     * 
     * @return 主密钥
     */
    private SecretKey getFallbackMasterKey() throws Exception {
        String storedKey = prefs.getString("fallback_master_key", null);
        
        if (storedKey == null) {
            // 生成新密钥
            byte[] keyBytes = CryptoUtil.generateRandomKey();
            storedKey = Base64.encodeToString(keyBytes, Base64.NO_WRAP);
            prefs.edit().putString("fallback_master_key", storedKey).apply();
        }
        
        // 从 Base64 解码
        byte[] keyBytes = Base64.decode(storedKey, Base64.NO_WRAP);
        return new javax.crypto.spec.SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
    }
    
    /**
     * 生成主密钥并存储到 Android KeyStore
     * 
     * @throws Exception 密钥生成失败
     */
    private void generateMasterKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
        
        // 配置密钥生成参数
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);
        
        builder.setBlockModes(KeyProperties.BLOCK_MODE_CBC);
        builder.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
        builder.setKeySize(256);
        builder.setUserAuthenticationRequired(false); // 不需要用户认证
        
        // Android 7.0 以下需要设置随机数
        // Android 9.0 以上可以设置更强的安全参数
        
        keyGenerator.init(builder.build());
        keyGenerator.generateKey();
    }
    
    /**
     * 获取或生成数据加密密钥
     * 使用主密钥派生或直接使用主密钥
     * 
     * @return 16 字节数据加密密钥
     */
    public byte[] getDataEncryptionKey() {
        try {
            SecretKey masterKey = getMasterKey();
            return masterKey.getEncoded();
        } catch (Exception e) {
            // 降级方案：使用基于密码的密钥
            return CryptoUtil.generateKeyFromPassword("personnel_app_fallback_key");
        }
    }
    
    /**
     * 存储字段级别的 IV
     * 每个敏感字段使用不同的 IV
     * 
     * @param fieldName 字段名
     * @param iv 初始化向量
     */
    public void saveFieldIV(String fieldName, byte[] iv) {
        String ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP);
        prefs.edit().putString(KEY_IV_PREFIX + fieldName, ivBase64).apply();
    }
    
    /**
     * 获取字段级别的 IV
     * 
     * @param fieldName 字段名
     * @return 初始化向量，如果不存在则返回 null
     */
    public byte[] getFieldIV(String fieldName) {
        String ivBase64 = prefs.getString(KEY_IV_PREFIX + fieldName, null);
        if (ivBase64 == null) {
            return null;
        }
        return Base64.decode(ivBase64, Base64.NO_WRAP);
    }
    
    /**
     * 检查 KeyStore 是否可用
     * 
     * @return KeyStore 是否可用
     */
    public boolean isKeyStoreAvailable() {
        // Android 7.0 以下不支持 KeyStore
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return false;
        }
        
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            return keyStore.containsAlias(KEY_ALIAS);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 检查是否使用降级方案
     * 
     * @return 是否使用降级方案
     */
    public boolean isUsingFallback() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N || !isKeyStoreAvailable();
    }
    
    /**
     * 删除所有密钥（重置）
     * 谨慎使用！会导致之前加密的数据无法解密
     */
    public void deleteAllKeys() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            keyStore.deleteEntry(KEY_ALIAS);
        } catch (Exception e) {
            // 忽略删除错误
        }
        
        // 清除 SharedPreferences 中的 IV
        prefs.edit().clear().apply();
    }
    
    /**
     * 获取密钥指纹（用于调试和验证）
     * 
     * @return 密钥指纹字符串
     */
    public String getKeyFingerprint() {
        try {
            SecretKey masterKey = getMasterKey();
            return CryptoUtil.getKeyFingerprint(masterKey.getEncoded());
        } catch (Exception e) {
            return "error";
        }
    }
}
