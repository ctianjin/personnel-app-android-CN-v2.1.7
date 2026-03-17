package com.jieshi.personnel.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.jieshi.personnel.database.entity.PersonnelEntity;

import java.util.List;

/**
 * 人员数据访问对象（DAO）
 * 
 * @author 秋天 · 严谨专业版
 * @version 1.0.0
 * @since 2026-03-15
 */
@Dao
public interface PersonnelDao {
    
    /**
     * 插入人员（如果存在则替换）
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PersonnelEntity entity);
    
    /**
     * 批量插入人员
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PersonnelEntity> entities);
    
    /**
     * 更新人员
     */
    @Update
    int update(PersonnelEntity entity);
    
    /**
     * 删除人员
     */
    @Delete
    int delete(PersonnelEntity entity);
    
    /**
     * 根据 ID 删除人员
     */
    @Query("DELETE FROM personnel WHERE id = :id")
    int deleteById(String id);
    
    /**
     * 根据姓名删除人员
     */
    @Query("DELETE FROM personnel WHERE name = :name")
    int deleteByName(String name);
    
    /**
     * 删除所有人员
     */
    @Query("DELETE FROM personnel")
    void deleteAll();
    
    /**
     * 获取所有人员
     */
    @Query("SELECT * FROM personnel ORDER BY name ASC")
    List<PersonnelEntity> getAll();
    
    /**
     * 根据 ID 查询人员
     */
    @Query("SELECT * FROM personnel WHERE id = :id")
    PersonnelEntity getById(String id);
    
    /**
     * 根据姓名精确查询
     */
    @Query("SELECT * FROM personnel WHERE name = :name LIMIT 1")
    PersonnelEntity getByName(String name);
    
    /**
     * 根据姓名模糊查询
     */
    @Query("SELECT * FROM personnel WHERE name LIKE '%' || :namePart || '%' ORDER BY name ASC")
    List<PersonnelEntity> searchByName(String namePart);
    
    /**
     * 根据人员类型筛选
     */
    @Query("SELECT * FROM personnel WHERE personnel_type = :type ORDER BY name ASC")
    List<PersonnelEntity> getByType(String type);
    
    /**
     * 获取驻村领导
     */
    @Query("SELECT * FROM personnel WHERE is_village_leader = 1 ORDER BY name ASC")
    List<PersonnelEntity> getVillageLeaders();
    
    /**
     * 统计总数
     */
    @Query("SELECT COUNT(*) FROM personnel")
    int getCount();
    
    /**
     * 按类型统计
     */
    @Query("SELECT personnel_type, COUNT(*) as count FROM personnel GROUP BY personnel_type")
    List<TypeCount> getCountByType();
    
    /**
     * 类型统计辅助类
     */
    class TypeCount {
        public String personnel_type;
        public int count;
    }
}
