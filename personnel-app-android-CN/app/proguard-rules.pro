# Add project specific ProGuard rules here.

# ==================== Apache POI ====================
-dontwarn org.apache.poi.**
-keep class org.apache.poi.** { *; }
-dontwarn org.openxmlformats.**
-keep class org.openxmlformats.** { *; }

# ==================== pinyin4j ====================
-dontwarn net.sourceforge.pinyin4j.**
-keep class net.sourceforge.pinyin4j.** { *; }

# ==================== OkHttp ====================
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ==================== Gson ====================
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ==================== 数据模型类（保持字段名） ====================
-keep class com.jieshi.personnel.model.** {
    *;
}

# ==================== Glide ====================
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# ==================== 通用规则 ====================
# 保留所有实体类的字段
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# 保留枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留 R 资源
-keepclassmembers class **.R$* {
    public static <fields>;
}
