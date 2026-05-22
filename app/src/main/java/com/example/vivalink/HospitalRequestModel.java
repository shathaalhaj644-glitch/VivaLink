package com.example.vivalink;

public class HospitalRequestModel {

    public String requestId, bloodType, city, hospitalName, status, department, confirmedAt, hospitalId, units, phone, city_bloodType;


    public String donorId;

    public int donatedCount;


    public HospitalRequestModel() {}

    public HospitalRequestModel(String requestId, String bloodType, String city, String hospitalName,
                                String units, String status, String department, String confirmedAt,
                                String hospitalId, String phone, String city_bloodType, int donatedCount, String donorId) {
        this.requestId = requestId;
        this.bloodType = bloodType;
        this.city = city;
        this.hospitalName = hospitalName;
        this.units = units;
        this.status = status;
        this.department = department;
        this.confirmedAt = confirmedAt;
        this.hospitalId = hospitalId;
        this.phone = phone;
        this.city_bloodType = city_bloodType;
        this.donatedCount = donatedCount;
        this.donorId = donorId;
    }


    public String getFormattedDate() {
        if (confirmedAt == null || confirmedAt.isEmpty() || confirmedAt.equals("--")) {
            return "--";
        }
        try {

            java.text.SimpleDateFormat parser = new java.text.SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss.SSS", java.util.Locale.ENGLISH);
            java.util.Date dateObj = parser.parse(confirmedAt);


            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm", java.util.Locale.ENGLISH);
            return formatter.format(dateObj);
        } catch (Exception e) {

            return confirmedAt;
        }
    }
}