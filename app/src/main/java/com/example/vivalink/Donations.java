package com.example.vivalink;

public class Donations {
    private String donationId;   // PK: رقم العملية
    private String donorId;      // FK: رقم المتبرع
    private String hospitalId;   // FK: رقم المستشفى
    private String quantity;     // كمية الدم
    private String donationDate; // تاريخ التبرع
    private String status;       // حالة التبرع (مثلاً: مقبول، مرفوض، قيد المراجعة)
    private String requestId;    // FK: رقم الطلب (لو التبرع جاي بناءً على طلب معين)

    // 1. Constructor فارغ (إجباري للفايربيس)
    public Donations() {}

    // 2. Constructor كامل
    public Donations(String donationId, String donorId, String hospitalId,
                     String quantity, String donationDate, String status, String requestId) {
        this.donationId = donationId;
        this.donorId = donorId;
        this.hospitalId = hospitalId;
        this.quantity = quantity;
        this.donationDate = donationDate;
        this.status = status;
        this.requestId = requestId;
    }

    // 3. Getters & Setters
    public String getDonationId() { return donationId; }
    public void setDonationId(String donationId) { this.donationId = donationId; }

    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getDonationDate() { return donationDate; }
    public void setDonationDate(String donationDate) { this.donationDate = donationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    // 4. toString() لسهولة الطباعة أثناء الـ Debugging
    @Override
    public String toString() {
        return "Donation{" +
                "donationId='" + donationId + '\'' +
                ", donorId='" + donorId + '\'' +
                ", hospitalId='" + hospitalId + '\'' +
                ", quantity='" + quantity + '\'' +
                ", donationDate='" + donationDate + '\'' +
                ", status='" + status + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
