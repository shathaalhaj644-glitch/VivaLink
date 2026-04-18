package com.example.vivalink;

public class Donors {
    private String name, email, phone, bloodType, city, lastDonation, role;
    private String bloodLevel, bloodTestProofUrl, bloodTestSubmittedAt, bloodTestStatus;
    private String createdAt, diseaseDetails, diseaseName, donationCount, fullName;
    private String hasDisease, hasDiseases, lastBloodTest, uid;

    public Donors() {}

    public Donors(String name, String email, String city, String bloodType, String phone,
                  String lastDonation, String role) {
        this.name = name;
        this.email = email;
        this.city = city;
        this.bloodType = bloodType;
        this.phone = phone;
        this.lastDonation = lastDonation;
        this.role = role;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getBloodType() { return bloodType; }
    public String getCity() { return city; }
    public String getLastDonation() { return lastDonation; }
    public String getRole() { return role; }
    public String getBloodLevel() { return bloodLevel; }
    public String getBloodTestProofUrl() { return bloodTestProofUrl; }
    public String getBloodTestSubmittedAt() { return bloodTestSubmittedAt; }
    public String getBloodTestStatus() { return bloodTestStatus; }
    public String getCreatedAt() { return createdAt; }
    public String getDiseaseDetails() { return diseaseDetails; }
    public String getDiseaseName() { return diseaseName; }
    public String getDonationCount() { return donationCount; }
    public String getFullName() { return fullName; }
    public String getHasDisease() { return hasDisease; }
    public String getHasDiseases() { return hasDiseases; }
    public String getLastBloodTest() { return lastBloodTest; }
    public String getUid() { return uid; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public void setCity(String city) { this.city = city; }
    public void setLastDonation(String lastDonation) { this.lastDonation = lastDonation; }
    public void setRole(String role) { this.role = role; }
    public void setBloodLevel(String bloodLevel) { this.bloodLevel = bloodLevel; }
    public void setBloodTestProofUrl(String bloodTestProofUrl) { this.bloodTestProofUrl = bloodTestProofUrl; }
    public void setBloodTestSubmittedAt(String bloodTestSubmittedAt) { this.bloodTestSubmittedAt = bloodTestSubmittedAt; }
    public void setBloodTestStatus(String bloodTestStatus) { this.bloodTestStatus = bloodTestStatus; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setDiseaseDetails(String diseaseDetails) { this.diseaseDetails = diseaseDetails; }
    public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }
    public void setDonationCount(String donationCount) { this.donationCount = donationCount; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setHasDisease(String hasDisease) { this.hasDisease = hasDisease; }
    public void setHasDiseases(String hasDiseases) { this.hasDiseases = hasDiseases; }
    public void setLastBloodTest(String lastBloodTest) { this.lastBloodTest = lastBloodTest; }
    public void setUid(String uid) { this.uid = uid; }
}
