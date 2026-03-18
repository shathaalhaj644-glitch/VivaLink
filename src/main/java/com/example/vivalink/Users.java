package com.example.vivalink; // تأكدي إن هاد السطر يطابق مكان الملف عندك

public class Users {
    // تعريف البيانات الأساسية للمستخدم
    public String fullName;
    public String email;
    public String phoneNumber;
    public String bloodGroup; // فصيلة الدم
    public String id;         // معرف المستخدم في فايربيس (UID)

    // 1. Constructor فاضي (ضروري جداً لعمل مكتبة Firebase Database)
    public Users() {
    }

    // 2. Constructor لتعبئة البيانات عند تسجيل مستخدم جديد
    public Users(String id, String fullName, String email, String phoneNumber, String bloodGroup) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.bloodGroup = bloodGroup;
    }

    // 3. Getters & Setters (اختياري بس يفضل وجودهم للاحترافية)
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