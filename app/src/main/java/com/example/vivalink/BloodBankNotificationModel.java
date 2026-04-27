package com.example.vivalink;

public class BloodBankNotificationModel {
    private String notificationId, title, message, type, targetType, targetUserId;
    private String city, bloodType, hospitalName; // تأكدي من وجود bloodType هنا
    private long createdAt;
    private boolean isRead;

    public BloodBankNotificationModel() {}

    // الدوال الضرورية التي يطلبها الكود
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String id) { this.notificationId = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public String getTargetUserId() { return targetUserId; }
    public void setTargetUserId(String targetUserId) { this.targetUserId = targetUserId; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}