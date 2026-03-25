package com.example.vivalink;

public class BloodBankStaff {
    private String staffId;          // PK
    private String userId;           // FK لربطه بجدول الـ Users
    private String bankId;           // FK لربطه بجدول الـ BloodBanks
    private String fullName;         // الاسم الكامل
    private String employeeNumber;   // رقم الموظف
    private String position;         // المسمى الوظيفي

    // Constructor فارغ (مطلوب للفايربيس)
    public BloodBankStaff() {}

    // Constructor كامل
    public BloodBankStaff(String staffId, String userId, String bankId,
                          String fullName, String employeeNumber, String position) {
        this.staffId = staffId;
        this.userId = userId;
        this.bankId = bankId;
        this.fullName = fullName;
        this.employeeNumber = employeeNumber;
        this.position = position;
    }

    // Getters & Setters
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBankId() { return bankId; }
    public void setBankId(String bankId) { this.bankId = bankId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}
