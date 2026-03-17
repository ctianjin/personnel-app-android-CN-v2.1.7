package com.jieshi.personnel.model;

/**
 * 村层级结构体
 * 用于描述村（社区）的组织架构
 */
public class VillageLevel {
    /** 村/社区名称 */
    private String villageName;
    /** 村类型（村/社区） */
    private String villageType;
    /** 职务 */
    private String position;
    /** 是否第一书记 */
    private boolean isSecretary;
    /** 是否驻村领导（镇府派驻） */
    private boolean isStationedLeader;
    /** 所属镇名 */
    private String townName;

    public VillageLevel() {
    }

    public VillageLevel(String villageName, String villageType, String position) {
        this.villageName = villageName;
        this.villageType = villageType;
        this.position = position;
        this.isSecretary = false;
        this.isStationedLeader = false;
    }

    // Getters and Setters
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isSecretary() {
        return isSecretary;
    }

    public void setSecretary(boolean secretary) {
        isSecretary = secretary;
    }

    public boolean isStationedLeader() {
        return isStationedLeader;
    }

    public void setStationedLeader(boolean stationedLeader) {
        isStationedLeader = stationedLeader;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    @Override
    public String toString() {
        return "VillageLevel{" +
                "villageName='" + villageName + '\'' +
                ", villageType='" + villageType + '\'' +
                ", position='" + position + '\'' +
                ", isSecretary=" + isSecretary +
                ", isStationedLeader=" + isStationedLeader +
                ", townName='" + townName + '\'' +
                '}';
    }
}
