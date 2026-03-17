package com.jieshi.personnel.model;

/**
 * 奖惩信息结构体
 */
public class AwardPunishment {
    /** 类型（奖励/惩罚） */
    private String type;
    /** 日期 */
    private String date;
    /** 名称/事由 */
    private String name;
    /** 级别 */
    private String level;
    /** 描述 */
    private String description;

    public AwardPunishment() {
    }

    public AwardPunishment(String type, String date, String name) {
        this.type = type;
        this.date = date;
        this.name = name;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AwardPunishment{" +
                "type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", level='" + level + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
