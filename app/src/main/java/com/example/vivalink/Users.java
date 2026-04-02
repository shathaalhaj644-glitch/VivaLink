package com.example.vivalink;

public class Users {

    public String fullName;
    public String email;
    public String phoneNumber;
    public String bloodGroup;
    public String id;


    public Users() {
    }


    public Users(String id, String fullName, String email, String phoneNumber, String bloodGroup) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.bloodGroup = bloodGroup;
    }


    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}