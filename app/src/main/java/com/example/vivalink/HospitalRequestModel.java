package com.example.vivalink;

public class HospitalRequestModel {

    public String requestId, bloodType, city, hospitalName, status, department, date, hospitalId, units;
    public String city_bloodType;

    public HospitalRequestModel() {}

    public HospitalRequestModel(String requestId, String bloodType, String city,
                                String hospitalName, String units, String status,
                                String department, String date, String hospitalId,
                                String city_bloodType) {

        this.requestId = requestId;
        this.bloodType = bloodType;
        this.city = city;
        this.hospitalName = hospitalName;
        this.units = units;
        this.status = status;
        this.department = department;
        this.date = date;
        this.hospitalId = hospitalId;
        this.city_bloodType = city_bloodType;
    }

    public String getCity_bloodType() {
        return city_bloodType;
    }

    public void setCity_bloodType(String city_bloodType) {
        this.city_bloodType = city_bloodType;
    }
}