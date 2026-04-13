package com.example.vivalink;

public class HospitalRequestModel {
    public String requestId, bloodType, city, hospitalName, status, department, date, time, hospitalId, units, phone, city_bloodType;

    public HospitalRequestModel() {}

    public HospitalRequestModel(String requestId, String bloodType, String city, String hospitalName,
                                String units, String status, String department, String date, String time,
                                String hospitalId, String phone, String city_bloodType) {
        this.requestId = requestId;
        this.bloodType = bloodType;
        this.city = city;
        this.hospitalName = hospitalName;
        this.units = units;
        this.status = status;
        this.department = department;
        this.date = date;
        this.time = time;
        this.hospitalId = hospitalId;
        this.phone = phone;
        this.city_bloodType = city_bloodType;
    }
}