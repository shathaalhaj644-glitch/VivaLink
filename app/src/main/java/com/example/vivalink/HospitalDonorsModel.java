package com.example.vivalink;

public class HospitalDonorsModel {
    public String fullName, bloodType, phone, city, role;

    public HospitalDonorsModel() {} // إجباري للفايربيس

    public HospitalDonorsModel(String fullName, String bloodType, String phone, String city, String role) {
        this.fullName = fullName;
        this.bloodType = bloodType;
        this.phone = phone;
        this.city = city;
        this.role = role;
    }

    // Getters آمنة لمنع الكراش
    public String getFullName() { return (fullName != null) ? fullName : "بدون اسم"; }
    public String getBloodType() { return (bloodType != null) ? bloodType : "--"; }
    public String getPhone() { return (phone != null) ? phone : ""; }
    public String getCity() { return (city != null) ? city : ""; }
}
