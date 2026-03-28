package com.example.vivalink;

public class BloodRequests {
    private String bloodType;
    private String hospitalName;
    private String city;
    private String department;
    private String units;
    private String status;
    private String requestId;

    // 1. تغيير نوع الوقت من String إلى long لاستقبال الـ Timestamp من الفايربيس
    private long timestamp;

    // 2. حقل الفلترة المركبة (مدينة_زمرة) لضمان عدم ظهور زمر دم مختلفة
    private String city_bloodType;

    // متغير داخلي (غير موجود في القاعدة) لعرض النص النهائي مثل "منذ 5 دقائق"
    private String timeDisplay;

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
    public long getTimestamp() { return timestamp; }
    public String getCity_bloodType() { return city_bloodType; }
    public String getTimeDisplay() { return timeDisplay; }

    // Setters (مهمة جداً لعملية الـ Mapping)
    public void setStatus(String status) { this.status = status; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setCity_bloodType(String city_bloodType) { this.city_bloodType = city_bloodType; }
    public void setTimeDisplay(String timeDisplay) { this.timeDisplay = timeDisplay; }
}