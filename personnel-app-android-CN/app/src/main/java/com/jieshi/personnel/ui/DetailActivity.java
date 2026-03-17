package com.jieshi.personnel.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jieshi.personnel.R;
import com.jieshi.personnel.manager.PersonnelDataManager;
import com.jieshi.personnel.model.PersonnelInfo;

import java.io.File;

/**
 * 人员详情（四级菜单）
 * 
 * 展示人员全维度信息，支持多身份展示
 */
public class DetailActivity extends AppCompatActivity {
    
    private ImageView ivAvatar;
    private TextView tvName, tvGender, tvBirthDate, tvEducation;
    private TextView tvWorkStartDate, tvIdentity, tvPoliticalStatus, tvAddress;
    private TextView tvPosition;
    private TextView tvWorkExperiences, tvAwards, tvFamilyMembers, tvComment;
    
    private PersonnelDataManager dataManager;
    private PersonnelInfo currentInfo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        
        // 初始化视图
        initViews();
        
        // 加载数据
        loadPersonnelData();
    }
    
    private void initViews() {
        // 返回按钮
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        // 头像
        ivAvatar = findViewById(R.id.ivAvatar);
        
        // 基本信息
        tvName = findViewById(R.id.tvName);
        tvGender = findViewById(R.id.tvGender);
        tvBirthDate = findViewById(R.id.tvBirthDate);
        tvEducation = findViewById(R.id.tvEducation);
        tvWorkStartDate = findViewById(R.id.tvWorkStartDate);
        tvIdentity = findViewById(R.id.tvIdentity);
        tvPoliticalStatus = findViewById(R.id.tvPoliticalStatus);
        tvAddress = findViewById(R.id.tvAddress);
        tvPosition = findViewById(R.id.tvPosition);
        
        // 详细信息
        tvWorkExperiences = findViewById(R.id.tvWorkExperiences);
        tvAwards = findViewById(R.id.tvAwards);
        tvFamilyMembers = findViewById(R.id.tvFamilyMembers);
        tvComment = findViewById(R.id.tvComment);
    }
    
    private void loadPersonnelData() {
        String personnelId = getIntent().getStringExtra("personnel_id");
        
        if (personnelId == null) {
            finish();
            return;
        }
        
        // 初始化数据管理器
        String csvPath = new File(getFilesDir(), "personnel.csv").getAbsolutePath();
        dataManager = new PersonnelDataManager(csvPath);
        dataManager.loadFromCsv();
        
        // 查找人员
        currentInfo = findPersonnelById(personnelId);
        
        if (currentInfo == null) {
            finish();
            return;
        }
        
        // 显示数据
        displayPersonnelInfo();
    }
    
    private PersonnelInfo findPersonnelById(String id) {
        for (PersonnelInfo info : dataManager.getAllPersonnel()) {
            if (info.getId().equals(id)) {
                return info;
            }
        }
        return null;
    }
    
    private void displayPersonnelInfo() {
        // 头像
        if (currentInfo.getAvatarPath() != null && !currentInfo.getAvatarPath().isEmpty()) {
            File avatarFile = new File(currentInfo.getAvatarPath());
            if (avatarFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(avatarFile.getAbsolutePath());
                if (bitmap != null) {
                    ivAvatar.setImageBitmap(bitmap);
                } else {
                    ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
                }
            } else {
                ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
            }
        } else {
            ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
        }
        
        // 基本信息
        tvName.setText(currentInfo.getName());
        tvGender.setText(currentInfo.getGender());
        tvBirthDate.setText(currentInfo.getBirthDate());
        tvEducation.setText(currentInfo.getEducation());
        tvWorkStartDate.setText(currentInfo.getWorkStartDate());
        tvIdentity.setText(currentInfo.getIdentityDescription());
        tvPoliticalStatus.setText(currentInfo.getPoliticalStatus());
        tvAddress.setText(currentInfo.getNativePlace());
        tvPosition.setText(currentInfo.getFullPosition());
        
        // 个人简历
        if (currentInfo.getWorkExperiences() != null && !currentInfo.getWorkExperiences().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < currentInfo.getWorkExperiences().size(); i++) {
                sb.append(currentInfo.getWorkExperiences().get(i).toString());
                if (i < currentInfo.getWorkExperiences().size() - 1) {
                    sb.append("\n");
                }
            }
            tvWorkExperiences.setText(sb.toString());
        } else {
            tvWorkExperiences.setText("暂无");
        }
        
        // 家庭成员
        if (currentInfo.getFamilyMembers() != null && !currentInfo.getFamilyMembers().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < currentInfo.getFamilyMembers().size(); i++) {
                sb.append(currentInfo.getFamilyMembers().get(i).toString());
                if (i < currentInfo.getFamilyMembers().size() - 1) {
                    sb.append("\n");
                }
            }
            tvFamilyMembers.setText(sb.toString());
        } else {
            tvFamilyMembers.setText("暂无");
        }
        
        // 奖惩信息
        if (currentInfo.getAwardPunishments() != null && !currentInfo.getAwardPunishments().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < currentInfo.getAwardPunishments().size(); i++) {
                sb.append(currentInfo.getAwardPunishments().get(i).toString());
                if (i < currentInfo.getAwardPunishments().size() - 1) {
                    sb.append("\n");
                }
            }
            tvAwards.setText(sb.toString());
        } else {
            tvAwards.setText("暂无");
        }
        
        // 简要评价
        if (currentInfo.getComment() != null && !currentInfo.getComment().isEmpty()) {
            tvComment.setText(currentInfo.getComment());
        } else {
            tvComment.setText("暂无");
        }
    }
}
