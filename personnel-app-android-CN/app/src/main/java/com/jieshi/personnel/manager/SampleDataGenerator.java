package com.jieshi.personnel.manager;

import android.content.Context;

import com.jieshi.personnel.model.AwardPunishment;
import com.jieshi.personnel.model.FamilyMember;
import com.jieshi.personnel.model.GovernmentLevel;
import com.jieshi.personnel.model.PersonnelInfo;
import com.jieshi.personnel.model.VillageLevel;
import com.jieshi.personnel.model.WorkExperience;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 模拟数据生成器
 * 
 * 根据需求表格生成测试数据
 */
public class SampleDataGenerator {
    
    /**
     * 生成模拟数据
     */
    public static List<PersonnelInfo> generateSampleData() {
        List<PersonnelInfo> list = new ArrayList<>();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
        
        // ===== 示例 1: 朱昌立 - 镇党政领导班子 + 包二村村两委（多身份） =====
        PersonnelInfo zhu = new PersonnelInfo();
        zhu.setId("uuid_001");
        zhu.setName("朱昌立");
        zhu.setGender("男");
        zhu.setBirthDate("1970-01");
        zhu.setEthnicity("汉族");
        zhu.setPoliticalStatus("中共党员");
        zhu.setEducation("本科");
        zhu.setMajor("行政管理");
        zhu.setWorkStartDate("1990-07");
        zhu.setPhone("13800138001");
        zhu.setIdCard("441581197001010011");
        zhu.setAddress("陆丰碣石");
        zhu.setComment("政治立场坚定，工作思路清晰，勇于担当");
        
        // 多身份
        zhu.setTownOfficial(true);
        zhu.setVillageCadre(true);
        zhu.setGridDefender(false);
        zhu.checkMultipleIdentities();
        
        // 镇府干部信息
        zhu.setInstitution("镇党政领导班子");
        zhu.setPositionOrder(1);
        
        GovernmentLevel gov1 = new GovernmentLevel();
        gov1.setTownName("碣石镇");
        gov1.setDepartment("镇党政领导班子");
        gov1.setPosition("镇党委书记");
        gov1.setRank("正科级");
        gov1.setVillageLeader(true);
        gov1.setStationedVillages(new String[]{"包二村"});
        zhu.setGovernmentLevel(gov1);
        
        // 驻村信息
        zhu.setStationedVillagesStr("包二村");
        
        // 村两委信息
        zhu.setVillageCommunity("包二村");
        VillageLevel village1 = new VillageLevel();
        village1.setVillageName("包二村");
        village1.setVillageType("村");
        village1.setPosition("第一书记");
        village1.setSecretary(true);
        zhu.setVillageLevel(village1);
        
        // 履历
        WorkExperience exp1 = new WorkExperience();
        exp1.setDescription("1990.07--2005.05 在碣石镇党政办工作");
        zhu.addWorkExperience(exp1);
        WorkExperience exp2 = new WorkExperience();
        exp2.setDescription("2005.06--2015.03 碣石镇副镇长");
        zhu.addWorkExperience(exp2);
        WorkExperience exp3 = new WorkExperience();
        exp3.setDescription("2015.04--至今 碣石镇党委书记");
        zhu.addWorkExperience(exp3);
        
        // 奖惩
        AwardPunishment award1 = new AwardPunishment();
        award1.setDescription("2018 年 获汕尾市优秀党务工作者");
        zhu.addAwardPunishment(award1);
        AwardPunishment award2 = new AwardPunishment();
        award2.setDescription("2020 年 获广东省脱贫攻坚先进个人");
        zhu.addAwardPunishment(award2);
        
        // 家庭
        FamilyMember fm1 = new FamilyMember();
        fm1.setName("妻子：林 XX，1972.05，碣石镇中心小学，教师");
        zhu.addFamilyMember(fm1);
        FamilyMember fm2 = new FamilyMember();
        fm2.setName("儿子：朱 XX，1995.08，广州 XX 公司，职员");
        zhu.addFamilyMember(fm2);
        
        zhu.setCreateTime(now);
        zhu.setUpdateTime(now);
        zhu.setStatus("NORMAL");
        list.add(zhu);
        
        // ===== 示例 2: 陈志强 - 镇长，挂驻 3 个村（多村挂驻） =====
        PersonnelInfo chen = new PersonnelInfo();
        chen.setId("uuid_002");
        chen.setName("陈志强");
        chen.setGender("男");
        chen.setBirthDate("1972-02");
        chen.setEthnicity("汉族");
        chen.setPoliticalStatus("中共党员");
        chen.setEducation("硕士研究生");
        chen.setMajor("公共管理");
        chen.setWorkStartDate("1992-07");
        chen.setPhone("13800138002");
        chen.setIdCard("441581197202150012");
        chen.setAddress("汕尾城区");
        chen.setComment("工作务实，善于协调，群众基础好");
        
        chen.setTownOfficial(true);
        chen.setVillageCadre(true);
        chen.setGridDefender(false);
        chen.checkMultipleIdentities();
        
        chen.setInstitution("镇党政领导班子");
        chen.setPositionOrder(2);
        
        GovernmentLevel gov2 = new GovernmentLevel();
        gov2.setTownName("碣石镇");
        gov2.setDepartment("镇党政领导班子");
        gov2.setPosition("镇长");
        gov2.setRank("正科级");
        gov2.setVillageLeader(true);
        gov2.setStationedVillages(new String[]{"包一村", "曾厝村", "戴厝村"});
        chen.setGovernmentLevel(gov2);
        
        // 多村挂驻
        chen.setStationedVillagesStr("包一村，曾厝村，戴厝村");
        
        chen.setVillageCommunity("包一村");
        VillageLevel village2 = new VillageLevel();
        village2.setVillageName("包一村");
        village2.setVillageType("村");
        village2.setPosition("驻村领导");
        village2.setStationedLeader(true);
        chen.setVillageLevel(village2);
        
        WorkExperience exp4 = new WorkExperience();
        exp4.setDescription("1992.07--2008.05 碣石镇经济发展办");
        chen.addWorkExperience(exp4);
        WorkExperience exp5 = new WorkExperience();
        exp5.setDescription("2008.06--2018.03 碣石镇副镇长");
        chen.addWorkExperience(exp5);
        WorkExperience exp6 = new WorkExperience();
        exp6.setDescription("2018.04--至今 碣石镇镇长");
        chen.addWorkExperience(exp6);
        
        AwardPunishment award3 = new AwardPunishment();
        award3.setDescription("2019 年 获汕尾市优秀公务员");
        chen.addAwardPunishment(award3);
        
        FamilyMember fm3 = new FamilyMember();
        fm3.setName("妻子：陈 XX，1975.03，汕尾市 XX 局，科员");
        chen.addFamilyMember(fm3);
        
        chen.setCreateTime(now);
        chen.setUpdateTime(now);
        chen.setStatus("NORMAL");
        list.add(chen);
        
        // ===== 示例 3: 李秀英 - 党建办主任（单一镇府干部） =====
        PersonnelInfo li = new PersonnelInfo();
        li.setId("uuid_003");
        li.setName("李秀英");
        li.setGender("女");
        li.setBirthDate("1980-03");
        li.setEthnicity("汉族");
        li.setPoliticalStatus("中共党员");
        li.setEducation("本科");
        li.setMajor("行政管理");
        li.setWorkStartDate("2002-07");
        li.setPhone("13800138003");
        li.setIdCard("441581198003200023");
        li.setAddress("陆丰碣石");
        li.setComment("工作认真负责，业务能力强");
        
        li.setTownOfficial(true);
        li.setVillageCadre(false);
        li.setGridDefender(false);
        li.checkMultipleIdentities();
        
        li.setInstitution("党建和组织人事办公室");
        li.setPositionOrder(1);
        
        GovernmentLevel gov3 = new GovernmentLevel();
        gov3.setTownName("碣石镇");
        gov3.setDepartment("党建和组织人事办公室");
        gov3.setPosition("主任");
        gov3.setRank("正股级");
        li.setGovernmentLevel(gov3);
        
        li.setCreateTime(now);
        li.setUpdateTime(now);
        li.setStatus("NORMAL");
        list.add(li);
        
        // ===== 示例 4: 黄建华 - 包一村党支部书记（单一村两委） =====
        PersonnelInfo huang = new PersonnelInfo();
        huang.setId("uuid_004");
        huang.setName("黄建华");
        huang.setGender("男");
        huang.setBirthDate("1975-04");
        huang.setEthnicity("汉族");
        huang.setPoliticalStatus("中共党员");
        huang.setEducation("大专");
        huang.setMajor("农村管理");
        huang.setWorkStartDate("1995-07");
        huang.setPhone("13800138004");
        huang.setIdCard("441581197504100034");
        huang.setAddress("陆丰碣石");
        huang.setComment("扎根基层，服务群众，威信高");
        
        huang.setTownOfficial(false);
        huang.setVillageCadre(true);
        huang.setGridDefender(false);
        huang.checkMultipleIdentities();
        
        huang.setVillageCommunity("包一村");
        huang.setPositionOrder(2);
        
        VillageLevel village3 = new VillageLevel();
        village3.setVillageName("包一村");
        village3.setVillageType("村");
        village3.setPosition("党支部书记");
        village3.setSecretary(true);
        huang.setVillageLevel(village3);
        
        WorkExperience exp7 = new WorkExperience();
        exp7.setDescription("1995.07--2010.05 包一村村委会主任");
        huang.addWorkExperience(exp7);
        WorkExperience exp8 = new WorkExperience();
        exp8.setDescription("2010.06--至今 包一村党支部书记");
        huang.addWorkExperience(exp8);
        
        AwardPunishment award4 = new AwardPunishment();
        award4.setDescription("2021 年 获陆丰市优秀党务工作者");
        huang.addAwardPunishment(award4);
        
        FamilyMember fm4 = new FamilyMember();
        fm4.setName("妻子：黄 XX，1978.02，务农");
        huang.addFamilyMember(fm4);
        
        huang.setCreateTime(now);
        huang.setUpdateTime(now);
        huang.setStatus("NORMAL");
        list.add(huang);
        
        // ===== 示例 5: 吴志强 - 网格联防员 =====
        PersonnelInfo wu = new PersonnelInfo();
        wu.setId("uuid_005");
        wu.setName("吴志强");
        wu.setGender("男");
        wu.setBirthDate("1985-05");
        wu.setEthnicity("汉族");
        wu.setPoliticalStatus("群众");
        wu.setEducation("高中");
        wu.setWorkStartDate("2005-07");
        wu.setPhone("13800138005");
        wu.setIdCard("441581198505150045");
        wu.setAddress("陆丰碣石");
        
        wu.setTownOfficial(false);
        wu.setVillageCadre(false);
        wu.setGridDefender(true);
        wu.checkMultipleIdentities();
        
        wu.setVillageCommunity("包一村");
        wu.setPositionOrder(1);
        
        VillageLevel village4 = new VillageLevel();
        village4.setVillageName("包一村");
        village4.setVillageType("村");
        village4.setPosition("网格联防员");
        wu.setVillageLevel(village4);
        
        wu.setCreateTime(now);
        wu.setUpdateTime(now);
        wu.setStatus("NORMAL");
        list.add(wu);
        
        // ===== 示例 6: 林志明 - 镇党委副书记，挂驻 3 个村 =====
        PersonnelInfo lin = new PersonnelInfo();
        lin.setId("uuid_006");
        lin.setName("林志明");
        lin.setGender("男");
        lin.setBirthDate("1973-06");
        lin.setEthnicity("汉族");
        lin.setPoliticalStatus("中共党员");
        lin.setEducation("本科");
        lin.setMajor("行政管理");
        lin.setWorkStartDate("1993-07");
        lin.setPhone("13800138006");
        lin.setIdCard("441581197306200056");
        lin.setAddress("汕尾城区");
        lin.setComment("政治素质好，组织协调能力强");
        
        lin.setTownOfficial(true);
        lin.setVillageCadre(true);
        lin.setGridDefender(false);
        lin.checkMultipleIdentities();
        
        lin.setInstitution("镇党政领导班子");
        lin.setPositionOrder(3);
        
        GovernmentLevel gov4 = new GovernmentLevel();
        gov4.setTownName("碣石镇");
        gov4.setDepartment("镇党政领导班子");
        gov4.setPosition("镇党委副书记");
        gov4.setRank("副科级");
        gov4.setVillageLeader(true);
        gov4.setStationedVillages(new String[]{"曾厝村", "戴厝村", "港口村"});
        lin.setGovernmentLevel(gov4);
        
        lin.setStationedVillagesStr("曾厝村，戴厝村，港口村");
        
        VillageLevel village5 = new VillageLevel();
        village5.setVillageName("曾厝村");
        village5.setVillageType("村");
        village5.setPosition("驻村领导");
        village5.setStationedLeader(true);
        lin.setVillageLevel(village5);
        
        WorkExperience exp9 = new WorkExperience();
        exp9.setDescription("1993.07--2010.05 碣石镇党政办");
        lin.addWorkExperience(exp9);
        WorkExperience exp10 = new WorkExperience();
        exp10.setDescription("2010.06--2018.03 碣石镇党委委员");
        lin.addWorkExperience(exp10);
        WorkExperience exp11 = new WorkExperience();
        exp11.setDescription("2018.04--至今 碣石镇党委副书记");
        lin.addWorkExperience(exp11);
        
        AwardPunishment award5 = new AwardPunishment();
        award5.setDescription("2020 年 获汕尾市优秀党务工作者");
        lin.addAwardPunishment(award5);
        
        FamilyMember fm5 = new FamilyMember();
        fm5.setName("妻子：林 XX，1976.04，汕尾市 XX 医院，护士");
        lin.addFamilyMember(fm5);
        
        lin.setCreateTime(now);
        lin.setUpdateTime(now);
        lin.setStatus("NORMAL");
        list.add(lin);
        
        return list;
    }
    
    /**
     * 导出模拟数据到 CSV
     */
    public static void exportToCSV(Context context) {
        List<PersonnelInfo> list = generateSampleData();
        
        String csvPath = new File(context.getFilesDir(), "personnel.csv").getAbsolutePath();
        PersonnelDataManager manager = new PersonnelDataManager(csvPath);
        
        for (PersonnelInfo info : list) {
            manager.addPersonnel(info);
        }
    }
}
