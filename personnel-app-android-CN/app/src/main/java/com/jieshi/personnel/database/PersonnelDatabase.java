package com.jieshi.personnel.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jieshi.personnel.database.dao.PersonnelDao;
import com.jieshi.personnel.database.entity.PersonnelEntity;

/**
 * 人员信息数据库（Room）
 * 
 * 单例模式，线程安全
 * 
 * @author 秋天 · 严谨专业版
 * @version 1.0.0
 * @since 2026-03-15
 */
@Database(
    entities = {PersonnelEntity.class},
    version = 1,
    exportSchema = true
)
public abstract class PersonnelDatabase extends RoomDatabase {
    
    private static volatile PersonnelDatabase INSTANCE;
    private static final String DATABASE_NAME = "personnel_db";
    
    /**
     * 获取数据库实例（单例）
     */
    public static PersonnelDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PersonnelDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            PersonnelDatabase.class,
                            DATABASE_NAME
                        )
                        .fallbackToDestructiveMigration() // 开发阶段使用，生产环境应实现 Migration
                        .build();
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * 获取人员 DAO
     */
    public abstract PersonnelDao personnelDao();
    
    /**
     * 关闭数据库
     */
    public static void closeInstance() {
        if (INSTANCE != null && INSTANCE.isOpen()) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }
}
