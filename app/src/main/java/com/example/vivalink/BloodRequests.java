package com.example.vivalink;

public class BloodRequests {
    private String requestId;
    private String hospitalName;
    private String bloodType;
    private String city;
    private String department;
    private String units;
    private String time;

    // ضروري جداً لـ Firebase
    public BloodRequests() {}

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getHospitalName() { return hospitalName != null ? hospitalName : "غير معروف"; }
    public String getBloodType() { return bloodType != null ? bloodType : "--"; }
    public String getCity() { return city != null ? city : ""; }
    public String getDepartment() { return department != null ? department : ""; }
    public String getUnits() { return units != null ? units : "0"; }
    public String getTime() { return time != null ? time : ""; }
}