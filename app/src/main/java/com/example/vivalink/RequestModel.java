package com.example.vivalink;

public class RequestModel {
    private String hospitalName, bloodType, city, department, units, status, requestId;

    // 1. الحقل الرقمي للوقت (Timestamp) بدلاً من String
    private long timestamp;

    // 2. حقل الدمج للفلترة (Nablus_O+)
    private String city_bloodType;

    // 3. Constructor فارغ (إجباري للـ Firebase)
    public RequestModel() {}

    // 4. Constructor شامل لتعبئة البيانات عند إنشاء طلب جديد
    public RequestModel(String hospitalName, String bloodType, String city,
                        String department, String units, String status,
                        long timestamp, String city_bloodType) {
        this.hospitalName = hospitalName;
        this.bloodType = bloodType;
        this.city = city;
        this.department = department;
        this.units = units;
        this.status = status;
        this.timestamp = timestamp;
        this.city_bloodType = city_bloodType;
    }

    // 5. Getters
    public String getHospitalName() { return hospitalName; }
    public String getBloodType() { return bloodType; }
    public String getCity() { return city; }
    public String getDepartment() { return department; }
    public String getUnits() { return units; }
    public String getStatus() { return status; }
    public String getRequestId() { return requestId; }
    public long getTimestamp() { return timestamp; }
    public String getCity_bloodType() { return city_bloodType; }

    // 6. Setters (خاصة للـ ID والوقت)
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setCity_bloodType(String city_bloodType) { this.city_bloodType = city_bloodType; }
}