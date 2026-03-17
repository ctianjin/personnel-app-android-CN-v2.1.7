package com.jieshi.personnel.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.jieshi.personnel.R;
import com.jieshi.personnel.manager.ExcelImportManager;

import java.io.File;

/**
 * 数据导入界面
 * 
 * 支持 Excel 批量导入和头像 ZIP 包匹配
 */
public class ImportActivity extends AppCompatActivity {
    
    private MaterialButton btnSelectExcel, btnSelectAvatarZip, btnStartImport;
    private TextInputEditText etExcelPath, etAvatarZipPath;
    private MaterialCardView progressCard;
    private ProgressBar progressBar;
    private TextView tvProgress, tvStatus;
    
    private Uri excelFileUri;
    private Uri avatarZipUri;
    
    private ExcelImportManager importManager;
    
    // 文件选择器
    private final ActivityResultLauncher<String> excelLauncher = registerForActivityResult(
        new ActivityResultContracts.GetContent(),
        result -> {
            if (result != null) {
                excelFileUri = result;
                etExcelPath.setText(getFileName(result));
                btnStartImport.setEnabled(true);
            }
        }
    );
    
    private final ActivityResultLauncher<String> avatarLauncher = registerForActivityResult(
        new ActivityResultContracts.GetContent(),
        result -> {
            if (result != null) {
                avatarZipUri = result;
                etAvatarZipPath.setText(getFileName(result));
            }
        }
    );
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        
        // 初始化视图
        initViews();
        
        // 初始化管理器
        importManager = new ExcelImportManager(this);
        
        // 设置按钮点击
        setupButtons();
    }
    
    private void initViews() {
        // 工具栏
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("数据导入");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        
        // 文件选择
        btnSelectExcel = findViewById(R.id.btnSelectExcel);
        btnSelectAvatarZip = findViewById(R.id.btnSelectAvatarZip);
        btnStartImport = findViewById(R.id.btnStartImport);
        
        // 文件路径显示
        etExcelPath = findViewById(R.id.etExcelPath);
        etAvatarZipPath = findViewById(R.id.etAvatarZipPath);
        
        // 进度显示
        progressCard = findViewById(R.id.progressCard);
        progressBar = findViewById(R.id.progressBar);
        tvProgress = findViewById(R.id.tvProgress);
        tvStatus = findViewById(R.id.tvStatus);
        
        // 初始状态
        btnStartImport.setEnabled(false);
        progressCard.setVisibility(View.GONE);
    }
    
    private void setupButtons() {
        // 选择 Excel
        btnSelectExcel.setOnClickListener(v -> {
            excelLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        });
        
        // 选择头像 ZIP
        btnSelectAvatarZip.setOnClickListener(v -> {
            avatarLauncher.launch("application/zip");
        });
        
        // 开始导入
        btnStartImport.setOnClickListener(v -> {
            if (excelFileUri != null) {
                startImport();
            }
        });
    }
    
    private void startImport() {
        // 显示进度
        progressCard.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        tvProgress.setText("准备导入...");
        tvStatus.setText("");
        
        btnSelectExcel.setEnabled(false);
        btnSelectAvatarZip.setEnabled(false);
        btnStartImport.setEnabled(false);
        
        // 获取文件路径
        String excelPath = getRealPath(excelFileUri);
        String avatarZipPath = avatarZipUri != null ? getRealPath(avatarZipUri) : null;
        
        // 开始导入
        importManager.importFromExcel(
            new File(excelPath),
            avatarZipPath,
            new ExcelImportManager.ImportCallback() {
                @Override
                public void onStart() {
                    runOnUiThread(() -> {
                        tvStatus.setText("开始导入...");
                    });
                }
                
                @Override
                public void onProgress(int current, int total, String message) {
                    runOnUiThread(() -> {
                        if (total > 0) {
                            progressBar.setIndeterminate(false);
                            int percent = (int) ((float) current / total * 100);
                            progressBar.setProgress(percent);
                        }
                        tvProgress.setText(message);
                    });
                }
                
                @Override
                public void onSuccess(int count, String message) {
                    runOnUiThread(() -> {
                        progressCard.setVisibility(View.GONE);
                        Toast.makeText(ImportActivity.this, message, Toast.LENGTH_LONG).show();
                        finish();
                    });
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        progressCard.setVisibility(View.GONE);
                        btnSelectExcel.setEnabled(true);
                        btnSelectAvatarZip.setEnabled(true);
                        Toast.makeText(ImportActivity.this, error, Toast.LENGTH_LONG).show();
                    });
                }
            }
        );
    }
    
    private String getFileName(Uri uri) {
        if (uri == null) return "";
        
        String result = null;
        if (uri.getScheme().equals("content")) {
            android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        
        return result;
    }
    
    private String getRealPath(Uri uri) {
        if (uri == null) return null;
        
        // 尝试获取真实路径
        String path = null;
        android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        try {
            if (cursor != null) {
                int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                if (index >= 0 && cursor.moveToFirst()) {
                    path = cursor.getString(index);
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        
        // 如果获取失败，复制到缓存目录
        if (path == null) {
            try {
                java.io.InputStream input = getContentResolver().openInputStream(uri);
                File cacheFile = new File(getCacheDir(), getFileName(uri));
                java.io.OutputStream output = new java.io.FileOutputStream(cacheFile);
                
                byte[] buffer = new byte[4096];
                int len;
                while ((len = input.read(buffer)) > 0) {
                    output.write(buffer, 0, len);
                }
                
                input.close();
                output.close();
                path = cacheFile.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return path;
    }
}
