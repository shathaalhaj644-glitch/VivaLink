package com.example.vivalink;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RequestModel {

    private String hospitalName, bloodType, city, department, units, requestId, confirmedAt, city_bloodType;
    private boolean isDonated;

    public RequestModel() {}


    public String getFormattedDate() {
        if (confirmedAt == null || confirmedAt.isEmpty() || confirmedAt.equals("--")) {
            return "--";
        }
        try {

            SimpleDateFormat parser = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
            Date dateObj = parser.parse(confirmedAt);


            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.ENGLISH);
            return formatter.format(dateObj);
        } catch (Exception e) {
            return confirmedAt;
        }
    }

    public String getHospitalName() { return hospitalName; }
    public String getBloodType() { return bloodType; }
    public String getCity() { return city; }
    public String getDepartment() { return department; }
    public String getUnits() { return units; }
    public String getRequestId() { return requestId; }
    public String getConfirmedAt() { return confirmedAt; }
    public boolean isDonated() { return isDonated; }


    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setDonated(boolean donated) { this.isDonated = donated; }
    public void setConfirmedAt(String confirmedAt) { this.confirmedAt = confirmedAt; }
}