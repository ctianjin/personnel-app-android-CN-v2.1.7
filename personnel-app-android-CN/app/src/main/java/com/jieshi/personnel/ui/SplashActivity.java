package com.jieshi.personnel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jieshi.personnel.R;

/**
 * 启动页/导入页
 * 
 * 蓝色渐变背景，简洁的导入按钮
 */
public class SplashActivity extends AppCompatActivity {
    
    private Button btnImport;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // 初始化视图
        initViews();
        
        // 设置按钮点击
        setupButtons();
    }
    
    private void initViews() {
        btnImport = findViewById(R.id.btnImport);
    }
    
    private void setupButtons() {
        btnImport.setOnClickListener(v -> {
            // 跳转到数据导入界面
            Intent intent = new Intent(SplashActivity.this, ImportActivity.class);
            startActivity(intent);
        });
    }
}
