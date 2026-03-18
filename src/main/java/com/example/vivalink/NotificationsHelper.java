package com.example.vivalink;

import com.example.vivalink.Notifications;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class NotificationsHelper {

    private DatabaseReference dbRef;

    public NotificationsHelper() {
        // إنشاء مرجع لجدول الإشعارات في Firebase تحت اسم "Notifications"
        dbRef = FirebaseDatabase.getInstance().getReference("Notifications");
    }

    // --- 1. عمليات قاعدة البيانات الأساسية (CRUD) ---

    // إضافة إشعار جديد ✅
    public Task<Void> addNotification(Notifications notification) {
        return dbRef.child(notification.getNotificationId()).setValue(notification);
    }

    // جلب كل الإشعارات ✅
    public Query getAllNotifications() {
        return dbRef;
    }

    // جلب إشعار محدد باستخدام الـ ID ✅
    public Query getNotificationById(String notificationId) {
        return dbRef.child(notificationId);
    }

    // تحديث حالة القراءة للإشعار ✅
    public Task<Void> markAsRead(String notificationId) {
        return dbRef.child(notificationId).child("isRead").setValue(true);
    }

    // تحديث نص أو عنوان الإشعار ✅
    public Task<Void> updateNotification(String notificationId, Notifications updatedNotification) {
        return dbRef.child(notificationId).setValue(updatedNotification);
    }

    // حذف إشعار ✅
    public Task<Void> deleteNotification(String notificationId) {
        return dbRef.child(notificationId).removeValue();
    }

    // --- 2. عمليات البحث المتقدم (Advanced Search) ---

    // جلب الإشعارات حسب المستخدم 📌
    public Query getNotificationsByUser(String userId) {
        return dbRef.orderByChild("userId").equalTo(userId);
    }

    // جلب الإشعارات غير المقروءة فقط 🔔
    public Query getUnreadNotifications(String userId) {
        return dbRef.orderByChild("userId").equalTo(userId);
        // بعدين في onDataChange تفحصي isRead == false
    }

    // --- 3. منطق الفحص والتنسيق (Logic & Validation) ---

    // التحقق من صحة بيانات الإشعار قبل رفعه ✅
    public static String validateNotification(String title, String message, String userId) {
        if (userId == null || userId.trim().isEmpty()) return "رقم المستخدم مطلوب ✅";
        if (title == null || title.trim().isEmpty()) return "عنوان الإشعار مطلوب ✅";
        if (message == null || message.trim().isEmpty()) return "نص الإشعار مطلوب ✅";

        return null; // البيانات سليمة
    }

    // دالة لتنسيق حالة القراءة بالعربي ✅
    public static String getReadStatusLabel(boolean isRead) {
        return isRead ? "مقروء ✅" : "غير مقروء 🔔";
    }
}
