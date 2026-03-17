package com.jieshi.personnel.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.jieshi.personnel.R;
import com.jieshi.personnel.manager.PersonnelDataManager;
import com.jieshi.personnel.sync.SyncManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 数据同步界面
 */
public class SyncActivity extends AppCompatActivity {
    
    private TextInputEditText etServerUrl, etAuthToken;
    private MaterialButton btnSyncNow;
    private TextView tvSyncStatus, tvLastSyncTime, tvProgress;
    private TextView tvLocalCount, tvUploadCount, tvDownloadCount, tvConflictCount;
    private ProgressBar progressBar;
    private MaterialCardView cardProgress, cardStats;
    
    private SyncManager syncManager;
    private PersonnelDataManager dataManager;
    private Handler handler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        
        handler = new Handler(Looper.getMainLooper());
        
        // 初始化视图
        initViews();
        
        // 初始化工具
        initManagers();
        
        // 加载配置
        loadConfig();
        
        // 更新状态
        updateSyncStatus();
        
        // 设置按钮点击
        setupButtons();
    }
    
    private void initViews() {
        // 工具栏
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        
        // 视图
        etServerUrl = findViewById(R.id.etServerUrl);
        etAuthToken = findViewById(R.id.etAuthToken);
        btnSyncNow = findViewById(R.id.btnSyncNow);
        tvSyncStatus = findViewById(R.id.tvSyncStatus);
        tvLastSyncTime = findViewById(R.id.tvLastSyncTime);
        tvProgress = findViewById(R.id.tvProgress);
        tvLocalCount = findViewById(R.id.tvLocalCount);
        tvUploadCount = findViewById(R.id.tvUploadCount);
        tvDownloadCount = findViewById(R.id.tvDownloadCount);
        tvConflictCount = findViewById(R.id.tvConflictCount);
        progressBar = findViewById(R.id.progressBar);
        cardProgress = findViewById(R.id.cardProgress);
        cardStats = findViewById(R.id.cardStats);
    }
    
    private void initManagers() {
        String csvPath = new File(getFilesDir(), "personnel.csv").getAbsolutePath();
        dataManager = new PersonnelDataManager(csvPath);
        dataManager.loadFromCsv();
        
        // SyncManager 将在点击同步时初始化
    }
    
    private void loadConfig() {
        // 从 SharedPreferences 加载配置
        // 实际应用中应实现配置存储
        etServerUrl.setText("https://api.example.com");
    }
    
    private void updateSyncStatus() {
        long lastSyncTime = syncManager != null ? syncManager.getLastSyncTime() : 0;
        
        if (lastSyncTime > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            String timeStr = sdf.format(new Date(lastSyncTime));
            tvSyncStatus.setText("上次同步：" + timeStr);
            tvLastSyncTime.setText("数据已是最新");
        } else {
            tvSyncStatus.setText("上次同步：从未");
            tvLastSyncTime.setText("请点击同步按钮");
        }
        
        // 更新本地数据统计
        tvLocalCount.setText(dataManager.getCount() + " 条");
    }
    
    private void setupButtons() {
        btnSyncNow.setOnClickListener(v -> startSync());
    }
    
    private void startSync() {
        String serverUrl = etServerUrl.getText().toString().trim();
        String authToken = etAuthToken.getText().toString().trim();
        
        if (serverUrl.isEmpty()) {
            Toast.makeText(this, "请输入服务器地址", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (authToken.isEmpty()) {
            Toast.makeText(this, "请输入认证令牌", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 初始化 SyncManager
        syncManager = new SyncManager(this, serverUrl, authToken, dataManager);
        
        // 检查网络
        if (!syncManager.isNetworkAvailable()) {
            Toast.makeText(this, "网络连接不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 显示进度
        cardProgress.setVisibility(View.VISIBLE);
        cardStats.setVisibility(View.GONE);
        progressBar.setIndeterminate(true);
        tvProgress.setText("正在连接服务器...");
        btnSyncNow.setEnabled(false);
        
        // 开始同步
        syncManager.sync(new SyncManager.SyncCallback() {
            @Override
            public void onSuccess(int uploaded, int downloaded) {
                handler.post(() -> {
                    progressBar.setIndeterminate(false);
                    progressBar.setProgress(100);
                    tvProgress.setText("同步完成！");
                    tvUploadCount.setText(uploaded + " 条");
                    tvDownloadCount.setText(downloaded + " 条");
                    cardStats.setVisibility(View.VISIBLE);
                    btnSyncNow.setEnabled(true);
                    
                    Toast.makeText(SyncActivity.this, 
                        "同步成功：上传 " + uploaded + " 条，下载 " + downloaded + " 条", 
                        Toast.LENGTH_LONG).show();
                    
                    updateSyncStatus();
                });
            }
            
            @Override
            public void onError(String error) {
                handler.post(() -> {
                    cardProgress.setVisibility(View.GONE);
                    btnSyncNow.setEnabled(true);
                    Toast.makeText(SyncActivity.this, 
                        "同步失败：" + error, 
                        Toast.LENGTH_LONG).show();
                });
            }
            
            @Override
            public void onProgress(int current, int total) {
                handler.post(() -> {
                    progressBar.setIndeterminate(false);
                    int progress = (int) ((float) current / total * 100);
                    progressBar.setProgress(progress);
                    tvProgress.setText("正在同步：" + current + "/" + total);
                });
            }
        });
    }
}
