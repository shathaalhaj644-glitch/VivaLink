package com.example.vivalink;

public class Donations {
    private String donationId;
    private String donorId;
    private String hospitalId;
    private String quantity;
    private String donationDate;
    private String status;
    private String requestId;


    public Donations() {}


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
