package com.example.vivalink;

public class HospitalDonorsModel {
    private String fullName;
    private String bloodType;
    private String city;
    private String phone;
    private String lastDonation;
    private int donationCount;

    // Constructor فاضي (إجباري للفايربيس)
    public HospitalDonorsModel() {}

    // Constructor كامل (للتجارب والـ List اليدوية)
    public HospitalDonorsModel(String fullName, String bloodType, String city, String phone, String lastDonation, int donationCount) {
        this.fullName = fullName;
        this.bloodType = bloodType;
        this.city = city;
        this.phone = phone;
        this.lastDonation = lastDonation;
        this.donationCount = donationCount;
    }

    // Getters & Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLastDonation() { return lastDonation; }
    public void setLastDonation(String lastDonation) { this.lastDonation = lastDonation; }

    public int getDonationCount() { return donationCount; }
    public void setDonationCount(int donationCount) { this.donationCount = donationCount; }
}