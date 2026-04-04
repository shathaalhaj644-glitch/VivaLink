package com.example.vivalink;

public class Donors {
    private String name;
    private String email;
    private String phone;
    private String bloodType;
    private String city;
    private String lastDonation;
    private String role;

    public Donors() {}
    public Donors(String name, String email, String city, String bloodType, String phone, String lastDonation, String role) {
        this.name = name;
        this.email = email;
        this.city = city;
        this.bloodType = bloodType;
        this.phone = phone;
        this.lastDonation = lastDonation;
        this.role = role;
    }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getLastDonation() { return lastDonation; }
    public void setLastDonation(String lastDonation) { this.lastDonation = lastDonation; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}