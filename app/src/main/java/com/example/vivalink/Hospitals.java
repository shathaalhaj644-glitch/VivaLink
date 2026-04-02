package com.example.vivalink;

public class Hospitals {
    private String hospitalId;
    private String hospitalName;
    private String city;
    private String email;
    private String contactPhone;

    public Hospitals() {}

    public Hospitals(String hospitalId, String hospitalName, String city, String email, String contactPhone) {
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.city = city;
        this.email = email;
        this.contactPhone = contactPhone;
    }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    @Override
    public String toString() {
        return "Hospitals{" +
                "hospitalId='" + hospitalId + '\'' +
                ", hospitalName='" + hospitalName + '\'' +
                ", city='" + city + '\'' +
                ", email='" + email + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                '}';
    }
}
