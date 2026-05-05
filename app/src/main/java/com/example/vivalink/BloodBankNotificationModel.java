package com.example.vivalink;

public class BloodBankNotificationModel {

    // 🔹 الحقول الأساسية
    private String notificationId;
    private String title;
    private String message;
    private String hospitalId; // 👈 المعرف الخاص بالمستشفى صاحب الطلب
    private String type;

    // 🔹 الفلترة والتحكم
    private String targetType;      // DONOR أو ADMIN
    private String targetUserId;    // للمستشفى (ADMIN)
    private String userId;          // 🔥 للمستخدم (DONOR - إشعارات شخصية)

    private String city;            // للإشعارات العامة
    private String bloodType;       // للإشعارات العامة

    private String hospitalName;

    private long createdAt;         // وقت الإرسال
    private boolean isRead;         // مقروء أو لا

    // 🔹 Constructor فارغ (مهم لـ Firebase)
    public BloodBankNotificationModel() {}

    // =========================
    // 🔹 Getters & Setters
    // =========================

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    // 🔥 أهم إضافة (الإشعارات الشخصية)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}