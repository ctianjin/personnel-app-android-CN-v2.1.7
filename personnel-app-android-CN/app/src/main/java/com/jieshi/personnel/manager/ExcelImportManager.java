package com.jieshi.personnel.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.jieshi.personnel.model.PersonnelInfo;
import com.jieshi.personnel.util.AvatarLoader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Excel 导入管理器
 */
public class ExcelImportManager {
    
    private static final String TAG = "ExcelImportManager";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    
    private final Context context;
    private final Handler mainHandler;
    
    public interface ImportCallback {
        void onStart();
        void onProgress(int current, int total, String message);
        void onSuccess(int count, String message);
        void onError(String error);
    }
    
    public ExcelImportManager(Context context) {
        this.context = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public void importFromExcel(final File excelFile, final String avatarZipPath, 
                                 final ImportCallback callback) {
        new Thread(() -> {
            try {
                if (callback != null) {
                    mainHandler.post(callback::onStart);
                }
                
                List<PersonnelInfo> personnelList = parseExcel(excelFile, callback);
                
                if (personnelList == null || personnelList.isEmpty()) {
                    if (callback != null) {
                        mainHandler.post(() -> callback.onError("Excel 文件中没有有效数据"));
                    }
                    return;
                }
                
                if (avatarZipPath != null && !avatarZipPath.isEmpty()) {
                    processAvatars(personnelList, avatarZipPath);
                }
                
                String csvPath = new File(context.getFilesDir(), "personnel.csv").getAbsolutePath();
                PersonnelDataManager dataManager = new PersonnelDataManager(csvPath);
                dataManager.loadFromCsv();
                
                int count = 0;
                final int total = personnelList.size();
                for (PersonnelInfo info : personnelList) {
                    PersonnelInfo existing = dataManager.searchByName(info.getName());
                    if (existing != null && existing.getIdCard() != null && 
                        existing.getIdCard().equals(info.getIdCard())) {
                        info.setId(existing.getId());
                        dataManager.updatePersonnel(info);
                    } else {
                        dataManager.addPersonnel(info);
                    }
                    count++;
                    
                    if (callback != null) {
                        final int finalCount = count;
                        mainHandler.post(() -> 
                            callback.onProgress(finalCount, total, 
                                "正在保存：" + finalCount + "/" + total));
                    }
                }
                
                final int finalTotal = count;
                if (callback != null) {
                    mainHandler.post(() -> 
                        callback.onSuccess(finalTotal, "成功导入 " + finalTotal + " 条人员信息"));
                }
                
            } catch (Exception e) {
                if (callback != null) {
                    mainHandler.post(() -> callback.onError("导入失败：" + e.getMessage()));
                }
            }
        }).start();
    }
    
    private List<PersonnelInfo> parseExcel(File excelFile, ImportCallback callback) 
            throws IOException {
        List<PersonnelInfo> list = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            final int totalRows = sheet.getLastRowNum() + 1;
            
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) throw new IOException("Excel 文件没有表头");
            
            Map<String, Integer> columnIndex = parseHeader(headerRow);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                PersonnelInfo info = parseRow(row, columnIndex);
                if (info != null) list.add(info);
                
                if (callback != null) {
                    final int current = i;
                    mainHandler.post(() -> 
                        callback.onProgress(current, totalRows, "正在解析..."));
                }
            }
        }
        return list;
    }
    
    private Map<String, Integer> parseHeader(Row headerRow) {
        Map<String, Integer> columnIndex = new HashMap<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                columnIndex.put(cell.getStringCellValue().trim().toLowerCase(), i);
            }
        }
        return columnIndex;
    }
    
    private PersonnelInfo parseRow(Row row, Map<String, Integer> columnIndex) {
        PersonnelInfo info = new PersonnelInfo();
        
        info.setName(getCellValue(row, columnIndex, "姓名"));
        info.setGender(getCellValue(row, columnIndex, "性别"));
        info.setIdCard(getCellValue(row, columnIndex, "身份证号"));
        info.setPhone(getCellValue(row, columnIndex, "联系电话"));
        info.setAddress(getCellValue(row, columnIndex, "现住址"));
        
        String now = new SimpleDateFormat(DATE_FORMAT, Locale.CHINA).format(new Date());
        info.setCreateTime(now);
        info.setUpdateTime(now);
        info.setStatus("NORMAL");
        
        return info;
    }
    
    private String getCellValue(Row row, Map<String, Integer> columnIndex, String columnName) {
        Integer index = columnIndex.get(columnName.toLowerCase());
        if (index == null) return "";
        Cell cell = row.getCell(index);
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            default: return "";
        }
    }
    
    private void processAvatars(List<PersonnelInfo> personnelList, String avatarZipPath) {
        try {
            String avatarDir = new File(context.getFilesDir(), "avatars").getAbsolutePath();
            AvatarLoader.extractAvatarZip(avatarZipPath, avatarDir);
            for (PersonnelInfo info : personnelList) {
                if (info.getIdCard() != null && !info.getIdCard().isEmpty()) {
                    String avatarPath = AvatarLoader.getAvatarPath(info.getIdCard(), avatarDir);
                    if (avatarPath != null) info.setAvatarPath(avatarPath);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "头像处理失败：" + e.getMessage());
        }
    }
}
