package com.example.vivalink;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class BloodBankDonorsModel {
    private String uid;
    private String name, fullName, phone, bloodType, city, lastDonation;
    private Object donationCount;
    private String note;


    private String bloodTestStatus;
    private String bloodTestProofUrl;
    private String lastBloodTest;


    private String donorId;
    private String requestId;
    private String status;
    private String hospitalId;
    private String nextDonationDate;

    public BloodBankDonorsModel() {

    }


    public String getDisplayName() {
        if (fullName != null && !fullName.isEmpty() && !fullName.equals("null")) {
            return fullName;
        }
        return (name != null) ? name : "متبرع غير معروف";
    }


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

    public String getLastDonation() {
        return (lastDonation == null || lastDonation.isEmpty() || lastDonation.equals("--")) ? "لا يوجد" : lastDonation;
    }
    public void setLastDonation(String lastDonation) { this.lastDonation = lastDonation; }

    public String getDonationCount() {
        return donationCount == null ? "0" : String.valueOf(donationCount);
    }
    public void setDonationCount(Object donationCount) { this.donationCount = donationCount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }


    public String getBloodTestStatus() {
        return (bloodTestStatus == null || bloodTestStatus.isEmpty()) ? "غير متوفر" : bloodTestStatus;
    }
    public void setBloodTestStatus(String bloodTestStatus) { this.bloodTestStatus = bloodTestStatus; }

    public String getBloodTestProofUrl() { return bloodTestProofUrl; }
    public void setBloodTestProofUrl(String bloodTestProofUrl) { this.bloodTestProofUrl = bloodTestProofUrl; }

    public String getLastBloodTest() { return lastBloodTest; }
    public void setLastBloodTest(String lastBloodTest) { this.lastBloodTest = lastBloodTest; }



    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getNextDonationDate() { return nextDonationDate; }
    public void setNextDonationDate(String nextDonationDate) { this.nextDonationDate = nextDonationDate; }
}