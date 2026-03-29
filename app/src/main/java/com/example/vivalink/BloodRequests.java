package com.example.vivalink;

public class BloodRequests {
    private String bloodType;
    private String hospitalName;
    private String city;
    private String department;
    private String units;
    private String status;
    private String requestId;

    // 1. تغيير الاسم من timestamp لـ time وتغيير النوع لـ String
    private String time;

    // 2. حقل الفلترة المركبة
    private String city_bloodType;

    // مشيد فارغ مطلوب للفايربيس
    public BloodRequests() {}

    // Getters
    public String getBloodType() { return bloodType; }
    public String getHospitalName() { return hospitalName; }
    public String getCity() { return city; }
    public String getDepartment() { return department; }
    public String getUnits() { return units; }
    public String getStatus() { return status; }
    public String getRequestId() { return requestId; }
    public String getTime() { return time; } // تعديل الـ Getter
    public String getCity_bloodType() { return city_bloodType; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setTime(String time) { this.time = time; } // تعديل الـ Setter
    public void setCity_bloodType(String city_bloodType) { this.city_bloodType = city_bloodType; }
}