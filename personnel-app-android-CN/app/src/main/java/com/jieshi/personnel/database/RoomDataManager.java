package com.jieshi.personnel.database;

import android.content.Context;

import com.jieshi.personnel.database.dao.PersonnelDao;
import com.jieshi.personnel.database.entity.PersonnelEntity;
import com.jieshi.personnel.model.PersonnelInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Room 数据库数据管理器
 * 
 * 提供异步数据库操作，避免阻塞主线程
 * 
 * @author 秋天 · 严谨专业版
 * @version 1.0.0
 * @since 2026-03-15
 */
public class RoomDataManager {
    
    private final PersonnelDao dao;
    private final ExecutorService executor;
    
    /**
     * 构造函数
     */
    public RoomDataManager(Context context) {
        PersonnelDatabase db = PersonnelDatabase.getInstance(context);
        this.dao = db.personnelDao();
        this.executor = Executors.newSingleThreadExecutor();
    }
    
    /**
     * 插入人员（异步）
     */
    public void insert(PersonnelInfo info, Callback<Long> callback) {
        executor.execute(() -> {
            PersonnelEntity entity = new PersonnelEntity(info);
            long id = dao.insert(entity);
            if (callback != null) {
                callback.onResult(id);
            }
        });
    }
    
    /**
     * 插入人员（同步）
     */
    public long insertSync(PersonnelInfo info) {
        PersonnelEntity entity = new PersonnelEntity(info);
        return dao.insert(entity);
    }
    
    /**
     * 批量插入（异步）
     */
    public void insertAll(List<PersonnelInfo> list, Callback<Void> callback) {
        executor.execute(() -> {
            List<PersonnelEntity> entities = new ArrayList<>();
            for (PersonnelInfo info : list) {
                entities.add(new PersonnelEntity(info));
            }
            dao.insertAll(entities);
            if (callback != null) {
                callback.onResult(null);
            }
        });
    }
    
    /**
     * 更新人员（异步）
     */
    public void update(PersonnelInfo info, Callback<Integer> callback) {
        executor.execute(() -> {
            PersonnelEntity entity = new PersonnelEntity(info);
            int count = dao.update(entity);
            if (callback != null) {
                callback.onResult(count);
            }
        });
    }
    
    /**
     * 删除人员（异步）
     */
    public void delete(PersonnelInfo info, Callback<Integer> callback) {
        executor.execute(() -> {
            PersonnelEntity entity = new PersonnelEntity(info);
            int count = dao.delete(entity);
            if (callback != null) {
                callback.onResult(count);
            }
        });
    }
    
    /**
     * 根据 ID 删除（异步）
     */
    public void deleteById(String id, Callback<Integer> callback) {
        executor.execute(() -> {
            int count = dao.deleteById(id);
            if (callback != null) {
                callback.onResult(count);
            }
        });
    }
    
    /**
     * 获取所有人员（异步）
     */
    public void getAll(Callback<List<PersonnelInfo>> callback) {
        executor.execute(() -> {
            List<PersonnelEntity> entities = dao.getAll();
            List<PersonnelInfo> list = new ArrayList<>();
            for (PersonnelEntity entity : entities) {
                list.add(entity.toPersonnelInfo());
            }
            if (callback != null) {
                callback.onResult(list);
            }
        });
    }
    
    /**
     * 获取所有人员（同步）
     */
    public List<PersonnelInfo> getAllSync() {
        List<PersonnelEntity> entities = dao.getAll();
        List<PersonnelInfo> list = new ArrayList<>();
        for (PersonnelEntity entity : entities) {
            list.add(entity.toPersonnelInfo());
        }
        return list;
    }
    
    /**
     * 根据 ID 查询（异步）
     */
    public void getById(String id, Callback<PersonnelInfo> callback) {
        executor.execute(() -> {
            PersonnelEntity entity = dao.getById(id);
            PersonnelInfo info = entity != null ? entity.toPersonnelInfo() : null;
            if (callback != null) {
                callback.onResult(info);
            }
        });
    }
    
    /**
     * 根据姓名查询（异步）
     */
    public void getByName(String name, Callback<PersonnelInfo> callback) {
        executor.execute(() -> {
            PersonnelEntity entity = dao.getByName(name);
            PersonnelInfo info = entity != null ? entity.toPersonnelInfo() : null;
            if (callback != null) {
                callback.onResult(info);
            }
        });
    }
    
    /**
     * 模糊搜索（异步）
     */
    public void searchByName(String namePart, Callback<List<PersonnelInfo>> callback) {
        executor.execute(() -> {
            List<PersonnelEntity> entities = dao.searchByName(namePart);
            List<PersonnelInfo> list = new ArrayList<>();
            for (PersonnelEntity entity : entities) {
                list.add(entity.toPersonnelInfo());
            }
            if (callback != null) {
                callback.onResult(list);
            }
        });
    }
    
    /**
     * 按类型筛选（异步）
     */
    public void getByType(String type, Callback<List<PersonnelInfo>> callback) {
        executor.execute(() -> {
            List<PersonnelEntity> entities = dao.getByType(type);
            List<PersonnelInfo> list = new ArrayList<>();
            for (PersonnelEntity entity : entities) {
                list.add(entity.toPersonnelInfo());
            }
            if (callback != null) {
                callback.onResult(list);
            }
        });
    }
    
    /**
     * 获取驻村领导（异步）
     */
    public void getVillageLeaders(Callback<List<PersonnelInfo>> callback) {
        executor.execute(() -> {
            List<PersonnelEntity> entities = dao.getVillageLeaders();
            List<PersonnelInfo> list = new ArrayList<>();
            for (PersonnelEntity entity : entities) {
                list.add(entity.toPersonnelInfo());
            }
            if (callback != null) {
                callback.onResult(list);
            }
        });
    }
    
    /**
     * 获取总数（异步）
     */
    public void getCount(Callback<Integer> callback) {
        executor.execute(() -> {
            int count = dao.getCount();
            if (callback != null) {
                callback.onResult(count);
            }
        });
    }
    
    /**
     * 清空数据库（异步）
     */
    public void deleteAll(Callback<Void> callback) {
        executor.execute(() -> {
            dao.deleteAll();
            if (callback != null) {
                callback.onResult(null);
            }
        });
    }
    
    /**
     * 操作回调接口
     */
    public interface Callback<T> {
        void onResult(T result);
    }
}
