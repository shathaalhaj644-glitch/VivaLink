package com.example.vivalink;

public class Hospitals {
    private String hospitalId;       // PK
    private String userId;           // FK
    private String hospitalName;     // اسم المستشفى
    private String address;          // العنوان
    private String contactPhone;     // رقم التواصل
    private boolean verified;        // حالة التوثيق

    // 1. Constructor فارغ (إجباري للفايربيس)
    public Hospitals() {}

    // 2. Constructor كامل
    public Hospitals(String hospitalId, String userId, String hospitalName,
                     String address, String contactPhone, boolean verified) {
        this.hospitalId = hospitalId;
        this.userId = userId;
        this.hospitalName = hospitalName;
        this.address = address;
        this.contactPhone = contactPhone;
        this.verified = verified;
    }

    // 3. Getters & Setters
    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    // 4. toString() لسهولة الطباعة أثناء الـ Debugging
    @Override
    public String toString() {
        return "Hospitals{" +
                "hospitalId='" + hospitalId + '\'' +
                ", userId='" + userId + '\'' +
                ", hospitalName='" + hospitalName + '\'' +
                ", address='" + address + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", verified=" + verified +
                '}';
    }
}
