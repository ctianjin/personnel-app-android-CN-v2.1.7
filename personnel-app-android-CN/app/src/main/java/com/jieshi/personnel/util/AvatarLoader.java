package com.jieshi.personnel.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 头像加载器
 * 
 * 处理 ZIP 包解压和头像匹配
 * 
 * @author 秋天 · 严谨专业版
 * @version 1.0.0
 * @since 2026-03-15
 */
public class AvatarLoader {
    
    private static final String TAG = "AvatarLoader";
    
    /**
     * 解压头像 ZIP 包
     * 
     * @param zipPath ZIP 文件路径
     * @param outputDir 输出目录
     */
    public static void extractAvatarZip(String zipPath, String outputDir) throws IOException {
        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists()) {
            outputDirFile.mkdirs();
        }
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            
            while ((entry = zis.getNextEntry()) != null) {
                // 跳过目录
                if (entry.isDirectory()) {
                    zis.closeEntry();
                    continue;
                }
                
                // 获取文件名（身份证号.jpg）
                String fileName = entry.getName();
                
                // 只处理 jpg/jpeg 文件
                if (!fileName.toLowerCase().endsWith(".jpg") && 
                    !fileName.toLowerCase().endsWith(".jpeg")) {
                    zis.closeEntry();
                    continue;
                }
                
                // 创建输出文件
                File outputFile = new File(outputDir, fileName);
                
                // 创建父目录
                File parent = outputFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                
                // 写入文件
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[4096];
                    int len;
                    
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                
                zis.closeEntry();
                Log.d(TAG, "解压头像：" + fileName);
            }
        }
        
        Log.d(TAG, "头像解压完成，输出目录：" + outputDir);
    }
    
    /**
     * 根据身份证号获取头像路径
     * 
     * @param idCard 身份证号
     * @param avatarDir 头像目录
     * @return 头像文件路径，不存在返回 null
     */
    public static String getAvatarPath(String idCard, String avatarDir) {
        if (idCard == null || idCard.isEmpty()) {
            return null;
        }
        
        // 清理身份证号（去除空格）
        String cleanIdCard = idCard.replace(" ", "").trim();
        
        // 尝试不同格式
        String[] formats = {
            cleanIdCard + ".jpg",
            cleanIdCard + ".jpeg",
            cleanIdCard.toUpperCase() + ".jpg",
            cleanIdCard.toUpperCase() + ".jpeg"
        };
        
        for (String format : formats) {
            File file = new File(avatarDir, format);
            if (file.exists()) {
                Log.d(TAG, "找到头像：" + file.getAbsolutePath());
                return file.getAbsolutePath();
            }
        }
        
        Log.d(TAG, "未找到头像：" + cleanIdCard);
        return null;
    }
    
    /**
     * 删除所有头像缓存
     */
    public static void clearAvatarCache(String avatarDir) {
        File dir = new File(avatarDir);
        if (!dir.exists()) {
            return;
        }
        
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
        
        Log.d(TAG, "头像缓存已清理");
    }
}
