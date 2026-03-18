package com.example.vivalink;

public class Donors {
    private String donorId;            // PK: رقم المتبرع
    private String userId;             // FK: ربطه بجدول الـ Users
    private String fullName;           // الاسم الكامل
    private String phone;              // رقم الهاتف
    private String bloodType;          // فصيلة الدم
    private String city;               // المدينة
    private String lastDonationDate;   // آخر تاريخ للتبرع
    private String chronicDiseases;    // الأمراض المزمنة (إن وجدت)

    // 1. Constructor فارغ (إجباري للفايربيس)
    public Donors() {}

    // 2. Constructor كامل
    public Donors(String donorId, String userId, String fullName, String phone,
                  String bloodType, String city, String lastDonationDate, String chronicDiseases) {
        this.donorId = donorId;
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.bloodType = bloodType;
        this.city = city;
        this.lastDonationDate = lastDonationDate;
        this.chronicDiseases = chronicDiseases;
    }

    // 3. Getters & Setters
    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(String lastDonationDate) { this.lastDonationDate = lastDonationDate; }

    public String getChronicDiseases() { return chronicDiseases; }
    public void setChronicDiseases(String chronicDiseases) { this.chronicDiseases = chronicDiseases; }

    // 4. toString() لسهولة الطباعة أثناء الـ Debugging
    @Override
    public String toString() {
        return "Donors{" +
                "donorId='" + donorId + '\'' +
                ", userId='" + userId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", city='" + city + '\'' +
                ", lastDonationDate='" + lastDonationDate + '\'' +
                ", chronicDiseases='" + chronicDiseases + '\'' +
                '}';
    }
}
