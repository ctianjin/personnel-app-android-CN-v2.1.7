package com.jieshi.personnel.manager;

import com.jieshi.personnel.model.*;
import com.jieshi.personnel.util.PinyinUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 人员数据管理类
 * 实现 CSV 加载、搜索、增删、导出功能
 * 适用于 Android 移动端离线存储
 */
public class PersonnelDataManager {
    
    /** 内存中的人员数据列表 */
    private List<PersonnelInfo> personnelList;
    /** 按姓名索引的 Map，用于快速搜索 */
    private Map<String, PersonnelInfo> nameIndex;
    /** CSV 文件路径 */
    private String csvFilePath;
    /** 日期格式化 */
    private SimpleDateFormat dateFormat;

    /**
     * 构造函数
     * @param csvFilePath CSV 文件存储路径
     */
    public PersonnelDataManager(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        this.personnelList = new ArrayList<>();
        this.nameIndex = new HashMap<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    }

    // ==================== 功能 1: 从 CSV 文件加载人员信息 ====================
    
    /**
     * 从 CSV 文件加载人员信息到内存中
     * @return 加载成功返回 true，失败返回 false
     */
    public boolean loadFromCsv() {
        return loadFromCsv(csvFilePath);
    }

    /**
     * 从指定 CSV 文件加载人员信息到内存中
     * @param filePath CSV 文件路径
     * @return 加载成功返回 true，失败返回 false
     */
    public boolean loadFromCsv(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("文件不存在：" + filePath);
            return false;
        }

        personnelList.clear();
        nameIndex.clear();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            // 读取表头
            String headerLine = reader.readLine();
            if (headerLine == null) {
                System.out.println("CSV 文件为空");
                return false;
            }
            
            // 解析表头，获取列索引
            Map<String, Integer> columnIndex = parseHeader(headerLine);
            
            // 逐行读取数据
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    PersonnelInfo info = parseCsvLine(line, columnIndex);
                    if (info != null) {
                        personnelList.add(info);
                        // 建立姓名索引（支持重名，用列表）
                        indexPersonnel(info);
                    }
                } catch (Exception e) {
                    System.out.println("第 " + lineNumber + " 行解析失败：" + e.getMessage());
                }
            }
            
            System.out.println("成功加载 " + personnelList.size() + " 条人员信息");
            return true;
            
        } catch (IOException e) {
            System.out.println("读取 CSV 文件失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 解析 CSV 表头
     * 支持中英文列名映射（保持驼峰命名）
     */
    private Map<String, Integer> parseHeader(String headerLine) {
        Map<String, Integer> columnIndex = new HashMap<>();
        String[] columns = headerLine.split(",");
        for (int i = 0; i < columns.length; i++) {
            String colName = columns[i].trim();
            // 放入原始列名（保持驼峰命名）
            columnIndex.put(colName, i);
            // 放入小写版本（兼容旧数据）
            columnIndex.put(colName.toLowerCase(), i);
            // 放入中文到英文的映射
            String englishName = chineseToEnglish(colName);
            if (englishName != null && !englishName.isEmpty()) {
                // 放入驼峰命名
                columnIndex.put(englishName, i);
                // 放入小写版本（兼容旧数据）
                columnIndex.put(englishName.toLowerCase(), i);
            }
        }
        return columnIndex;
    }

    /**
     * 中文列名转英文列名映射
     */
    private String chineseToEnglish(String chinese) {
        if (chinese == null) return null;
        switch (chinese) {
            case "姓名": return "name";
            case "性别": return "gender";
            case "出生日期": return "birthDate";
            case "民族": return "ethnicity";
            case "政治面貌": return "politicalStatus";
            case "学历": return "education";
            case "专业": return "major";
            case "参加工作时间": return "workStartDate";
            case "联系电话": return "phone";
            case "身份证号": return "idCard";
            case "现住址": return "address";
            case "籍贯": return "nativePlace";
            case "现任职务": return "currentPosition";
            case "简要评价": return "comment";
            case "是否镇府干部": return "isTownOfficial";
            case "是否村两委干部": return "isVillageCadre";
            case "是否网格联防员": return "isGridDefender";
            case "所属内设机构": return "institution";
            case "单位内职位排序": return "positionOrder";
            case "所属村（社区）": return "villageCommunity";
            case "工作履历": return "workExperiences";
            case "奖惩记录": return "awardPunishments";
            case "近亲属信息": return "familyMembers";
            case "人员类型": return "personnelType";
            case "入党时间": return "partyJoinDate";
            default: return null;
        }
    }

    /**
     * 解析 CSV 数据行
     */
    private PersonnelInfo parseCsvLine(String line, Map<String, Integer> columnIndex) {
        String[] values = parseCsvValues(line);
        
        PersonnelInfo info = new PersonnelInfo();
        
        // 基本信息（字段名与 getCsvHeader() 完全一致，驼峰命名）
        info.setId(getValue(values, columnIndex, "id"));
        info.setName(getValue(values, columnIndex, "name"));
        info.setGender(getValue(values, columnIndex, "gender"));
        info.setBirthDate(getValue(values, columnIndex, "birthDate"));
        info.setEthnicity(getValue(values, columnIndex, "ethnicity"));
        info.setPoliticalStatus(getValue(values, columnIndex, "politicalStatus"));
        info.setEducation(getValue(values, columnIndex, "education"));
        info.setMajor(getValue(values, columnIndex, "major"));
        info.setWorkStartDate(getValue(values, columnIndex, "workStartDate"));
        info.setPhone(getValue(values, columnIndex, "phone"));
        info.setNativePlace(getValue(values, columnIndex, "nativePlace"));
        info.setAddress(getValue(values, columnIndex, "address"));
        info.setCurrentPosition(getValue(values, columnIndex, "currentPosition"));
        info.setComment(getValue(values, columnIndex, "comment"));
        
        // 人员类型（设置枚举和布尔标志）
        String typeStr = getValue(values, columnIndex, "personnelType");
        if (!typeStr.isEmpty()) {
            try {
                info.setPersonnelType(PersonnelType.fromDisplayName(typeStr));
                // setPersonnelType 会自动同步 isTownOfficial/isVillageCadre/isGridDefender
            } catch (IllegalArgumentException e) {
                System.out.println("未知的人员类型：" + typeStr);
            }
        }
        
        // 如果没有设置 personnelType，检查布尔标志（兼容旧数据）
        if (info.getPersonnelType() == null) {
            if (info.isTownOfficial()) {
                info.setPersonnelType(PersonnelType.TOWN_OFFICIAL);
            } else if (info.isVillageCadre()) {
                info.setPersonnelType(PersonnelType.VILLAGE_COMMITTEE);
            } else if (info.isGridDefender()) {
                info.setPersonnelType(PersonnelType.GRID_DEFENDER);
            }
        }
        
        // 所属内设机构（镇府干部）
        info.setInstitution(getValue(values, columnIndex, "institution"));
        
        // 单位内职位排序
        String positionOrderStr = getValue(values, columnIndex, "positionOrder");
        if (!positionOrderStr.isEmpty()) {
            try {
                info.setPositionOrder(Integer.parseInt(positionOrderStr));
            } catch (NumberFormatException e) {
                info.setPositionOrder(999);
            }
        }
        
        // 所属村社区
        String villageCommunity = getValue(values, columnIndex, "villageCommunity");
        if (!villageCommunity.isEmpty()) {
            info.setVillageCommunity(villageCommunity);
        }
        
        // 解析列表类型字段（工作履历、奖惩记录、家庭成员）
        parseWorkExperiences(info, getValue(values, columnIndex, "workExperiences"));
        parseAwardPunishments(info, getValue(values, columnIndex, "awardPunishments"));
        parseFamilyMembers(info, getValue(values, columnIndex, "familyMembers"));
        
        // 系统字段
        info.setStatus(getValue(values, columnIndex, "status"));
        info.setCreateTime(getValue(values, columnIndex, "createTime"));
        info.setUpdateTime(getValue(values, columnIndex, "updateTime"));
        
        return info;
    }

    /**
     * 解析工作履历（支持多行文本）
     * 格式：每行一条记录，如 "2018.01-2020.12  XX 单位 XX 职务"
     */
    private void parseWorkExperiences(PersonnelInfo info, String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        String[] lines = text.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                WorkExperience exp = new WorkExperience();
                exp.setDescription(line);
                info.addWorkExperience(exp);
            }
        }
    }

    /**
     * 解析奖惩记录（支持多行文本）
     * 格式：每行一条记录，如 "2023 年 被评为优秀公务员"
     */
    private void parseAwardPunishments(PersonnelInfo info, String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        String[] lines = text.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                AwardPunishment ap = new AwardPunishment();
                ap.setDescription(line);
                info.addAwardPunishment(ap);
            }
        }
    }

    /**
     * 解析家庭成员及重要社会关系（支持多行文本）
     * 格式：每行一条记录，如 "父亲 张三 XX 单位退休"
     */
    private void parseFamilyMembers(PersonnelInfo info, String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        String[] lines = text.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                FamilyMember fm = new FamilyMember();
                fm.setDescription(line);
                info.addFamilyMember(fm);
            }
        }
    }

    /**
     * 解析 CSV 值（处理引号转义）
     */
    private String[] parseCsvValues(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++; // 跳过下一个引号
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        values.add(current.toString().trim());
        return values.toArray(new String[0]);
    }

    /**
     * 安全获取 CSV 值
     */
    private String getValue(String[] values, Map<String, Integer> columnIndex, String columnName) {
        Integer index = null;
        
        // 尝试多种列名格式
        String[] variants = {
            columnName,  // 原始（驼峰）
            columnName.toLowerCase(),  // 小写
            columnName.toUpperCase(),  // 大写
        };
        
        for (String variant : variants) {
            index = columnIndex.get(variant);
            if (index != null) {
                break;
            }
        }
        
        if (index != null && index < values.length) {
            String value = values[index];
            System.out.println("getValue: columnName=" + columnName + ", index=" + index + ", value=" + value);
            return value;
        }
        
        System.out.println("getValue: 未找到 columnName=" + columnName + ", columnIndex keys=" + columnIndex.keySet());
        return "";
    }

    /**
     * 建立姓名索引
     */
    private void indexPersonnel(PersonnelInfo info) {
        String name = info.getName().toLowerCase();
        if (nameIndex.containsKey(name)) {
            // 重名情况，在原有对象后追加（实际应用中可用 List 存储）
            System.out.println("发现重名：" + info.getName());
        }
        nameIndex.put(name, info);
    }

    // ==================== 功能 2: 根据姓名搜索人员 ====================
    
    /**
     * 根据人员姓名搜索详细信息
     * @param name 姓名
     * @return 找到返回 PersonnelInfo，否则返回 null
     */
    public PersonnelInfo searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        
        PersonnelInfo info = nameIndex.get(name.trim().toLowerCase());
        if (info != null) {
            System.out.println("找到人员：" + info.getName() + " - " + info.getPersonnelType().getDisplayName());
        } else {
            System.out.println("未找到人员：" + name);
        }
        return info;
    }

    /**
     * 模糊搜索（支持部分匹配）
     * @param namePart 姓名部分
     * @return 匹配的人员列表
     */
    public List<PersonnelInfo> searchByNameFuzzy(String namePart) {
        List<PersonnelInfo> results = new ArrayList<>();
        if (namePart == null || namePart.trim().isEmpty()) {
            return results;
        }
        
        String searchKey = namePart.trim().toLowerCase();
        for (PersonnelInfo info : personnelList) {
            if (info.getName().toLowerCase().contains(searchKey)) {
                results.add(info);
            }
        }
        
        System.out.println("模糊搜索 '" + namePart + "' 找到 " + results.size() + " 条结果");
        return results;
    }

    // ==================== 拼音搜索功能 ====================
    
    /**
     * 拼音搜索（支持全拼、首字母、中文混合搜索）
     * 例如：输入 "zhang"、"zs"、"张" 都能搜索到 "张三"
     * 
     * @param query 查询词（中文、拼音或首字母）
     * @return 匹配的人员列表
     */
    public List<PersonnelInfo> searchByPinyin(String query) {
        List<PersonnelInfo> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }
        
        String searchQuery = query.trim();
        for (PersonnelInfo info : personnelList) {
            String name = info.getName();
            if (PinyinUtil.matches(name, searchQuery)) {
                results.add(info);
            }
        }
        
        System.out.println("拼音搜索 '" + query + "' 找到 " + results.size() + " 条结果");
        return results;
    }
    
    /**
     * 首字母搜索（快速搜索）
     * 例如：输入 "zs" 搜索 "张三"，输入 "ls" 搜索 "李四"
     * 
     * @param initials 首字母（不区分大小写）
     * @return 匹配的人员列表
     */
    public List<PersonnelInfo> searchByInitials(String initials) {
        List<PersonnelInfo> results = new ArrayList<>();
        if (initials == null || initials.trim().isEmpty()) {
            return results;
        }
        
        String searchKey = initials.trim().toLowerCase();
        for (PersonnelInfo info : personnelList) {
            String nameInitials = PinyinUtil.toInitialsLower(info.getName());
            if (nameInitials.startsWith(searchKey)) {
                results.add(info);
            }
        }
        
        System.out.println("首字母搜索 '" + initials + "' 找到 " + results.size() + " 条结果");
        return results;
    }
    
    /**
     * 高级搜索（支持多字段组合搜索）
     * 
     * @param query 查询词
     * @param searchName 是否搜索姓名
     * @param searchPinyin 是否搜索拼音
     * @param searchPosition 是否搜索职务
     * @return 匹配的人员列表
     */
    public List<PersonnelInfo> advancedSearch(String query, boolean searchName, 
                                               boolean searchPinyin, boolean searchPosition) {
        List<PersonnelInfo> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }
        
        String searchQuery = query.trim().toLowerCase();
        
        for (PersonnelInfo info : personnelList) {
            boolean matched = false;
            
            // 姓名搜索
            if (searchName && info.getName().toLowerCase().contains(searchQuery)) {
                matched = true;
            }
            
            // 拼音搜索
            if (!matched && searchPinyin && PinyinUtil.matches(info.getName(), searchQuery)) {
                matched = true;
            }
            
            // 职务搜索
            if (!matched && searchPosition) {
                String position = info.getFullPosition().toLowerCase();
                if (position.contains(searchQuery)) {
                    matched = true;
                }
            }
            
            if (matched) {
                results.add(info);
            }
        }
        
        System.out.println("高级搜索 '" + query + "' 找到 " + results.size() + " 条结果");
        return results;
    }

    /**
     * 按人员类型筛选
     * @param type 人员类型
     * @return 筛选结果列表
     */
    public List<PersonnelInfo> filterByType(PersonnelType type) {
        List<PersonnelInfo> results = new ArrayList<>();
        for (PersonnelInfo info : personnelList) {
            if (info.getPersonnelType() == type) {
                results.add(info);
            }
        }
        return results;
    }

    // ==================== 功能 3: 新增/删除人员 ====================
    
    /**
     * 新增人员
     * @param info 人员信息结构体
     * @return 成功返回 true
     */
    public boolean addPersonnel(PersonnelInfo info) {
        if (info == null || info.getName() == null) {
            System.out.println("人员信息不能为空");
            return false;
        }
        
        // 设置时间戳
        String now = dateFormat.format(new Date());
        info.setCreateTime(now);
        info.setUpdateTime(now);
        info.setStatus("NORMAL");
        
        // 生成 ID（如果没有）
        if (info.getId() == null || info.getId().isEmpty()) {
            info.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        
        // 添加到内存
        personnelList.add(info);
        indexPersonnel(info);
        
        // 保存到 CSV
        return saveToCsv();
    }

    /**
     * 删除人员（按姓名）
     * @param name 姓名
     * @return 成功返回 true
     */
    public boolean removePersonnelByName(String name) {
        PersonnelInfo info = searchByName(name);
        if (info == null) {
            System.out.println("未找到要删除的人员：" + name);
            return false;
        }
        
        return removePersonnel(info);
    }

    /**
     * 删除人员（按人员信息结构体）
     * @param info 人员信息结构体
     * @return 成功返回 true
     */
    public boolean removePersonnel(PersonnelInfo info) {
        if (info == null) {
            return false;
        }
        
        // 从内存移除
        boolean removed = personnelList.remove(info);
        if (removed) {
            nameIndex.remove(info.getName().toLowerCase());
            info.setStatus("DELETED");
            System.out.println("成功删除人员：" + info.getName());
        }
        
        // 保存到 CSV
        return saveToCsv();
    }

    /**
     * 更新人员信息
     * @param info 更新后的人员信息结构体
     * @return 成功返回 true
     */
    public boolean updatePersonnel(PersonnelInfo info) {
        if (info == null || info.getId() == null) {
            return false;
        }
        
        // 查找原记录
        PersonnelInfo existing = null;
        for (PersonnelInfo p : personnelList) {
            if (p.getId().equals(info.getId())) {
                existing = p;
                break;
            }
        }
        
        if (existing == null) {
            System.out.println("未找到要更新的人员 ID: " + info.getId());
            return false;
        }
        
        // 更新信息
        info.setUpdateTime(dateFormat.format(new Date()));
        
        // 从索引移除旧记录
        nameIndex.remove(existing.getName().toLowerCase());
        
        // 替换记录
        int index = personnelList.indexOf(existing);
        personnelList.set(index, info);
        
        // 重新建立索引
        indexPersonnel(info);
        
        System.out.println("成功更新人员：" + info.getName());
        return saveToCsv();
    }

    // ==================== 功能 4: 导出全部人员信息到 CSV ====================
    
    /**
     * 导出全部人员信息到 CSV 文件
     * @return 成功返回 true
     */
    public boolean exportToCsv() {
        return saveToCsv();
    }

    /**
     * 导出全部人员信息到指定 CSV 文件
     * @param filePath 文件路径
     * @return 成功返回 true
     */
    public boolean exportToCsv(String filePath) {
        String originalPath = csvFilePath;
        csvFilePath = filePath;
        boolean result = saveToCsv();
        csvFilePath = originalPath;
        return result;
    }

    /**
     * 保存数据到 CSV 文件
     */
    private boolean saveToCsv() {
        File file = new File(csvFilePath);
        
        // 确保父目录存在
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            
            // 写入表头
            writer.write(getCsvHeader());
            writer.newLine();
            
            // 写入数据行
            for (PersonnelInfo info : personnelList) {
                if ("NORMAL".equals(info.getStatus()) || "UPDATED".equals(info.getStatus())) {
                    writer.write(info.toCsvRow());
                    writer.newLine();
                }
            }
            
            System.out.println("成功保存 " + personnelList.size() + " 条记录到：" + csvFilePath);
            return true;
            
        } catch (IOException e) {
            System.out.println("保存 CSV 文件失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 获取 CSV 表头
     */
    private String getCsvHeader() {
        return "id,name,gender,birthDate,ethnicity,politicalStatus,education,major,workStartDate,phone," +
               "nativePlace,address,currentPosition,comment," +
               "personnelType,institution,positionOrder,villageCommunity," +
               "workExperiences,awardPunishments,familyMembers," +
               "status,createTime,updateTime";
    }

    // ==================== 辅助方法 ====================
    
    /**
     * 获取所有人员列表
     */
    public List<PersonnelInfo> getAllPersonnel() {
        return new ArrayList<>(personnelList);
    }

    /**
     * 获取人员总数
     */
    public int getCount() {
        return personnelList.size();
    }

    /**
     * 按人员类型分组统计
     */
    public Map<PersonnelType, Integer> getCountByType() {
        Map<PersonnelType, Integer> stats = new HashMap<>();
        for (PersonnelInfo info : personnelList) {
            PersonnelType type = info.getPersonnelType();
            if (type != null) {
                stats.put(type, stats.getOrDefault(type, 0) + 1);
            }
        }
        return stats;
    }

    /**
     * 获取所有驻村领导（自动排序到村两委列表首位）
     */
    public List<PersonnelInfo> getVillageLeaders() {
        List<PersonnelInfo> leaders = new ArrayList<>();
        for (PersonnelInfo info : personnelList) {
            if (info.isVillageLeader()) {
                leaders.add(info);
            }
        }
        return leaders;
    }

    /**
     * 清空内存数据
     */
    public void clear() {
        personnelList.clear();
        nameIndex.clear();
    }
}
