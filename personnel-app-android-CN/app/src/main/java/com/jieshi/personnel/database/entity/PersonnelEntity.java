package com.jieshi.personnel.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.jieshi.personnel.model.GovernmentLevel;
import com.jieshi.personnel.model.PersonnelInfo;
import com.jieshi.personnel.model.PersonnelType;
import com.jieshi.personnel.model.VillageLevel;

/**
 * 人员信息实体类（Room Database）
 * 
 * @author 秋天 · 严谨专业版
 * @version 1.0.0
 * @since 2026-03-15
 */
@Entity(tableName = "personnel")
public class PersonnelEntity {
    
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id = "";
    
    @ColumnInfo(name = "name")
    private String name;
    
    @ColumnInfo(name = "gender")
    private String gender;
    
    @ColumnInfo(name = "birth_date")
    private String birthDate;
    
    @ColumnInfo(name = "ethnicity")
    private String ethnicity;
    
    @ColumnInfo(name = "political_status")
    private String politicalStatus;
    
    @ColumnInfo(name = "education")
    private String education;
    
    @ColumnInfo(name = "major")
    private String major;
    
    @ColumnInfo(name = "work_start_date")
    private String workStartDate;
    
    @ColumnInfo(name = "phone")
    private String phone;
    
    @ColumnInfo(name = "id_card")
    private String idCard;
    
    @ColumnInfo(name = "native_place")
    private String nativePlace;
    
    @ColumnInfo(name = "address")
    private String address;
    
    @ColumnInfo(name = "personnel_type")
    private String personnelType;
    
    @ColumnInfo(name = "town_name")
    private String townName;
    
    @ColumnInfo(name = "department")
    private String department;
    
    @ColumnInfo(name = "position")
    private String position;
    
    @ColumnInfo(name = "rank")
    private String rank;
    
    @ColumnInfo(name = "is_village_leader")
    private boolean isVillageLeader;
    
    @ColumnInfo(name = "village_name")
    private String villageName;
    
    @ColumnInfo(name = "village_type")
    private String villageType;
    
    @ColumnInfo(name = "village_position")
    private String villagePosition;
    
    @ColumnInfo(name = "has_multiple_identities")
    private boolean hasMultipleIdentities;
    
    @ColumnInfo(name = "status")
    private String status;
    
    @ColumnInfo(name = "create_time")
    private String createTime;
    
    @ColumnInfo(name = "update_time")
    private String updateTime;
    
    // 默认构造函数（Room 需要）
    public PersonnelEntity() {
    }
    
    /**
     * 从 PersonnelInfo 转换
     */
    @Ignore
    public PersonnelEntity(PersonnelInfo info) {
        this.id = info.getId() != null ? info.getId() : "";
        this.name = info.getName();
        this.gender = info.getGender();
        this.birthDate = info.getBirthDate();
        this.ethnicity = info.getEthnicity();
        this.politicalStatus = info.getPoliticalStatus();
        this.education = info.getEducation();
        this.major = info.getMajor();
        this.workStartDate = info.getWorkStartDate();
        this.phone = info.getPhone();
        this.idCard = info.getIdCard();
        this.nativePlace = info.getNativePlace();
        this.address = info.getAddress();
        
        if (info.getPersonnelType() != null) {
            this.personnelType = info.getPersonnelType().name();
        }
        
        if (info.getGovernmentLevel() != null) {
            this.townName = info.getGovernmentLevel().getTownName();
            this.department = info.getGovernmentLevel().getDepartment();
            this.position = info.getGovernmentLevel().getPosition();
            this.rank = info.getGovernmentLevel().getRank();
            this.isVillageLeader = info.getGovernmentLevel().isVillageLeader();
        }
        
        if (info.getVillageLevel() != null) {
            this.villageName = info.getVillageLevel().getVillageName();
            this.villageType = info.getVillageLevel().getVillageType();
            this.villagePosition = info.getVillageLevel().getPosition();
        }
        
        this.hasMultipleIdentities = info.hasMultipleIdentities();
        this.status = info.getStatus();
        this.createTime = info.getCreateTime();
        this.updateTime = info.getUpdateTime();
    }
    
    /**
     * 转换为 PersonnelInfo
     */
    @Ignore
    public PersonnelInfo toPersonnelInfo() {
        PersonnelInfo info = new PersonnelInfo();
        
        info.setId(this.id);
        info.setName(this.name);
        info.setGender(this.gender);
        info.setBirthDate(this.birthDate);
        info.setEthnicity(this.ethnicity);
        info.setPoliticalStatus(this.politicalStatus);
        info.setEducation(this.education);
        info.setMajor(this.major);
        info.setWorkStartDate(this.workStartDate);
        info.setPhone(this.phone);
        info.setIdCard(this.idCard);
        info.setNativePlace(this.nativePlace);
        info.setAddress(this.address);
        
        if (this.personnelType != null) {
            try {
                info.setPersonnelType(PersonnelType.valueOf(this.personnelType));
            } catch (IllegalArgumentException e) {
                info.setPersonnelType(PersonnelType.TOWN_OFFICIAL);
            }
        }
        
        if (this.townName != null && !this.townName.isEmpty()) {
            GovernmentLevel govLevel = new GovernmentLevel();
            govLevel.setTownName(this.townName);
            govLevel.setDepartment(this.department);
            govLevel.setPosition(this.position);
            govLevel.setRank(this.rank);
            govLevel.setVillageLeader(this.isVillageLeader);
            info.setGovernmentLevel(govLevel);
        }
        
        if (this.villageName != null && !this.villageName.isEmpty()) {
            VillageLevel villageLevel = new VillageLevel();
            villageLevel.setVillageName(this.villageName);
            villageLevel.setVillageType(this.villageType);
            villageLevel.setPosition(this.villagePosition);
            info.setVillageLevel(villageLevel);
        }
        
        info.setHasMultipleIdentities(this.hasMultipleIdentities);
        info.setStatus(this.status);
        info.setCreateTime(this.createTime);
        info.setUpdateTime(this.updateTime);
        
        return info;
    }
    
    // ==================== Getters and Setters ====================
    
    @NonNull
    public String getId() {
        return id;
    }
    
    public void setId(@NonNull String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    
    public String getEthnicity() {
        return ethnicity;
    }
    
    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }
    
    public String getPoliticalStatus() {
        return politicalStatus;
    }
    
    public void setPoliticalStatus(String politicalStatus) {
        this.politicalStatus = politicalStatus;
    }
    
    public String getEducation() {
        return education;
    }
    
    public void setEducation(String education) {
        this.education = education;
    }
    
    public String getMajor() {
        return major;
    }
    
    public void setMajor(String major) {
        this.major = major;
    }
    
    public String getWorkStartDate() {
        return workStartDate;
    }
    
    public void setWorkStartDate(String workStartDate) {
        this.workStartDate = workStartDate;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getIdCard() {
        return idCard;
    }
    
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPersonnelType() {
        return personnelType;
    }
    
    public void setPersonnelType(String personnelType) {
        this.personnelType = personnelType;
    }
    
    public String getTownName() {
        return townName;
    }
    
    public void setTownName(String townName) {
        this.townName = townName;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public String getRank() {
        return rank;
    }
    
    public void setRank(String rank) {
        this.rank = rank;
    }
    
    public boolean isVillageLeader() {
        return isVillageLeader;
    }
    
    public void setVillageLeader(boolean villageLeader) {
        isVillageLeader = villageLeader;
    }
    
    public String getVillageName() {
        return villageName;
    }
    
    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }
    
    public String getVillageType() {
        return villageType;
    }
    
    public void setVillageType(String villageType) {
        this.villageType = villageType;
    }
    
    public String getVillagePosition() {
        return villagePosition;
    }
    
    public void setVillagePosition(String villagePosition) {
        this.villagePosition = villagePosition;
    }
    
    public boolean isHasMultipleIdentities() {
        return hasMultipleIdentities;
    }
    
    public void setHasMultipleIdentities(boolean hasMultipleIdentities) {
        this.hasMultipleIdentities = hasMultipleIdentities;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    public String getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
