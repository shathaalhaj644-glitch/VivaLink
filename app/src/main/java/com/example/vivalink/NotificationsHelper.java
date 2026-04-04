package com.example.vivalink;

import com.example.vivalink.Notifications;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class NotificationsHelper {

    private DatabaseReference dbRef;

    public NotificationsHelper() {

        dbRef = FirebaseDatabase.getInstance().getReference("Notifications");
    }


    public Task<Void> addNotification(Notifications notification) {
        return dbRef.child(notification.getNotificationId()).setValue(notification);
    }


    public Query getAllNotifications() {
        return dbRef;
    }


    public Query getNotificationById(String notificationId) {
        return dbRef.child(notificationId);
    }


    public Task<Void> markAsRead(String notificationId) {
        return dbRef.child(notificationId).child("isRead").setValue(true);
    }


    public Task<Void> updateNotification(String notificationId, Notifications updatedNotification) {
        return dbRef.child(notificationId).setValue(updatedNotification);
    }


    public Task<Void> deleteNotification(String notificationId) {
        return dbRef.child(notificationId).removeValue();
    }

    public Query getNotificationsByUser(String userId) {
        return dbRef.orderByChild("userId").equalTo(userId);
    }


    public Query getUnreadNotifications(String userId) {
        return dbRef.orderByChild("userId").equalTo(userId);

    }

    public static String validateNotification(String title, String message, String userId) {
        if (userId == null || userId.trim().isEmpty()) return "رقم المستخدم مطلوب ✅";
        if (title == null || title.trim().isEmpty()) return "عنوان الإشعار مطلوب ✅";
        if (message == null || message.trim().isEmpty()) return "نص الإشعار مطلوب ✅";

        return null;
    }


    public static String getReadStatusLabel(boolean isRead) {
        return isRead ? "مقروء ✅" : "غير مقروء 🔔";
    }
}
