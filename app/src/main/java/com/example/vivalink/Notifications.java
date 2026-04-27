package com.example.vivalink;

public class Notifications {
    private String notificationId, title, message, type, targetType, userId;
    private Object createdAt; // Object عشان يقبل String أو Long بدون ما يطفي التطبيق
    private boolean isRead;

    public Notifications() {} // ضروري جداً

    public String getNotificationId() { return notificationId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public String getTargetType() { return targetType; }
    public String getUserId() { return userId; }
    public Object getCreatedAt() { return createdAt; }
    public boolean isRead() { return isRead; }
}