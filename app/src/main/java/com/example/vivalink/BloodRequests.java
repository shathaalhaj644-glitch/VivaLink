package com.example.vivalink;

public class BloodRequests {
    private String bloodType, hospitalName, city, department, units, status, requestId, date, city_bloodType;

    public BloodRequests() {}

    // Getters
    public String getBloodType() { return bloodType; }
    public String getHospitalName() { return hospitalName; }
    public String getCity() { return city; }
    public String getDepartment() { return department; }
    public String getUnits() { return units; }
    public String getStatus() { return status; }
    public String getRequestId() { return requestId; }
    public String getDate() { return date; } // تم التعديل لـ date
    public String getCity_bloodType() { return city_bloodType; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setDate(String date) { this.date = date; } // تم التعديل لـ date
    public void setCity_bloodType(String city_bloodType) { this.city_bloodType = city_bloodType; }
}