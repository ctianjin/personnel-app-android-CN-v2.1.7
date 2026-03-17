package com.jieshi.personnel;

import com.jieshi.personnel.manager.PersonnelDataManager;
import com.jieshi.personnel.model.*;

import java.util.List;

/**
 * 人事信息管理系统 - 功能测试类
 * 演示所有核心功能的使用
 */
public class PersonnelAppTest {
    
    private static final String CSV_PATH = "data/sample_data.csv";
    private static final String EXPORT_PATH = "data/export_data.csv";

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    碣石镇人事信息管理系统 - 功能测试");
        System.out.println("========================================\n");
        
        PersonnelAppTest test = new PersonnelAppTest();
        
        // 测试 1: 从 CSV 加载数据
        test.testLoadFromCsv();
        
        // 测试 2: 按姓名搜索
        test.testSearchByName();
        
        // 测试 3: 新增人员
        test.testAddPersonnel();
        
        // 测试 4: 删除人员
        test.testRemovePersonnel();
        
        // 测试 5: 导出全部数据
        test.testExportToCsv();
        
        System.out.println("\n========================================");
        System.out.println("    所有测试完成!");
        System.out.println("========================================");
    }

    /**
     * 测试功能 1: 从 CSV 文件加载人员信息
     */
    public void testLoadFromCsv() {
        System.out.println("【测试 1】从 CSV 文件加载人员信息");
        System.out.println("----------------------------------------");
        
        PersonnelDataManager manager = new PersonnelDataManager(CSV_PATH);
        boolean success = manager.loadFromCsv();
        
        if (success) {
            System.out.println("✓ 加载成功!");
            System.out.println("  总人数：" + manager.getCount());
            
            // 显示类型统计
            System.out.println("\n  按类型统计:");
            manager.getCountByType().forEach((type, count) -> 
                System.out.println("    " + type.getDisplayName() + ": " + count + "人")
            );
        } else {
            System.out.println("✗ 加载失败!");
        }
        System.out.println();
    }

    /**
     * 测试功能 2: 根据姓名搜索人员
     */
    public void testSearchByName() {
        System.out.println("【测试 2】根据姓名搜索人员详细信息");
        System.out.println("----------------------------------------");
        
        PersonnelDataManager manager = new PersonnelDataManager(CSV_PATH);
        manager.loadFromCsv();
        
        // 精确搜索
        System.out.println("精确搜索 '张三':");
        PersonnelInfo info = manager.searchByName("张三");
        if (info != null) {
            printPersonnelDetail(info);
        }
        
        // 模糊搜索
        System.out.println("\n模糊搜索 '张':");
        List<PersonnelInfo> results = manager.searchByNameFuzzy("张");
        System.out.println("  找到 " + results.size() + " 条结果:");
        for (PersonnelInfo p : results) {
            System.out.println("    - " + p.getName() + " (" + p.getPersonnelType().getDisplayName() + ")");
        }
        System.out.println();
    }

    /**
     * 测试功能 3: 新增人员
     */
    public void testAddPersonnel() {
        System.out.println("【测试 3】新增人员");
        System.out.println("----------------------------------------");
        
        PersonnelDataManager manager = new PersonnelDataManager(CSV_PATH);
        manager.loadFromCsv();
        
        // 创建新人员
        PersonnelInfo newPerson = new PersonnelInfo("钱九", PersonnelType.GRID_DEFENDER);
        newPerson.setGender("男");
        newPerson.setBirthDate("1995-08-10");
        newPerson.setEthnicity("汉族");
        newPerson.setPoliticalStatus("共青团员");
        newPerson.setEducation("大专");
        newPerson.setPhone("13800138009");
        
        // 设置村层级信息
        VillageLevel villageLevel = new VillageLevel();
        villageLevel.setVillageName("新港村");
        villageLevel.setVillageType("村");
        villageLevel.setPosition("网格员");
        newPerson.setVillageLevel(villageLevel);
        
        // 添加履历
        WorkExperience exp = new WorkExperience();
        exp.setStartDate("2018-01-01");
        exp.setEndDate("至今");
        exp.setCompany("碣石镇新港村");
        exp.setPosition("网格员");
        newPerson.addWorkExperience(exp);
        
        // 添加家庭成员
        FamilyMember fm = new FamilyMember();
        fm.setName("钱妈妈");
        fm.setRelationship("母亲");
        fm.setCompany("务农");
        newPerson.addFamilyMember(fm);
        
        // 新增
        boolean success = manager.addPersonnel(newPerson);
        if (success) {
            System.out.println("✓ 新增成功!");
            System.out.println("  新增人员：" + newPerson.getName());
            System.out.println("  类型：" + newPerson.getPersonnelType().getDisplayName());
            System.out.println("  总人数：" + manager.getCount());
        } else {
            System.out.println("✗ 新增失败!");
        }
        System.out.println();
    }

    /**
     * 测试功能 4: 删除人员
     */
    public void testRemovePersonnel() {
        System.out.println("【测试 4】删除人员");
        System.out.println("----------------------------------------");
        
        PersonnelDataManager manager = new PersonnelDataManager(CSV_PATH);
        manager.loadFromCsv();
        
        int beforeCount = manager.getCount();
        System.out.println("删除前总人数：" + beforeCount);
        
        // 删除人员（按姓名）
        boolean success = manager.removePersonnelByName("钱九");
        if (success) {
            System.out.println("✓ 删除成功!");
            System.out.println("删除后总人数：" + manager.getCount());
        } else {
            System.out.println("✗ 删除失败!");
        }
        System.out.println();
    }

    /**
     * 测试功能 5: 导出全部人员信息
     */
    public void testExportToCsv() {
        System.out.println("【测试 5】导出全部人员信息到 CSV");
        System.out.println("----------------------------------------");
        
        PersonnelDataManager manager = new PersonnelDataManager(CSV_PATH);
        manager.loadFromCsv();
        
        boolean success = manager.exportToCsv(EXPORT_PATH);
        if (success) {
            System.out.println("✓ 导出成功!");
            System.out.println("  导出路径：" + EXPORT_PATH);
            System.out.println("  导出记录数：" + manager.getCount());
        } else {
            System.out.println("✗ 导出失败!");
        }
        System.out.println();
    }

    /**
     * 打印人员详细信息
     */
    private void printPersonnelDetail(PersonnelInfo info) {
        System.out.println("\n  === 人员详细信息 ===");
        System.out.println("  姓名：" + info.getName());
        System.out.println("  性别：" + info.getGender());
        System.out.println("  出生日期：" + info.getBirthDate());
        System.out.println("  民族：" + info.getEthnicity());
        System.out.println("  政治面貌：" + info.getPoliticalStatus());
        System.out.println("  学历：" + info.getEducation());
        System.out.println("  专业：" + info.getMajor());
        System.out.println("  联系电话：" + info.getPhone());
        System.out.println("  人员类型：" + info.getPersonnelType().getDisplayName());
        
        if (info.getGovernmentLevel() != null) {
            GovernmentLevel gov = info.getGovernmentLevel();
            System.out.println("  政府职务：" + gov.getTownName() + gov.getDepartment() + gov.getPosition());
            System.out.println("  职级：" + gov.getRank());
            System.out.println("  是否驻村领导：" + (gov.isVillageLeader() ? "是" : "否"));
            if (gov.getStationedVillages().length > 0) {
                System.out.println("  驻村：" + String.join(", ", gov.getStationedVillages()));
            }
        }
        
        if (info.getVillageLevel() != null) {
            VillageLevel village = info.getVillageLevel();
            System.out.println("  村职务：" + village.getVillageName() + village.getPosition());
        }
        
        System.out.println("  履历数量：" + info.getWorkExperiences().size());
        System.out.println("  奖惩记录：" + info.getAwardPunishments().size());
        System.out.println("  家庭成员：" + info.getFamilyMembers().size());
        System.out.println("  ====================\n");
    }
}
