package com.example.vivalink;

public class BloodBankNotificationModel {
    private String notificationId;
    private String title;
    private String message;
    private String type;
    private String targetType; // الحقل المسبب للخطأ سابقاً
    private String targetUserId; // الحقل المسبب للخطأ سابقاً
    private long createdAt;
    private boolean isRead;

    // Constructor فارغ مطلوب للفايربيس
    public BloodBankNotificationModel() {}

    // Getters and Setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public String getTargetUserId() { return targetUserId; }
    public void setTargetUserId(String targetUserId) { this.targetUserId = targetUserId; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}