package com.jieshi.personnel.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jieshi.personnel.BuildConfig;
import com.jieshi.personnel.R;
import com.jieshi.personnel.manager.AsyncExportManager;
import com.jieshi.personnel.manager.ExcelDataManager;
import com.jieshi.personnel.manager.PersonnelDataManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 设置界面
 */
public class SettingsActivity extends AppCompatActivity {
    
    private LinearLayout layoutExport, layoutImport, layoutClearCache;
    private TextView tvVersion;
    
    private PersonnelDataManager dataManager;
    private ExcelDataManager excelManager;
    
    private static final int REQUEST_IMPORT = 1001;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        initViews();
        initManagers();
        setupClickListeners();
        showVersion();
    }
    
    private void initViews() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        
        layoutExport = findViewById(R.id.layoutExport);
        layoutImport = findViewById(R.id.layoutImport);
        layoutClearCache = findViewById(R.id.layoutClearCache);
        tvVersion = findViewById(R.id.tvVersion);
    }
    
    private void initManagers() {
        String csvPath = new File(getFilesDir(), "personnel.csv").getAbsolutePath();
        dataManager = new PersonnelDataManager(csvPath);
        dataManager.loadFromCsv();
        
        String excelPath = new File(getFilesDir(), "personnel.xlsx").getAbsolutePath();
        excelManager = new ExcelDataManager(this, excelPath);
    }
    
    private void setupClickListeners() {
        layoutExport.setOnClickListener(v -> exportData());
        layoutImport.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImportActivity.class);
            startActivity(intent);
        });
        layoutClearCache.setOnClickListener(v -> clearCache());
    }
    
    private void showVersion() {
        tvVersion.setText("版本 " + BuildConfig.VERSION_NAME);
    }
    
    private void exportData() {
        new AlertDialog.Builder(this)
            .setTitle("导出数据")
            .setMessage("选择导出格式")
            .setPositiveButton("Excel", (dialog, which) -> exportToExcel())
            .setNegativeButton("CSV", (dialog, which) -> exportToCsv())
            .setNeutralButton("取消", null)
            .show();
    }
    
    private void exportToExcel() {
        final AsyncExportManager asyncExport = new AsyncExportManager(this);
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String fileName = "personnel_export_" + timestamp + ".xlsx";
        File exportDir = getExternalFilesDir(null);
        if (exportDir == null) exportDir = getFilesDir();
        File exportFile = new File(exportDir, fileName);
        
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setTitle("导出数据");
        progressDialog.setMessage("正在准备导出...");
        progressDialog.setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setButton(android.app.ProgressDialog.BUTTON_NEGATIVE, "取消", 
            (dialog, which) -> asyncExport.cancel());
        progressDialog.show();
        
        asyncExport.exportToExcelAsync(dataManager.getAllPersonnel(), 
            exportFile.getAbsolutePath(),
            new AsyncExportManager.ExportCallback() {
                @Override
                public void onStart() {
                    progressDialog.setMessage("正在导出...");
                }
                
                @Override
                public void onProgress(int current, int total, String message) {
                    int percent = (int) ((float) current / total * 100);
                    progressDialog.setProgress(percent);
                    progressDialog.setMessage(message);
                }
                
                @Override
                public void onSuccess(String filePath) {
                    progressDialog.dismiss();
                    Toast.makeText(SettingsActivity.this, "导出成功：" + filePath, Toast.LENGTH_LONG).show();
                }
                
                @Override
                public void onError(String error) {
                    progressDialog.dismiss();
                    Toast.makeText(SettingsActivity.this, "导出失败：" + error, Toast.LENGTH_LONG).show();
                }
                
                @Override
                public void onCancelled() {
                    progressDialog.dismiss();
                    Toast.makeText(SettingsActivity.this, "导出已取消", Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void exportToCsv() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String fileName = "personnel_export_" + timestamp + ".csv";
        File exportDir = getExternalFilesDir(null);
        if (exportDir == null) exportDir = getFilesDir();
        File exportFile = new File(exportDir, fileName);
        
        if (dataManager.exportToCsv(exportFile.getAbsolutePath())) {
            Toast.makeText(this, "导出成功：" + exportFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "导出失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void clearCache() {
        new AlertDialog.Builder(this)
            .setTitle("清除缓存")
            .setMessage("确定要清除应用缓存吗？")
            .setPositiveButton("确定", (dialog, which) -> {
                getCacheDir().delete();
                Toast.makeText(this, "缓存已清除", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("取消", null)
            .show();
    }
}
