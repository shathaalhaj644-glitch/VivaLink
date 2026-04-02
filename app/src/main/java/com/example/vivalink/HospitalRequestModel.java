package com.example.vivalink;

public class HospitalRequestModel {
    // تم تغيير units إلى String لتطابق "وحدتين" الموجودة في قاعدة بياناتك
    public String requestId, bloodType, city, hospitalName, status, department, date, hospitalId, units;

    public HospitalRequestModel() {}

    public HospitalRequestModel(String requestId, String bloodType, String city,
                                String hospitalName, String units, String status,
                                String department, String date, String hospitalId) {
        this.requestId = requestId;
        this.bloodType = bloodType;
        this.city = city;
        this.hospitalName = hospitalName;
        this.units = units;
        this.status = status;
        this.department = department;
        this.date = date;
        this.hospitalId = hospitalId;
    }
}