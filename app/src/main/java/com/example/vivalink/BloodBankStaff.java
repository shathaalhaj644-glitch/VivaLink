package com.example.vivalink;

public class BloodBankStaff {

    private String name;
    private String email;
    private String phone;
    private String city;
    private String hospitalName;
    private String hospitalId;
    private String role;

    public BloodBankStaff() {}


    public BloodBankStaff(String name, String email, String phone, String city,
                          String hospitalName, String hospitalId, String role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.hospitalName = hospitalName;
        this.hospitalId = hospitalId;
        this.role = role;
    }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}