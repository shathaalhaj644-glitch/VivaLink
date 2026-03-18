package com.example.vivalink;

public class BloodRequests {
    private String requestId;
    private String hospitalName;
    private String bloodType;
    private String location;
    private String department;
    private String units;
    private String publishTime;
    private String status; // الحالة (open, completed, cancelled)

    // Constructor فارغ (مطلوب لـ Firebase)
    public BloodRequests() {}

    // Constructor كامل
    public BloodRequests(String requestId, String hospitalName, String bloodType, String location,
                         String department, String units, String publishTime, String status) {
        this.requestId = requestId;
        this.hospitalName = hospitalName;
        this.bloodType = bloodType;
        this.location = location;
        this.department = department;
        this.units = units;
        this.publishTime = publishTime;
        this.status = status;
    }

    // Getters
    public String getRequestId() { return requestId; }
    public String getHospitalName() { return hospitalName; }
    public String getBloodType() { return bloodType; }
    public String getLocation() { return location; }
    public String getDepartment() { return department; }
    public String getUnits() { return units; }
    public String getPublishTime() { return publishTime; }
    public String getStatus() { return status; }

    // Setters
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public void setLocation(String location) { this.location = location; }
    public void setDepartment(String department) { this.department = department; }
    public void setUnits(String units) { this.units = units; }
    public void setPublishTime(String publishTime) { this.publishTime = publishTime; }
    public void setStatus(String status) { this.status = status; }
}
