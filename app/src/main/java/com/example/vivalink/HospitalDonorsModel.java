package com.example.vivalink;

public class HospitalDonorsModel {
    private String uid;
    private String fullName;
    private String bloodType;
    private String city;
    private String phone;
    private String lastDonation;
    private String lastBloodTest;
    private String diseaseName;
    private String hospitalName;


    private Integer donationCount;
    private boolean hasDisease;
    private String bloodLevel;
    private String bloodTestStatus;
    private String role;
    private String status;
    private String confirmedAt;
    private boolean confirmedByStaff;

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

    public String getBloodLevel() { return bloodLevel; }
    public void setBloodLevel(String bloodLevel) { this.bloodLevel = bloodLevel; }

    public String getBloodTestStatus() { return bloodTestStatus; }
    public void setBloodTestStatus(String bloodTestStatus) { this.bloodTestStatus = bloodTestStatus; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(String confirmedAt) { this.confirmedAt = confirmedAt; }

    public boolean isConfirmedByStaff() { return confirmedByStaff; }
    public void setConfirmedByStaff(boolean confirmedByStaff) { this.confirmedByStaff = confirmedByStaff; }
}
