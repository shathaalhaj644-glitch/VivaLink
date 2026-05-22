package com.example.vivalink;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BloodRequests {
    private String bloodType, hospitalName, city, department, units, status, requestId, date, city_bloodType;
    private boolean arrivalConfirmed, confirmedByStaff;
    private double arrivalTimeHours;
    private String assignedDonorId, confirmedAt, staffConfirmedAt;
    private int donatedCount;
    private String donorPhone, hospitalId, role;

    public BloodRequests() {}


    public String getBloodType() { return bloodType; }
    public String getHospitalName() { return hospitalName; }
    public String getCity() { return city; }
    public String getDepartment() { return department; }
    public String getUnits() { return units; }
    public String getStatus() { return status; }
    public String getRequestId() { return requestId; }
    public String getDate() { return date; }
    public String getCity_bloodType() { return city_bloodType; }
    public boolean isArrivalConfirmed() { return arrivalConfirmed; }
    public double getArrivalTimeHours() { return arrivalTimeHours; }
    public String getAssignedDonorId() { return assignedDonorId; }
    public String getConfirmedAt() { return confirmedAt; }
    public boolean isConfirmedByStaff() { return confirmedByStaff; }
    public int getDonatedCount() { return donatedCount; }
    public String getDonorPhone() { return donorPhone; }
    public String getHospitalId() { return hospitalId; }
    public String getRole() { return role; }
    public String getStaffConfirmedAt() { return staffConfirmedAt; }


    public String getFormattedRequestDate() {
        try {
            if (confirmedAt != null && !confirmedAt.isEmpty()) {

                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
                Date dateObj = isoFormat.parse(confirmedAt);


                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.ENGLISH);
                return outputFormat.format(dateObj);
            } else {
                return date != null ? date : "--";
            }
        } catch (Exception e) {
            return confirmedAt != null ? confirmedAt : "--";
        }
    }

    // Setters
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public void setCity(String city) { this.city = city; }
    public void setDepartment(String department) { this.department = department; }
    public void setUnits(String units) { this.units = units; }
    public void setStatus(String status) { this.status = status; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setDate(String date) { this.date = date; }
    public void setCity_bloodType(String city_bloodType) { this.city_bloodType = city_bloodType; }
    public void setArrivalConfirmed(boolean arrivalConfirmed) { this.arrivalConfirmed = arrivalConfirmed; }
    public void setArrivalTimeHours(double arrivalTimeHours) { this.arrivalTimeHours = arrivalTimeHours; }
    public void setAssignedDonorId(String assignedDonorId) { this.assignedDonorId = assignedDonorId; }
    public void setConfirmedAt(String confirmedAt) { this.confirmedAt = confirmedAt; }
    public void setConfirmedByStaff(boolean confirmedByStaff) { this.confirmedByStaff = confirmedByStaff; }
    public void setDonatedCount(int donatedCount) { this.donatedCount = donatedCount; }
    public void setDonorPhone(String donorPhone) { this.donorPhone = donorPhone; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }
    public void setRole(String role) { this.role = role; }
    public void setStaffConfirmedAt(String staffConfirmedAt) { this.staffConfirmedAt = staffConfirmedAt; }
}
