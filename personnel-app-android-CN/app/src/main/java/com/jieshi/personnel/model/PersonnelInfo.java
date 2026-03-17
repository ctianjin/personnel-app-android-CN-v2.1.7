package com.jieshi.personnel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 人员信息详情结构体
 * 包含基本信息、履历、奖惩、近亲属等全维度人事信息
 */
public class PersonnelInfo {
    // ==================== 基本信息 ====================
    private String id;
    private String name;
    private String gender;
    private String birthDate;
    private String ethnicity;
    private String politicalStatus;
    private String education;
    private String major;
    private String workStartDate;
    private String idCard;
    private String phone;
    private String address;
    private String nativePlace;
    private String avatarPath;
    private String comment;

    // ==================== 身份标识 ====================
    private PersonnelType personnelType;
    private boolean isTownOfficial;
    private boolean isVillageCadre;
    private boolean isGridDefender;
    private boolean hasMultipleIdentities;

    // ==================== 镇府干部信息 ====================
    private String institution;
    private int positionOrder;
    private GovernmentLevel governmentLevel;
    private String stationedVillagesStr;
    private transient List<String> stationedVillages;

    // ==================== 村两委干部信息 ====================
    private String villageCommunity;
    private VillageLevel villageLevel;

    // ==================== 关联列表 ====================
    private List<WorkExperience> workExperiences;
    private List<AwardPunishment> awardPunishments;
    private List<FamilyMember> familyMembers;

    // ==================== 系统字段 ====================
    private String createTime;
    private String updateTime;
    private String status;

    public PersonnelInfo() {
        this.id = UUID.randomUUID().toString().replace("-", "");
        this.workExperiences = new ArrayList<>();
        this.awardPunishments = new ArrayList<>();
        this.familyMembers = new ArrayList<>();
        this.status = "NORMAL";
        this.hasMultipleIdentities = false;
        this.positionOrder = 999;
    }

    public PersonnelInfo(String name) {
        this();
        this.name = name;
    }

    public PersonnelInfo(String name, PersonnelType personnelType) {
        this(name);
        this.personnelType = personnelType;
    }

    // ==================== 业务方法 ====================

    public boolean isVillageLeader() {
        return governmentLevel != null && governmentLevel.isVillageLeader();
    }

    public void parseStationedVillages() {
        if (stationedVillagesStr == null || stationedVillagesStr.trim().isEmpty()) {
            stationedVillages = new ArrayList<>();
            return;
        }
        stationedVillages = new ArrayList<>();
        String[] villages = stationedVillagesStr.split("[,，]");
        for (String village : villages) {
            String trimmed = village.trim();
            if (!trimmed.isEmpty()) {
                stationedVillages.add(trimmed);
            }
        }
    }

    public List<String> getStationedVillages() {
        if (stationedVillages == null) {
            parseStationedVillages();
        }
        return stationedVillages;
    }

    public boolean isStationedInVillage(String villageName) {
        if (stationedVillages == null) {
            parseStationedVillages();
        }
        if (stationedVillages == null || stationedVillages.isEmpty()) {
            return false;
        }
        for (String village : stationedVillages) {
            if (village != null && village.trim().equals(villageName)) {
                return true;
            }
        }
        return false;
    }

    public void checkMultipleIdentities() {
        int count = 0;
        if (isTownOfficial) count++;
        if (isVillageCadre) count++;
        if (isGridDefender) count++;
        this.hasMultipleIdentities = (count > 1);
    }

    public String toCsvRow() {
        return String.format("%s,%s,%s,%s,%s,%s\n", 
            id, name, gender, idCard, phone, 
            personnelType != null ? personnelType.getDisplayName() : "");
    }

    public String getFullPosition() {
        StringBuilder sb = new StringBuilder();
        if (institution != null && !institution.isEmpty()) {
            sb.append(institution);
        }
        if (governmentLevel != null) {
            if (sb.length() > 0) sb.append(" / ");
            sb.append(governmentLevel.getPosition());
        }
        if (villageLevel != null) {
            if (sb.length() > 0) sb.append(" / ");
            sb.append(villageLevel.getVillageName())
              .append(villageLevel.getPosition());
        }
        return sb.length() > 0 ? sb.toString() : "未分配职务";
    }

    public void addWorkExperience(WorkExperience exp) {
        if (this.workExperiences == null) this.workExperiences = new ArrayList<>();
        this.workExperiences.add(exp);
    }

    public void addAwardPunishment(AwardPunishment ap) {
        if (this.awardPunishments == null) this.awardPunishments = new ArrayList<>();
        this.awardPunishments.add(ap);
    }

    public void addFamilyMember(FamilyMember fm) {
        if (this.familyMembers == null) this.familyMembers = new ArrayList<>();
        this.familyMembers.add(fm);
    }

    // ==================== Getters and Setters ====================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getEthnicity() { return ethnicity; }
    public void setEthnicity(String ethnicity) { this.ethnicity = ethnicity; }

    public String getPoliticalStatus() { return politicalStatus; }
    public void setPoliticalStatus(String politicalStatus) { this.politicalStatus = politicalStatus; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getWorkStartDate() { return workStartDate; }
    public void setWorkStartDate(String workStartDate) { this.workStartDate = workStartDate; }

    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getNativePlace() { return nativePlace != null ? nativePlace : address; }
    public void setNativePlace(String nativePlace) { this.nativePlace = nativePlace; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public PersonnelType getPersonnelType() { return personnelType; }
    public void setPersonnelType(PersonnelType personnelType) { this.personnelType = personnelType; }

    public boolean isTownOfficial() { return isTownOfficial; }
    public void setTownOfficial(boolean townOfficial) { this.isTownOfficial = townOfficial; }

    public boolean isVillageCadre() { return isVillageCadre; }
    public void setVillageCadre(boolean villageCadre) { this.isVillageCadre = villageCadre; }

    public boolean isGridDefender() { return isGridDefender; }
    public void setGridDefender(boolean gridDefender) { this.isGridDefender = gridDefender; }

    public boolean hasMultipleIdentities() { return hasMultipleIdentities; }
    public void setHasMultipleIdentities(boolean hasMultipleIdentities) { this.hasMultipleIdentities = hasMultipleIdentities; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public int getPositionOrder() { return positionOrder; }
    public void setPositionOrder(int positionOrder) { this.positionOrder = positionOrder; }

    public GovernmentLevel getGovernmentLevel() { return governmentLevel; }
    public void setGovernmentLevel(GovernmentLevel governmentLevel) { this.governmentLevel = governmentLevel; }

    public String getStationedVillagesStr() { return stationedVillagesStr; }
    public void setStationedVillagesStr(String stationedVillagesStr) {
        this.stationedVillagesStr = stationedVillagesStr;
        this.stationedVillages = null;
    }

    public String getVillageCommunity() { return villageCommunity; }
    public void setVillageCommunity(String villageCommunity) { this.villageCommunity = villageCommunity; }

    public VillageLevel getVillageLevel() { return villageLevel; }
    public void setVillageLevel(VillageLevel villageLevel) { this.villageLevel = villageLevel; }

    public List<WorkExperience> getWorkExperiences() { return workExperiences; }
    public void setWorkExperiences(List<WorkExperience> workExperiences) { this.workExperiences = workExperiences; }

    public List<AwardPunishment> getAwardPunishments() { return awardPunishments; }
    public void setAwardPunishments(List<AwardPunishment> awardPunishments) { this.awardPunishments = awardPunishments; }

    public List<FamilyMember> getFamilyMembers() { return familyMembers; }
    public void setFamilyMembers(List<FamilyMember> familyMembers) { this.familyMembers = familyMembers; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getIdentityDescription() {
        List<String> identities = new ArrayList<>();
        if (isTownOfficial) identities.add("镇府干部");
        if (isVillageCadre) identities.add("村两委干部");
        if (isGridDefender) identities.add("网格联防员");
        return identities.isEmpty() ? "未知身份" : String.join(" + ", identities);
    }
}
