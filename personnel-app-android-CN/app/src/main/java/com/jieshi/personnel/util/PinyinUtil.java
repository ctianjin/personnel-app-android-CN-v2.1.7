package com.jieshi.personnel.util;

import android.util.Log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具类
 * 
 * 实现中文姓名转拼音、首字母缩写
 * 支持拼音搜索和首字母搜索
 * 
 * @author 秋天 · 严谨专业版
 * @version 1.0.0
 * @since 2026-03-15
 */
public class PinyinUtil {
    
    private static final String TAG = "PinyinUtil";
    
    private static HanyuPinyinOutputFormat defaultFormat;
    
    static {
        defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
    }
    
    /**
     * 将中文转换为全拼（小写，无声调）
     * 
     * @param chinese 中文字符串
     * @return 拼音字符串（空格分隔每个字）
     */
    public static String toPinyin(String chinese) {
        if (chinese == null || chinese.isEmpty()) {
            return "";
        }
        
        StringBuilder pinyin = new StringBuilder();
        char[] chars = chinese.toCharArray();
        
        for (char ch : chars) {
            // 如果是 ASCII 字符，直接添加
            if (ch < 128) {
                pinyin.append(ch);
            } else {
                try {
                    String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(ch, defaultFormat);
                    if (pinyins != null && pinyins.length > 0) {
                        pinyin.append(pinyins[0]);
                    } else {
                        pinyin.append(ch);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    pinyin.append(ch);
                }
            }
            pinyin.append(" ");
        }
        
        return pinyin.toString().trim();
    }
    
    /**
     * 将中文转换为全拼（无空格）
     * 
     * @param chinese 中文字符串
     * @return 拼音字符串（无空格）
     */
    public static String toPinyinNoSpace(String chinese) {
        return toPinyin(chinese).replace(" ", "");
    }
    
    /**
     * 获取中文首字母（大写）
     * 
     * @param chinese 中文字符串
     * @return 首字母缩写（大写）
     */
    public static String toInitials(String chinese) {
        if (chinese == null || chinese.isEmpty()) {
            return "";
        }
        
        StringBuilder initials = new StringBuilder();
        char[] chars = chinese.toCharArray();
        
        for (char ch : chars) {
            if (ch < 128) {
                initials.append(Character.toUpperCase(ch));
            } else {
                try {
                    String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(ch, defaultFormat);
                    if (pinyins != null && pinyins.length > 0) {
                        initials.append(Character.toUpperCase(pinyins[0].charAt(0)));
                    } else {
                        initials.append(ch);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    initials.append(ch);
                }
            }
        }
        
        return initials.toString();
    }
    
    /**
     * 获取中文首字母（小写）
     * 
     * @param chinese 中文字符串
     * @return 首字母缩写（小写）
     */
    public static String toInitialsLower(String chinese) {
        return toInitials(chinese).toLowerCase();
    }
    
    /**
     * 检查拼音是否匹配
     * 支持全拼匹配和首字母匹配
     * 
     * @param chinese 中文文本
     * @param query 查询词（可以是中文、拼音或首字母）
     * @return 是否匹配
     */
    public static boolean matches(String chinese, String query) {
        if (chinese == null || query == null) {
            return false;
        }
        
        if (chinese.isEmpty() || query.isEmpty()) {
            return false;
        }
        
        // 直接包含匹配（中文搜索）
        if (chinese.contains(query)) {
            return true;
        }
        
        String pinyin = toPinyinNoSpace(chinese).toLowerCase();
        String initials = toInitialsLower(chinese);
        String queryLower = query.toLowerCase().trim();
        
        // 全拼匹配
        if (pinyin.contains(queryLower)) {
            return true;
        }
        
        // 首字母匹配
        if (initials.contains(queryLower)) {
            return true;
        }
        
        // 首字母模糊匹配（如 "zsan" 匹配 "张三"）
        if (queryLower.length() >= 2) {
            String pinyinWithSpaces = toPinyin(chinese).toLowerCase();
            String[] pinyinParts = pinyinWithSpaces.split(" ");
            
            // 尝试逐字匹配
            StringBuilder partialInitials = new StringBuilder();
            for (String part : pinyinParts) {
                if (!part.isEmpty()) {
                    partialInitials.append(part.charAt(0));
                    if (queryLower.startsWith(partialInitials.toString())) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * 获取拼音索引字符串（用于快速搜索）
     * 返回格式：全拼 + 首字母 + 全拼（无空格）
     * 
     * @param chinese 中文文本
     * @return 索引字符串
     */
    public static String getPinyinIndex(String chinese) {
        if (chinese == null || chinese.isEmpty()) {
            return "";
        }
        
        String pinyin = toPinyin(chinese).toLowerCase();
        String initials = toInitialsLower(chinese);
        String pinyinNoSpace = toPinyinNoSpace(chinese).toLowerCase();
        
        return pinyin + " " + initials + " " + pinyinNoSpace;
    }
    
    /**
     * 测试方法
     */
    public static void main(String[] args) {
        String[] testNames = {"张三", "李四", "王五", "欧阳锋", "令狐冲"};
        
        for (String name : testNames) {
            Log.d(TAG, name + " -> 全拼：" + toPinyin(name));
            Log.d(TAG, name + " -> 首字母：" + toInitials(name));
            Log.d(TAG, name + " -> 索引：" + getPinyinIndex(name));
        }
        
        // 测试匹配
        Log.d(TAG, "\n=== 匹配测试 ===");
        Log.d(TAG, "张三 matches 'zhang'? " + matches("张三", "zhang"));
        Log.d(TAG, "张三 matches 'zs'? " + matches("张三", "zs"));
        Log.d(TAG, "张三 matches '张'? " + matches("张三", "张"));
        Log.d(TAG, "张三 matches 'z'? " + matches("张三", "z"));
    }
}
