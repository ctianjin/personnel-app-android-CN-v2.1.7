package com.jieshi.personnel.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.jieshi.personnel.model.PersonnelInfo;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步导出管理器
 */
public class AsyncExportManager {
    
    private static final String TAG = "AsyncExportManager";
    
    private final Context context;
    private final ExecutorService executor;
    private final Handler mainHandler;
    
    private volatile boolean isCancelled = false;
    
    public interface ExportCallback {
        void onStart();
        void onProgress(int current, int total, String message);
        void onSuccess(String filePath);
        void onError(String error);
        void onCancelled();
    }
    
    public AsyncExportManager(Context context) {
        this.context = context.getApplicationContext();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public void exportToExcelAsync(final List<PersonnelInfo> personnelList,
                                    final String filePath,
                                    final ExportCallback callback) {
        isCancelled = false;
        
        executor.execute(() -> {
            try {
                if (callback != null) {
                    mainHandler.post(callback::onStart);
                }
                
                int total = personnelList.size();
                
                try (Workbook workbook = new XSSFWorkbook()) {
                    Sheet sheet = workbook.createSheet("人员信息");
                    CellStyle headerStyle = createHeaderStyle(workbook);
                    CellStyle dataStyle = createDataStyle(workbook);
                    
                    Row headerRow = sheet.createRow(0);
                    createHeaderRow(headerRow, headerStyle);
                    
                    for (int i = 0; i < total; i++) {
                        if (isCancelled) {
                            if (callback != null) {
                                mainHandler.post(callback::onCancelled);
                            }
                            return;
                        }
                        
                        PersonnelInfo info = personnelList.get(i);
                        Row row = sheet.createRow(i + 1);
                        populateRow(row, info, dataStyle);
                        
                        if (i % 100 == 0 || i == total - 1) {
                            final int current = i + 1;
                            mainHandler.post(() -> callback.onProgress(current, total, "正在导出..."));
                        }
                    }
                    
                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
                        workbook.write(fos);
                    }
                    
                    if (callback != null) {
                        mainHandler.post(() -> callback.onSuccess(filePath));
                    }
                }
            } catch (IOException e) {
                if (callback != null) {
                    mainHandler.post(() -> callback.onError("导出失败：" + e.getMessage()));
                }
            }
        });
    }
    
    public void cancel() {
        isCancelled = true;
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private void createHeaderRow(Row row, CellStyle style) {
        String[] headers = {"ID", "姓名", "性别", "身份证号", "人员类型"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }
    
    private void populateRow(Row row, PersonnelInfo info, CellStyle style) {
        row.createCell(0).setCellValue(info.getId());
        row.createCell(1).setCellValue(info.getName());
        row.createCell(2).setCellValue(info.getGender());
        row.createCell(3).setCellValue(info.getIdCard());
        row.createCell(4).setCellValue(info.getPersonnelType() != null ? 
            info.getPersonnelType().getDisplayName() : "");
    }
}
