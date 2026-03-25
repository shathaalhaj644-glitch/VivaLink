package com.example.vivalink;

public class Notifications {
    private String notificationId;   // PK: رقم الإشعار
    private String userId;           // FK: المستخدم المستهدف
    private String title;            // عنوان الإشعار
    private String message;          // نص الإشعار
    private boolean isRead;          // هل تمت قراءة الإشعار
    private String createdAt;        // وقت إنشاء الإشعار

    // 1. Constructor فارغ (إجباري للفايربيس)
    public Notifications() {}

    // 2. Constructor كامل
    public Notifications(String notificationId, String userId, String title,
                         String message, boolean isRead, String createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // 3. Getters & Setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // 4. toString() لسهولة الطباعة أثناء الـ Debugging
    @Override
    public String toString() {
        return "Notifications{" +
                "notificationId='" + notificationId + '\'' +
                ", userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
