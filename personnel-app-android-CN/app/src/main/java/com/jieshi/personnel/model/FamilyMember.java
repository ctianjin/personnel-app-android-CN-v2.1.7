package com.jieshi.personnel.model;

import android.content.Context;

import com.jieshi.personnel.util.CryptoUtil;
import com.jieshi.personnel.util.KeyManager;

/**
 * 近亲属信息结构体
 */
public class FamilyMember {
    /** 姓名 */
    private String name;
    /** 关系 */
    private String relationship;
    /** 工作单位 */
    private String company;
    /** 职务 */
    private String position;
    /** 联系方式 */
    private String phone;
    /** 政治面貌 */
    private String politicalStatus;

    public FamilyMember() {
    }

    public FamilyMember(String name, String relationship) {
        this.name = name;
        this.relationship = relationship;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPoliticalStatus() {
        return politicalStatus;
    }

    public void setPoliticalStatus(String politicalStatus) {
        this.politicalStatus = politicalStatus;
    }

    @Override
    public String toString() {
        return "FamilyMember{" +
                "name='" + name + '\'' +
                ", relationship='" + relationship + '\'' +
                ", company='" + company + '\'' +
                ", position='" + position + '\'' +
                ", phone='" + phone + '\'' +
                ", politicalStatus='" + politicalStatus + '\'' +
                '}';
    }

    // ==================== 加密/解密方法 ====================
    
    /**
     * 加密敏感字段（电话号码）
     * 
     * @param context Android 上下文
     */
    public void encryptSensitiveFields(Context context) {
        if (context == null || phone == null || phone.isEmpty()) return;
        
        if (!CryptoUtil.isEncrypted(phone)) {
            KeyManager keyManager = new KeyManager(context);
            byte[] key = keyManager.getDataEncryptionKey();
            phone = CryptoUtil.encrypt(phone, key, CryptoUtil.generateIV());
        }
    }
    
    /**
     * 解密敏感字段（电话号码）
     * 
     * @param context Android 上下文
     */
    public void decryptSensitiveFields(Context context) {
        if (context == null || phone == null || phone.isEmpty()) return;
        
        if (CryptoUtil.isEncrypted(phone)) {
            try {
                KeyManager keyManager = new KeyManager(context);
                byte[] key = keyManager.getDataEncryptionKey();
                phone = CryptoUtil.decrypt(phone, key);
            } catch (Exception e) {
                phone = "[解密失败]";
            }
        }
    }
}
