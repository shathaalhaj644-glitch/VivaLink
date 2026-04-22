package com.example.vivalink;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class BloodBankDonorsModel {
    private String uid, name, fullName, phone, bloodType, city, lastDonation;
    private Object donationCount; // Object لمنع الكراش
    private String note;

    // الحقول الجديدة للفحوصات
    private String bloodTestStatus;
    private String bloodTestProofUrl; // لتخزين نص الصورة Base64
    private String lastBloodTest; // تاريخ آخر فحص مقبول

    public BloodBankDonorsModel() {}

    public String getDisplayName() {
        return (fullName != null && !fullName.isEmpty() && !fullName.equals("null")) ? fullName : name;
    }

    // Getters & Setters القديمة
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getLastDonation() { return (lastDonation == null || lastDonation.isEmpty()) ? "لا يوجد" : lastDonation; }
    public void setLastDonation(String lastDonation) { this.lastDonation = lastDonation; }
    public String getDonationCount() { return donationCount == null ? "0" : String.valueOf(donationCount); }
    public void setDonationCount(Object donationCount) { this.donationCount = donationCount; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    // Getters & Setters الجديدة (ضرورية لعمل الفحوصات)
    public String getBloodTestStatus() {
        return (bloodTestStatus == null || bloodTestStatus.isEmpty()) ? "غير متوفر" : bloodTestStatus;
    }
    public void setBloodTestStatus(String bloodTestStatus) { this.bloodTestStatus = bloodTestStatus; }

    public String getBloodTestProofUrl() { return bloodTestProofUrl; }
    public void setBloodTestProofUrl(String bloodTestProofUrl) { this.bloodTestProofUrl = bloodTestProofUrl; }

    public String getLastBloodTest() { return lastBloodTest; }
    public void setLastBloodTest(String lastBloodTest) { this.lastBloodTest = lastBloodTest; }
}