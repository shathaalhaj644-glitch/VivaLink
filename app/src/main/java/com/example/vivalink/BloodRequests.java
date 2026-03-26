package com.example.vivalink;

public class BloodRequests {
    private String bloodType;
    private String hospitalName;
    private String city;
    private String department;
    private String units;
    private String time;
    private String status; // الحقل اللي كان ناقص
    private String requestId;

    public BloodRequests() {}

    public String getBloodType() { return bloodType; }
    public String getHospitalName() { return hospitalName; }
    public String getCity() { return city; }
    public String getDepartment() { return department; }
    public String getUnits() { return units; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public String getRequestId() { return requestId; }

    public void setStatus(String status) { this.status = status; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
}