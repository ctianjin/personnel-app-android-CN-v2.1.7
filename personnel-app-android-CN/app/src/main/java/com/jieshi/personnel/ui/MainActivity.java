package com.jieshi.personnel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.jieshi.personnel.R;
import com.jieshi.personnel.manager.PersonnelDataManager;
import com.jieshi.personnel.model.PersonnelInfo;
import com.jieshi.personnel.util.PermissionHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 主界面（一级菜单）
 * 
 * 功能：
 * - 顶部搜索框（全局姓名搜索）
 * - 三大人员类型按钮（镇府干部/村两委干部/网格联防员）
 * - 数据更新按钮
 * - 搜索结果展示
 * 
 * @author 秋天 · 严谨专业版
 * @version 2.0.0
 * @since 2026-03-15
 */
public class MainActivity extends AppCompatActivity {
    
    private EditText searchEditText;
    private RecyclerView searchRecyclerView;
    private MaterialCardView searchResultCard;
    
    private MaterialButton btnTownOfficial;
    private MaterialButton btnVillageCadre;
    private MaterialButton btnGridDefender;
    private MaterialButton btnDataUpdate;
    
    private PersonnelDataManager dataManager;
    private SearchAdapter searchAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 初始化视图
        initViews();
        
        // 初始化工具
        initDataManager();
        
        // 检查权限
        checkPermissions();
        
        // 设置按钮点击
        setupButtons();
        
        // 设置搜索
        setupSearch();
    }
    
    /**
     * 初始化视图
     */
    private void initViews() {
        // 搜索框
        searchEditText = findViewById(R.id.searchEditText);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchResultCard = findViewById(R.id.searchResultCard);
        
        // 三大功能按钮
        btnTownOfficial = findViewById(R.id.btnTownOfficial);
        btnVillageCadre = findViewById(R.id.btnVillageCadre);
        btnGridDefender = findViewById(R.id.btnGridDefender);
        
        // 数据更新按钮
        btnDataUpdate = findViewById(R.id.btnDataUpdate);
        
        // 初始化搜索适配器
        searchAdapter = new SearchAdapter(new ArrayList<>());
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(searchAdapter);
    }
    
    /**
     * 初始化数据管理器
     */
    private void initDataManager() {
        String csvPath = new File(getFilesDir(), "personnel.csv").getAbsolutePath();
        dataManager = new PersonnelDataManager(csvPath);
        dataManager.loadFromCsv();
    }
    
    /**
     * 检查权限
     */
    private void checkPermissions() {
        if (!PermissionHelper.hasStoragePermission(this)) {
            PermissionHelper.requestStoragePermission(this);
        }
    }
    
    /**
     * 设置按钮点击
     */
    private void setupButtons() {
        // 镇府干部
        btnTownOfficial.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InstitutionListActivity.class);
            startActivity(intent);
        });
        
        // 村两委干部
        btnVillageCadre.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VillageListActivity.class);
            intent.putExtra("personnel_type", "village_cadre");
            startActivity(intent);
        });
        
        // 网格联防员
        btnGridDefender.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VillageListActivity.class);
            intent.putExtra("personnel_type", "grid_defender");
            startActivity(intent);
        });
        
        // 数据更新
        btnDataUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ImportActivity.class);
            startActivity(intent);
        });
    }
    
    /**
     * 设置搜索
     */
    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    searchResultCard.setVisibility(View.GONE);
                } else {
                    performSearch(query);
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    /**
     * 执行搜索
     */
    private void performSearch(String query) {
        List<PersonnelInfo> results = dataManager.searchByPinyin(query);
        
        if (results.isEmpty()) {
            searchResultCard.setVisibility(View.GONE);
            Toast.makeText(this, "未找到匹配的人员", Toast.LENGTH_SHORT).show();
        } else {
            searchAdapter.updateData(results);
            searchResultCard.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PermissionHelper.REQUEST_STORAGE_PERMISSION) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (!allGranted) {
                Toast.makeText(this, "权限被拒绝，部分功能可能无法使用", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 重新加载数据（可能有更新）
        initDataManager();
    }
}
