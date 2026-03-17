package com.jieshi.personnel.model;

/**
 * 政府层级结构体
 * 用于描述镇政府的组织架构
 */
public class GovernmentLevel {
    /** 镇名称 */
    private String townName;
    /** 部门名称 */
    private String department;
    /** 职务 */
    private String position;
    /** 职级 */
    private String rank;
    /** 是否驻村领导 */
    private boolean isVillageLeader;
    /** 驻村的村名（可多个） */
    private String[] stationedVillages;

    public GovernmentLevel() {
    }

    public GovernmentLevel(String townName, String department, String position, String rank) {
        this.townName = townName;
        this.department = department;
        this.position = position;
        this.rank = rank;
        this.isVillageLeader = false;
        this.stationedVillages = new String[0];
    }

    // Getters and Setters
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

    public String[] getStationedVillages() {
        return stationedVillages;
    }

    public void setStationedVillages(String[] stationedVillages) {
        this.stationedVillages = stationedVillages;
    }

    @Override
    public String toString() {
        return "GovernmentLevel{" +
                "townName='" + townName + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", rank='" + rank + '\'' +
                ", isVillageLeader=" + isVillageLeader +
                ", stationedVillages=" + java.util.Arrays.toString(stationedVillages) +
                '}';
    }
}
