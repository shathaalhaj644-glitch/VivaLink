package com.example.vivalink;

public class RequestModel {
    private String hospitalName, bloodType, city, department, units, requestId, date;
    private boolean isDonated;

    public RequestModel() {}

    // Getters
    public String getHospitalName() { return hospitalName; }
    public String getBloodType() { return bloodType; }
    public String getCity() { return city; }
    public String getDepartment() { return department; }
    public String getUnits() { return units; }
    public String getRequestId() { return requestId; }
    public String getDate() { return date; }
    public boolean isDonated() { return isDonated; }


    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setDonated(boolean donated) { isDonated = donated; }
}