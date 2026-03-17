package com.jieshi.personnel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jieshi.personnel.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 村（社区）列表（二级菜单）
 * 
 * 显示 42 个村社区（3 列网格），点击跳转到人员列表
 */
public class VillageListActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private VillageAdapter adapter;
    private List<Village> villageList;
    private ImageView btnBack;
    
    private String personnelType; // "village_cadre" 或 "grid_defender"
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village_list);
        
        // 获取传入的人员类型
        personnelType = getIntent().getStringExtra("personnel_type");
        
        // 初始化视图
        initViews();
        
        // 加载村社区数据
        loadVillages();
        
        // 设置列表（3 列网格）
        setupRecyclerView();
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        recyclerView = findViewById(R.id.recyclerView);
    }
    
    private void loadVillages() {
        villageList = new ArrayList<>();
        
        try {
            InputStream is = getAssets().open("villages.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            
            String json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);
            JSONArray array = obj.getJSONArray("villages");
            
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                Village village = new Village(
                    item.getString("id"),
                    item.getString("name"),
                    item.getString("type")
                );
                villageList.add(village);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setupRecyclerView() {
        // 3 列网格布局
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        
        adapter = new VillageAdapter(villageList, personnelType, this);
        recyclerView.setAdapter(adapter);
    }
    
    /**
     * 村社区数据类
     */
    public static class Village {
        public String id;
        public String name;
        public String type; // "村" 或 "社区"
        
        public Village(String id, String name, String type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }
    }
}
