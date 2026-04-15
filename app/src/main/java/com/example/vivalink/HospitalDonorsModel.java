package com.example.vivalink;

public class HospitalDonorsModel {
    private String uid, fullName, bloodType, city, phone, lastDonation, lastBloodTest, diseaseName, hospitalName;
    private Integer donationCount;
    private boolean hasDisease;

    public HospitalDonorsModel() {}

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

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

    public String getLastBloodTest() { return lastBloodTest; }
    public void setLastBloodTest(String lastBloodTest) { this.lastBloodTest = lastBloodTest; }

    public String getDiseaseName() { return diseaseName; }
    public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public Integer getDonationCount() { return donationCount == null ? 0 : donationCount; }
    public void setDonationCount(Integer donationCount) { this.donationCount = donationCount; }

    public boolean isHasDisease() { return hasDisease; }
    public void setHasDisease(boolean hasDisease) { this.hasDisease = hasDisease; }
}
