package com.example.vivalink;

public class Notifications {
    private String notificationId;
    private String userId;      // هذا الحقل الأساسي في كودك
    private String targetType;  // DONOR أو ADMIN أو HOSPITAL
    private String title;
    private String message;
    private String type;        // نوع الإشعار (new_test, emergency, etc.)
    private boolean isRead;
    private String createdAt;

    // Constructor فارغ مطلوب للفايربيس
    public Notifications() {}

    public Notifications(String notificationId, String userId, String title, String message, boolean isRead, String createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}