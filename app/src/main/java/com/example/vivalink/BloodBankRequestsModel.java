package com.example.vivalink;

public class BloodBankRequestsModel {
    // الحقول مطابقة تماماً لأسماء المفاتيح في Firebase
    private String requestId;
    private String hospitalName;
    private String bloodType;
    private String units;
    private String department;
    private String confirmedAt; // تم التعديل بناءً على طلبك
    private String status;
    private String city;
    private String phone;
    private String hospitalId;

    // 1. Constructor فارغ (إلزامي لعمل Firebase)
    public BloodBankRequestsModel() {
    }

    // 2. Constructor بكافة الحقول
    public BloodBankRequestsModel(String requestId, String hospitalName, String bloodType, String units, String department, String confirmedAt, String status, String city, String phone, String hospitalId) {
        this.requestId = requestId;
        this.hospitalName = hospitalName;
        this.bloodType = bloodType;
        this.units = units;
        this.department = department;
        this.confirmedAt = confirmedAt;
        this.status = status;
        this.city = city;
        this.phone = phone;
        this.hospitalId = hospitalId;
    }

    // 3. Getters and Setters (مهمة جداً للقراءة والكتابة)

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getUnits() { return units; }
    public void setUnits(String units) { this.units = units; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    // الـ Getter الخاص بالتاريخ والوقت
    public String getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(String confirmedAt) { this.confirmedAt = confirmedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }
}