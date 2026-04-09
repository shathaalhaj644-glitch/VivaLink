package com.example.vivalink;

public class HospitalDonorsModel {

    private String id;
    private String fullName;
    private String bloodType;
    private String city;
    private String phone;
    private String lastDonation;
    private String lastBloodTest;
    private Integer donationCount;

    private boolean isVerifiedByHospital;
    private String officialStatus;
    private String hospitalNote;

    public HospitalDonorsModel() {}

    public HospitalDonorsModel(String id, String fullName, String bloodType, String city,
                               String phone, String lastDonation, String lastBloodTest, Integer donationCount,
                               boolean isVerifiedByHospital, String officialStatus, String hospitalNote) {
        this.id = id;
        this.fullName = fullName;
        this.bloodType = bloodType;
        this.city = city;
        this.phone = phone;
        this.lastDonation = lastDonation;
        this.lastBloodTest = lastBloodTest;
        this.donationCount = donationCount;
        this.isVerifiedByHospital = isVerifiedByHospital;
        this.officialStatus = officialStatus;
        this.hospitalNote = hospitalNote;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public String getLastBloodTest() {
        return (lastBloodTest == null || lastBloodTest.isEmpty()) ? "none" : lastBloodTest;
    }
    public void setLastBloodTest(String lastBloodTest) {
        this.lastBloodTest = lastBloodTest;
    }

    public Integer getDonationCount() {
        return donationCount == null ? 0 : donationCount;
    }
    public void setDonationCount(Integer donationCount) {
        this.donationCount = donationCount;
    }

    public boolean isVerifiedByHospital() { return isVerifiedByHospital; }
    public void setVerifiedByHospital(boolean verifiedByHospital) { isVerifiedByHospital = verifiedByHospital; }

    public String getOfficialStatus() { return officialStatus; }
    public void setOfficialStatus(String officialStatus) { this.officialStatus = officialStatus; }

    public String getHospitalNote() { return hospitalNote; }
    public void setHospitalNote(String hospitalNote) { this.hospitalNote = hospitalNote; }
}