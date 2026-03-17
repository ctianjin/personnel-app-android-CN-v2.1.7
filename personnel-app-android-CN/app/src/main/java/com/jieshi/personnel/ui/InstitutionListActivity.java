package com.jieshi.personnel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jieshi.personnel.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 镇府内设机构列表（二级菜单）
 * 
 * 显示 16 个镇府内设机构，点击跳转到人员列表
 */
public class InstitutionListActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private InstitutionAdapter adapter;
    private List<Institution> institutionList;
    private ImageView btnBack;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution_list);
        
        // 初始化视图
        initViews();
        
        // 加载机构数据
        loadInstitutions();
        
        // 设置列表
        setupRecyclerView();
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        recyclerView = findViewById(R.id.recyclerView);
    }
    
    private void loadInstitutions() {
        institutionList = new ArrayList<>();
        
        try {
            InputStream is = getAssets().open("institutions.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            
            String json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);
            JSONArray array = obj.getJSONArray("institutions");
            
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                Institution institution = new Institution(
                    item.getString("id"),
                    item.getString("name"),
                    item.getInt("order")
                );
                institutionList.add(institution);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setupRecyclerView() {
        adapter = new InstitutionAdapter(institutionList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    /**
     * 机构数据类
     */
    public static class Institution {
        public String id;
        public String name;
        public int order;
        
        public Institution(String id, String name, int order) {
            this.id = id;
            this.name = name;
            this.order = order;
        }
    }
}
