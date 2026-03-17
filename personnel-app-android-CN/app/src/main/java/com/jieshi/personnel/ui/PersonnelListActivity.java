package com.jieshi.personnel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jieshi.personnel.R;
import com.jieshi.personnel.manager.PersonnelDataManager;
import com.jieshi.personnel.model.PersonnelInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 人员列表（三级菜单）
 * 
 * 根据机构/村社区显示人员列表
 * 驻村领导自动排首位
 * 按职位排序
 */
public class PersonnelListActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private PersonnelListAdapter adapter;
    private List<PersonnelInfo> personnelList;
    private ImageView btnBack;
    private TextView tvTitle;
    
    private String listType; // "institution" 或 "village"
    private String institutionId;
    private String institutionName;
    private String villageId;
    private String villageName;
    private String personnelType; // "village_cadre" 或 "grid_defender"
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personnel_list);
        
        // 获取传入参数
        getIntentData();
        
        // 初始化视图
        initViews();
        
        // 加载人员数据
        loadPersonnel();
        
        // 设置列表
        setupRecyclerView();
    }
    
    private void getIntentData() {
        listType = getIntent().getStringExtra("type");
        
        if ("institution".equals(listType)) {
            institutionId = getIntent().getStringExtra("institution_id");
            institutionName = getIntent().getStringExtra("institution_name");
        } else if ("village".equals(listType)) {
            villageId = getIntent().getStringExtra("village_id");
            villageName = getIntent().getStringExtra("village_name");
            personnelType = getIntent().getStringExtra("personnel_type");
        }
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        tvTitle = findViewById(R.id.tvTitle);
        
        if ("institution".equals(listType)) {
            tvTitle.setText(institutionName);
        } else if ("village".equals(listType)) {
            tvTitle.setText(villageName);
        }
        
        recyclerView = findViewById(R.id.recyclerView);
    }
    
    private void loadPersonnel() {
        personnelList = new ArrayList<>();
        
        String csvPath = new File(getFilesDir(), "personnel.csv").getAbsolutePath();
        PersonnelDataManager dataManager = new PersonnelDataManager(csvPath);
        dataManager.loadFromCsv();
        
        List<PersonnelInfo> allPersonnel = dataManager.getAllPersonnel();
        
        if ("institution".equals(listType)) {
            // 按机构筛选
            for (PersonnelInfo info : allPersonnel) {
                if (info.isTownOfficial() && institutionId.equals(info.getInstitution())) {
                    personnelList.add(info);
                }
            }
        } else if ("village".equals(listType)) {
            // 按村社区筛选
            List<PersonnelInfo> villageLeaders = new ArrayList<>();
            List<PersonnelInfo> others = new ArrayList<>();
            
            for (PersonnelInfo info : allPersonnel) {
                boolean match = false;
                
                if ("village_cadre".equals(personnelType) && info.isVillageCadre()) {
                    // 村两委干部
                    if (villageName.equals(info.getVillageCommunity()) || 
                        info.isStationedInVillage(villageName)) {
                        match = true;
                    }
                } else if ("grid_defender".equals(personnelType) && info.isGridDefender()) {
                    // 网格联防员
                    if (villageName.equals(info.getVillageCommunity())) {
                        match = true;
                    }
                }
                
                if (match) {
                    // 驻村领导排首位
                    if (info.isVillageLeader() && info.isStationedInVillage(villageName)) {
                        villageLeaders.add(info);
                    } else {
                        others.add(info);
                    }
                }
            }
            
            // 排序
            Collections.sort(villageLeaders, Comparator.comparingInt(PersonnelInfo::getPositionOrder));
            Collections.sort(others, Comparator.comparingInt(PersonnelInfo::getPositionOrder));
            
            personnelList.addAll(villageLeaders);
            personnelList.addAll(others);
        }
    }
    
    private void setupRecyclerView() {
        adapter = new PersonnelListAdapter(personnelList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
