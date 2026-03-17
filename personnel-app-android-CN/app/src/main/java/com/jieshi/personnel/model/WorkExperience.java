package com.jieshi.personnel.model;

/**
 * 履历信息结构体
 */
public class WorkExperience {
    /** 开始时间 */
    private String startDate;
    /** 结束时间 */
    private String endDate;
    /** 工作单位 */
    private String company;
    /** 职务 */
    private String position;
    /** 工作描述 */
    private String description;

    public WorkExperience() {
    }

    public WorkExperience(String startDate, String endDate, String company, String position) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.company = company;
        this.position = position;
    }

    // Getters and Setters
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "WorkExperience{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", company='" + company + '\'' +
                ", position='" + position + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
