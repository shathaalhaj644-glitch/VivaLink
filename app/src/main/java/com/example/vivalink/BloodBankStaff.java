package com.example.vivalink;

public class BloodBankStaff {
    private String staffId;
    private String userId;
    private String bankId;
    private String fullName;
    private String employeeNumber;
    private String position;


    public BloodBankStaff() {}


    public BloodBankStaff(String staffId, String userId, String bankId,
                          String fullName, String employeeNumber, String position) {
        this.staffId = staffId;
        this.userId = userId;
        this.bankId = bankId;
        this.fullName = fullName;
        this.employeeNumber = employeeNumber;
        this.position = position;
    }


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
