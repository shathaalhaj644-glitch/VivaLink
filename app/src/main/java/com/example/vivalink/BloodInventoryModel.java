package com.example.vivalink;

public class BloodInventoryModel {
    // أضفنا fromHospitalId هنا
    public String bloodType, hospitalId, hospitalName, city, requestId, status, fromHospitalName, fromHospitalId;
    public int units, threshold, requestedUnits;

    public BloodInventoryModel() {}
}