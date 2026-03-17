package com.jieshi.personnel.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限请求帮助类
 * 
 * 适配 Android 13+ 媒体权限变更
 * 
 * @author 秋天 · 严谨专业版
 * @version 1.0.0
 * @since 2026-03-15
 */
public class PermissionHelper {
    
    /** 存储权限请求码 */
    public static final int REQUEST_STORAGE_PERMISSION = 1001;
    
    /** 相机权限请求码 */
    public static final int REQUEST_CAMERA_PERMISSION = 1002;
    
    /**
     * 获取需要的存储权限列表（根据 Android 版本）
     * 
     * @return 权限列表
     */
    public static String[] getStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+)
            return new String[] {
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11-12 (API 30-31)
            return new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE
            };
        } else {
            // Android 10 及以下
            return new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
    }
    
    /**
     * 检查是否有存储权限
     * 
     * @param context 上下文
     * @return 是否有权限
     */
    public static boolean hasStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 检查任一媒体权限
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            // Android 12 及以下检查存储权限
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    /**
     * 请求存储权限
     * 
     * @param activity Activity
     */
    public static void requestStoragePermission(Activity activity) {
        String[] permissions = getStoragePermissions();
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_STORAGE_PERMISSION);
    }
    
    /**
     * 检查是否需要请求权限
     * 
     * @param activity Activity
     * @return 是否需要请求
     */
    public static boolean shouldRequestPermission(Activity activity) {
        return !hasStoragePermission(activity);
    }
    
    /**
     * 获取所有需要的权限列表
     * 
     * @return 权限列表
     */
    public static String[] getAllPermissions() {
        List<String> permissions = new ArrayList<>();
        
        // 存储权限
        for (String perm : getStoragePermissions()) {
            permissions.add(perm);
        }
        
        // 相机权限
        permissions.add(Manifest.permission.CAMERA);
        
        return permissions.toArray(new String[0]);
    }
    
    /**
     * 检查是否所有权限都已授予
     * 
     * @param context 上下文
     * @return 是否全部授予
     */
    public static boolean hasAllPermissions(Context context) {
        for (String permission : getAllPermissions()) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 获取权限说明
     * 
     * @param permission 权限名
     * @return 说明文字
     */
    public static String getPermissionDescription(String permission) {
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return "读取设备上的文件";
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return "保存文件到设备";
            case Manifest.permission.READ_MEDIA_IMAGES:
                return "访问设备上的图片";
            case Manifest.permission.READ_MEDIA_VIDEO:
                return "访问设备上的视频";
            case Manifest.permission.READ_MEDIA_AUDIO:
                return "访问设备上的音频";
            case Manifest.permission.CAMERA:
                return "使用相机拍照";
            default:
                return "未知权限";
        }
    }
}
