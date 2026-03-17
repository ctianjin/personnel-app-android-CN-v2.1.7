package com.jieshi.personnel.manager;

import android.content.Context;
import android.util.Log;

import com.jieshi.personnel.model.PersonnelInfo;
import com.jieshi.personnel.model.PersonnelType;
import com.jieshi.personnel.model.GovernmentLevel;
import com.jieshi.personnel.model.VillageLevel;
import com.jieshi.personnel.model.WorkExperience;
import com.jieshi.personnel.model.AwardPunishment;
import com.jieshi.personnel.model.FamilyMember;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Excel 数据管理器
 * 
 * 使用 Apache POI 实现 Excel (.xlsx) 文件的导入导出
 * 支持全字段读写，包含样式和格式
 * 
 * @author 秋天 · 严谨专业版
 * @version 1.0.0
 * @since 2026-03-15
 */
public class ExcelDataManager {
    
    private static final String TAG = "ExcelDataManager";
    
    private final Context context;
    private final String defaultExcelPath;
    
    // Excel 列索引定义
    private static final int COL_ID = 0;
    private static final int COL_NAME = 1;
    private static final int COL_GENDER = 2;
    private static final int COL_BIRTH_DATE = 3;
    private static final int COL_ETHNICITY = 4;
    private static final int COL_POLITICAL_STATUS = 5;
    private static final int COL_EDUCATION = 6;
    private static final int COL_MAJOR = 7;
    private static final int COL_WORK_START_DATE = 8;
    private static final int COL_PHONE = 9;
    private static final int COL_PERSONNEL_TYPE = 10;
    private static final int COL_TOWN_NAME = 11;
    private static final int COL_DEPARTMENT = 12;
    private static final int COL_POSITION = 13;
    private static final int COL_RANK = 14;
    private static final int COL_IS_VILLAGE_LEADER = 15;
    private static final int COL_STATIONED_VILLAGES = 16;
    private static final int COL_VILLAGE_NAME = 17;
    private static final int COL_VILLAGE_TYPE = 18;
    private static final int COL_VILLAGE_POSITION = 19;
    private static final int COL_HAS_MULTIPLE_IDENTITIES = 20;
    private static final int COL_STATUS = 21;
    private static final int COL_CREATE_TIME = 22;
    private static final int COL_UPDATE_TIME = 23;
    
    /**
     * 构造函数
     * 
     * @param context Android 上下文
     * @param excelPath Excel 文件路径
     */
    public ExcelDataManager(Context context, String excelPath) {
        this.context = context.getApplicationContext();
        this.defaultExcelPath = excelPath;
    }
    
    /**
     * 从 Excel 文件加载人员信息
     * 
     * @return 加载成功返回 true，失败返回 false
     */
    public List<PersonnelInfo> loadFromExcel() {
        return loadFromExcel(defaultExcelPath);
    }
    
    /**
     * 从指定路径的 Excel 文件加载人员信息
     * 
     * @param excelPath Excel 文件路径
     * @return 人员信息列表
     */
    public List<PersonnelInfo> loadFromExcel(String excelPath) {
        List<PersonnelInfo> personnelList = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // 跳过标题行（第 0 行）
            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();
            
            Log.d(TAG, "Excel 行数：" + (lastRowNum - firstRowNum));
            
            for (int i = firstRowNum + 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    PersonnelInfo info = parseRow(row);
                    if (info != null) {
                        personnelList.add(info);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "解析第 " + (i + 1) + " 行失败：" + e.getMessage());
                }
            }
            
            Log.d(TAG, "成功加载 " + personnelList.size() + " 条记录");
            
        } catch (IOException e) {
            Log.e(TAG, "读取 Excel 文件失败：" + e.getMessage());
        }
        
        return personnelList;
    }
    
    /**
     * 解析 Excel 行数据为 PersonnelInfo 对象
     * 
     * @param row Excel 行
     * @return PersonnelInfo 对象
     */
    private PersonnelInfo parseRow(Row row) {
        PersonnelInfo info = new PersonnelInfo();
        
        info.setId(getCellStringValue(row, COL_ID));
        info.setName(getCellStringValue(row, COL_NAME));
        info.setGender(getCellStringValue(row, COL_GENDER));
        info.setBirthDate(getCellStringValue(row, COL_BIRTH_DATE));
        info.setEthnicity(getCellStringValue(row, COL_ETHNICITY));
        info.setPoliticalStatus(getCellStringValue(row, COL_POLITICAL_STATUS));
        info.setEducation(getCellStringValue(row, COL_EDUCATION));
        info.setMajor(getCellStringValue(row, COL_MAJOR));
        info.setWorkStartDate(getCellStringValue(row, COL_WORK_START_DATE));
        info.setPhone(getCellStringValue(row, COL_PHONE));
        
        // 人员类型
        String typeStr = getCellStringValue(row, COL_PERSONNEL_TYPE);
        info.setPersonnelType(PersonnelType.fromDisplayName(typeStr));
        
        // 政府层级
        String townName = getCellStringValue(row, COL_TOWN_NAME);
        if (!townName.isEmpty()) {
            GovernmentLevel govLevel = new GovernmentLevel();
            govLevel.setTownName(townName);
            govLevel.setDepartment(getCellStringValue(row, COL_DEPARTMENT));
            govLevel.setPosition(getCellStringValue(row, COL_POSITION));
            govLevel.setRank(getCellStringValue(row, COL_RANK));
            govLevel.setVillageLeader("是".equals(getCellStringValue(row, COL_IS_VILLAGE_LEADER)));
            
            // 驻村列表（用 | 分隔）
            String villages = getCellStringValue(row, COL_STATIONED_VILLAGES);
            if (!villages.isEmpty()) {
                govLevel.setStationedVillages(villages.split("\\|"));
            }
            
            info.setGovernmentLevel(govLevel);
        }
        
        // 村层级
        String villageName = getCellStringValue(row, COL_VILLAGE_NAME);
        if (!villageName.isEmpty()) {
            VillageLevel villageLevel = new VillageLevel();
            villageLevel.setVillageName(villageName);
            villageLevel.setVillageType(getCellStringValue(row, COL_VILLAGE_TYPE));
            villageLevel.setPosition(getCellStringValue(row, COL_VILLAGE_POSITION));
            
            info.setVillageLevel(villageLevel);
        }
        
        // 多身份
        info.setHasMultipleIdentities("是".equals(getCellStringValue(row, COL_HAS_MULTIPLE_IDENTITIES)));
        
        // 系统字段
        info.setStatus(getCellStringValue(row, COL_STATUS));
        info.setCreateTime(getCellStringValue(row, COL_CREATE_TIME));
        info.setUpdateTime(getCellStringValue(row, COL_UPDATE_TIME));
        
        return info;
    }
    
    /**
     * 导出人员信息到 Excel 文件
     * 
     * @param personnelList 人员信息列表
     * @return 导出成功返回 true
     */
    public boolean exportToExcel(List<PersonnelInfo> personnelList) {
        return exportToExcel(personnelList, defaultExcelPath);
    }
    
    /**
     * 导出人员信息到指定路径的 Excel 文件
     * 
     * @param personnelList 人员信息列表
     * @param excelPath Excel 文件路径
     * @return 导出成功返回 true
     */
    public boolean exportToExcel(List<PersonnelInfo> personnelList, String excelPath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("人员信息");
            
            // 创建标题行样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            createHeaderRow(headerRow, headerStyle);
            
            // 填充数据行
            int rowNum = 1;
            for (PersonnelInfo info : personnelList) {
                Row row = sheet.createRow(rowNum++);
                populateRow(row, info);
            }
            
            // 自动调整列宽
            for (int i = 0; i < 24; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 写入文件
            try (FileOutputStream fos = new FileOutputStream(excelPath)) {
                workbook.write(fos);
            }
            
            Log.d(TAG, "成功导出 " + personnelList.size() + " 条记录到 Excel");
            return true;
            
        } catch (IOException e) {
            Log.e(TAG, "导出 Excel 失败：" + e.getMessage());
            return false;
        }
    }
    
    /**
     * 创建标题行样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        
        // 背景色（浅灰色）
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // 边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // 对齐
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * 创建数据行样式
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    /**
     * 创建标题行
     */
    private void createHeaderRow(Row row, CellStyle style) {
        String[] headers = {
            "ID", "姓名", "性别", "出生日期", "民族", "政治面貌", "学历", "专业",
            "参加工作时间", "联系电话", "人员类型", "镇名称", "部门", "职务", "职级",
            "是否驻村领导", "驻村村名", "村名", "村类型", "村职务", "多身份",
            "状态", "创建时间", "更新时间"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }
    
    /**
     * 填充数据行
     */
    private void populateRow(Row row, PersonnelInfo info) {
        CellStyle dataStyle = createDataStyle(row.getSheet().getWorkbook());
        
        row.createCell(COL_ID).setCellValue(info.getId());
        row.createCell(COL_NAME).setCellValue(info.getName());
        row.createCell(COL_GENDER).setCellValue(info.getGender());
        row.createCell(COL_BIRTH_DATE).setCellValue(info.getBirthDate());
        row.createCell(COL_ETHNICITY).setCellValue(info.getEthnicity());
        row.createCell(COL_POLITICAL_STATUS).setCellValue(info.getPoliticalStatus());
        row.createCell(COL_EDUCATION).setCellValue(info.getEducation());
        row.createCell(COL_MAJOR).setCellValue(info.getMajor());
        row.createCell(COL_WORK_START_DATE).setCellValue(info.getWorkStartDate());
        row.createCell(COL_PHONE).setCellValue(info.getPhone());
        row.createCell(COL_PERSONNEL_TYPE).setCellValue(
            info.getPersonnelType() != null ? info.getPersonnelType().getDisplayName() : ""
        );
        
        // 政府层级
        if (info.getGovernmentLevel() != null) {
            GovernmentLevel gov = info.getGovernmentLevel();
            row.createCell(COL_TOWN_NAME).setCellValue(gov.getTownName());
            row.createCell(COL_DEPARTMENT).setCellValue(gov.getDepartment());
            row.createCell(COL_POSITION).setCellValue(gov.getPosition());
            row.createCell(COL_RANK).setCellValue(gov.getRank());
            row.createCell(COL_IS_VILLAGE_LEADER).setCellValue(gov.isVillageLeader() ? "是" : "否");
            
            String[] villages = gov.getStationedVillages();
            if (villages != null && villages.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < villages.length; i++) {
                    if (i > 0) sb.append("|");
                    sb.append(villages[i]);
                }
                row.createCell(COL_STATIONED_VILLAGES).setCellValue(sb.toString());
            } else {
                row.createCell(COL_STATIONED_VILLAGES).setCellValue("");
            }
        }
        
        // 村层级
        if (info.getVillageLevel() != null) {
            VillageLevel village = info.getVillageLevel();
            row.createCell(COL_VILLAGE_NAME).setCellValue(village.getVillageName());
            row.createCell(COL_VILLAGE_TYPE).setCellValue(village.getVillageType());
            row.createCell(COL_VILLAGE_POSITION).setCellValue(village.getPosition());
        }
        
        row.createCell(COL_HAS_MULTIPLE_IDENTITIES).setCellValue(
            info.hasMultipleIdentities() ? "是" : "否"
        );
        row.createCell(COL_STATUS).setCellValue(info.getStatus());
        row.createCell(COL_CREATE_TIME).setCellValue(info.getCreateTime());
        row.createCell(COL_UPDATE_TIME).setCellValue(info.getUpdateTime());
    }
    
    /**
     * 获取单元格的字符串值
     */
    private String getCellStringValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return String.format("%1$tY-%1$tm-%1$td", date);
                }
                double num = cell.getNumericCellValue();
                if (num == (long) num) {
                    return String.valueOf((long) num);
                }
                return String.valueOf(num);
            case BOOLEAN:
                return cell.getBooleanCellValue() ? "是" : "否";
            default:
                return "";
        }
    }
}
