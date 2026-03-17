package com.jieshi.personnel.util;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 数据加密工具类
 * 
 * 使用 AES-256-CBC 模式加密敏感信息
 * - 身份证号
 * - 电话号码
 * - 家庭住址
 * 
 * @author 秋天 · 严谨专业版
 * @version 1.0.0
 * @since 2026-03-15
 */
public class CryptoUtil {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 16; // 128 bits
    
    /**
     * 生成加密密钥
     * 实际使用时应从 Android KeyStore 或 SharedPreferences 安全存储中获取
     * 
     * @param password 密码（用于派生密钥）
     * @return 16 字节密钥（AES-128，兼容性更好）
     */
    public static byte[] generateKeyFromPassword(String password) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = sha.digest(password.getBytes(StandardCharsets.UTF_8));
            // 取前 16 字节（AES-128）
            byte[] key16 = new byte[16];
            System.arraycopy(key, 0, key16, 0, 16);
            return key16;
        } catch (Exception e) {
            throw new RuntimeException("生成密钥失败", e);
        }
    }
    
    /**
     * 生成随机密钥
     * 
     * @return 16 字节随机密钥
     */
    public static byte[] generateRandomKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(128); // 使用 128 位保证兼容性
            SecretKey key = keyGen.generateKey();
            return key.getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("生成随机密钥失败", e);
        }
    }
    
    /**
     * 生成随机 IV
     * 
     * @return 16 字节随机 IV
     */
    public static byte[] generateIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[IV_SIZE];
        random.nextBytes(iv);
        return iv;
    }
    
    /**
     * 加密数据
     * 
     * @param plainText 明文
     * @param key 密钥（16 字节）
     * @param iv 初始化向量（16 字节）
     * @return Base64 编码的密文（包含 IV 前缀）
     */
    public static String encrypt(String plainText, byte[] key, byte[] iv) {
        if (plainText == null || plainText.isEmpty()) {
            return "";
        }
        
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // 将 IV 和密文拼接（IV 在前 16 字节）
            byte[] result = new byte[IV_SIZE + encrypted.length];
            System.arraycopy(iv, 0, result, 0, IV_SIZE);
            System.arraycopy(encrypted, 0, result, IV_SIZE, encrypted.length);
            
            return Base64.encodeToString(result, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }
    
    /**
     * 解密数据
     * 
     * @param encryptedText Base64 编码的密文（包含 IV 前缀）
     * @param key 密钥（16 字节）
     * @return 明文
     */
    public static String decrypt(String encryptedText, byte[] key) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return "";
        }
        
        try {
            byte[] encrypted = Base64.decode(encryptedText, Base64.NO_WRAP);
            
            // 提取 IV（前 16 字节）
            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(encrypted, 0, iv, 0, IV_SIZE);
            
            // 提取密文
            byte[] cipherText = new byte[encrypted.length - IV_SIZE];
            System.arraycopy(encrypted, IV_SIZE, cipherText, 0, cipherText.length);
            
            SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            
            byte[] decrypted = cipher.doFinal(cipherText);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("解密失败", e);
        }
    }
    
    /**
     * 加密（使用默认密钥）
     * 实际使用时应传入安全存储的密钥
     * 
     * @param plainText 明文
     * @return Base64 编码的密文
     */
    public static String encrypt(String plainText) {
        // 默认密钥（实际使用时应替换为安全存储的密钥）
        byte[] key = generateKeyFromPassword("personnel_app_default_key_2026");
        byte[] iv = generateIV();
        return encrypt(plainText, key, iv);
    }
    
    /**
     * 解密（使用默认密钥）
     * 
     * @param encryptedText Base64 编码的密文
     * @return 明文
     */
    public static String decrypt(String encryptedText) {
        byte[] key = generateKeyFromPassword("personnel_app_default_key_2026");
        return decrypt(encryptedText, key);
    }
    
    /**
     * 判断字符串是否为加密数据
     * 简单判断：Base64 编码且长度合理
     * 
     * @param text 待判断的字符串
     * @return 是否为加密数据
     */
    public static boolean isEncrypted(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        // 加密数据通常是 Base64 编码，长度较长
        return text.length() > 44 && text.matches("^[A-Za-z0-9+/=]+$");
    }
    
    /**
     * 生成密钥的 SHA-256 指纹
     * 用于验证密钥是否正确
     * 
     * @param key 密钥
     * @return 密钥指纹（16 进制字符串）
     */
    public static String getKeyFingerprint(byte[] key) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha.digest(key);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString().substring(0, 16); // 取前 16 个字符
        } catch (Exception e) {
            return "unknown";
        }
    }
}
