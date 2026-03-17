package com.jieshi.personnel.model;

/**
 * 人员类型枚举
 * 对应系统三类人员：镇府干部、村两委干部、网格联防员
 */
public enum PersonnelType {
    /** 镇府干部 */
    TOWN_OFFICIAL("镇府干部"),
    /** 村两委干部 */
    VILLAGE_COMMITTEE("村两委干部"),
    /** 网格联防员 */
    GRID_DEFENDER("网格联防员");

    private final String displayName;

    PersonnelType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据显示名称获取枚举值
     */
    public static PersonnelType fromDisplayName(String name) {
        for (PersonnelType type : values()) {
            if (type.displayName.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的人员类型：" + name);
    }
}
