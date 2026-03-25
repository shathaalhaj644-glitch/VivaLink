package com.example.vivalink;

public class RequestModel {
    private String hospitalName, bloodType, city, department, units;

    // 1. Constructor فارغ ضروري للـ Firebase
    public RequestModel() {}

    // 2. Constructor لتعبئة البيانات
    public RequestModel(String hospitalName, String bloodType, String city, String department, String units) {
        this.hospitalName = hospitalName;
        this.bloodType = bloodType;
        this.city = city;
        this.department = department;
        this.units = units;
    }

    // 3. Getters (مهمة جداً للـ Adapter)
    public String getHospitalName() { return hospitalName; }
    public String getBloodType() { return bloodType; }
    public String getCity() { return city; }
    public String getDepartment() { return department; }
    public String getUnits() { return units; }
}