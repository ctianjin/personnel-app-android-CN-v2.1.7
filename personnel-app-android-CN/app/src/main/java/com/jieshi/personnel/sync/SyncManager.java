package com.jieshi.personnel.sync;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.jieshi.personnel.manager.PersonnelDataManager;
import com.jieshi.personnel.model.PersonnelInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 数据同步管理器
 * 
 * 实现多设备间的数据同步功能
 */
public class SyncManager {
    
    private static final String TAG = "SyncManager";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private final Context context;
    private final String apiBaseUrl;
    private final String authToken;
    private final OkHttpClient httpClient;
    private final Gson gson;
    
    private final PersonnelDataManager localDataManager;
    private final File syncStateFile;
    
    private SyncState syncState;
    
    public interface SyncCallback {
        void onSuccess(int uploaded, int downloaded);
        void onError(String error);
        void onProgress(int current, int total);
    }
    
    public SyncManager(Context context, String apiBaseUrl, String authToken, 
                       PersonnelDataManager dataManager) {
        this.context = context.getApplicationContext();
        this.apiBaseUrl = apiBaseUrl;
        this.authToken = authToken;
        this.localDataManager = dataManager;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        
        this.syncStateFile = new File(context.getFilesDir(), "sync_state.json");
        loadSyncState();
    }
    
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
    
    public void sync(SyncCallback callback) {
        if (!isNetworkAvailable()) {
            callback.onError("网络连接不可用");
            return;
        }
        
        new Thread(() -> {
            try {
                SyncResponse serverChanges = fetchServerChanges();
                List<SyncItem> localChanges = getLocalChanges();
                
                MergeResult mergeResult = mergeChanges(serverChanges.items, localChanges);
                
                int uploaded = uploadChanges(mergeResult.toUpload);
                int downloaded = applyServerChanges(mergeResult.toDownload);
                
                syncState.lastSyncTime = System.currentTimeMillis();
                syncState.lastSyncStatus = "success";
                saveSyncState();
                
                callback.onSuccess(uploaded, downloaded);
                
            } catch (Exception e) {
                syncState.lastSyncStatus = "failed: " + e.getMessage();
                saveSyncState();
                callback.onError(e.getMessage());
            }
        }).start();
    }
    
    private SyncResponse fetchServerChanges() throws IOException {
        long lastSyncTime = syncState.lastSyncTime;
        String url = apiBaseUrl + "/api/personnel/sync?since=" + lastSyncTime;
        
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + authToken)
                .get()
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("服务器响应错误：" + response.code());
            }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, SyncResponse.class);
        }
    }
    
    private List<SyncItem> getLocalChanges() {
        List<SyncItem> changes = new ArrayList<>();
        List<PersonnelInfo> allPersonnel = localDataManager.getAllPersonnel();
        
        for (PersonnelInfo info : allPersonnel) {
            SyncItem item = new SyncItem();
            item.id = info.getId();
            item.action = "update";
            item.data = info;
            item.timestamp = System.currentTimeMillis();
            changes.add(item);
        }
        
        return changes;
    }
    
    private MergeResult mergeChanges(List<SyncItem> serverChanges, List<SyncItem> localChanges) {
        MergeResult result = new MergeResult();
        if (serverChanges == null) serverChanges = new ArrayList<>();
        
        Map<String, SyncItem> localIndex = new HashMap<>();
        for (SyncItem item : localChanges) {
            localIndex.put(item.id, item);
        }
        
        for (SyncItem serverItem : serverChanges) {
            SyncItem localItem = localIndex.get(serverItem.id);
            if (localItem == null) {
                result.toDownload.add(serverItem);
            } else if (serverItem.timestamp > localItem.timestamp) {
                result.toDownload.add(serverItem);
            } else if (localItem.timestamp > serverItem.timestamp) {
                result.toUpload.add(localItem);
            }
        }
        
        for (SyncItem localItem : localChanges) {
            boolean existsInServer = false;
            for (SyncItem serverItem : serverChanges) {
                if (serverItem.id != null && serverItem.id.equals(localItem.id)) {
                    existsInServer = true;
                    break;
                }
            }
            if (!existsInServer) {
                result.toUpload.add(localItem);
            }
        }
        
        return result;
    }
    
    private int uploadChanges(List<SyncItem> changes) throws IOException {
        if (changes.isEmpty()) return 0;
        
        SyncRequest request = new SyncRequest();
        request.items = changes;
        
        String json = gson.toJson(request);
        RequestBody body = RequestBody.create(json, JSON);
        
        Request httpRequest = new Request.Builder()
                .url(apiBaseUrl + "/api/personnel/sync")
                .addHeader("Authorization", "Bearer " + authToken)
                .post(body)
                .build();
        
        try (Response response = httpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("上传失败：" + response.code());
            }
            return changes.size();
        }
    }
    
    private int applyServerChanges(List<SyncItem> changes) {
        int count = 0;
        for (SyncItem item : changes) {
            if (item.data instanceof PersonnelInfo) {
                PersonnelInfo info = (PersonnelInfo) item.data;
                PersonnelInfo existing = localDataManager.searchByName(info.getName());
                if (existing == null) {
                    localDataManager.addPersonnel(info);
                } else {
                    info.setId(existing.getId());
                    localDataManager.updatePersonnel(info);
                }
                count++;
            }
        }
        return count;
    }
    
    private void loadSyncState() {
        if (syncStateFile.exists()) {
            try {
                String json = new java.util.Scanner(syncStateFile).useDelimiter("\\Z").next();
                syncState = gson.fromJson(json, SyncState.class);
            } catch (Exception e) {
                syncState = new SyncState();
            }
        } else {
            syncState = new SyncState();
        }
    }
    
    private void saveSyncState() {
        try {
            String json = gson.toJson(syncState);
            java.io.FileWriter writer = new java.io.FileWriter(syncStateFile);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            Log.e(TAG, "保存同步状态失败：" + e.getMessage());
        }
    }
    
    public long getLastSyncTime() {
        return syncState != null ? syncState.lastSyncTime : 0;
    }
    
    public static class SyncState {
        @SerializedName("last_sync_time")
        public long lastSyncTime = 0;
        @SerializedName("last_sync_status")
        public String lastSyncStatus = "never";
    }
    
    public static class SyncRequest {
        @SerializedName("items")
        public List<SyncItem> items;
    }
    
    public static class SyncResponse {
        @SerializedName("success")
        public boolean success;
        @SerializedName("items")
        public List<SyncItem> items;
    }
    
    public static class SyncItem {
        @SerializedName("id")
        public String id;
        @SerializedName("action")
        public String action;
        @SerializedName("data")
        public Object data;
        @SerializedName("timestamp")
        public long timestamp;
    }
    
    private static class MergeResult {
        public List<SyncItem> toUpload = new ArrayList<>();
        public List<SyncItem> toDownload = new ArrayList<>();
    }
}
