package com.example.vivalink;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;

public class NotificationsHelper {

    private DatabaseReference dbRef;
    public static final String CHANNEL_ID = "vivalink_channel";

    public NotificationsHelper() {
        dbRef = FirebaseDatabase.getInstance().getReference("Notifications");
    }

    // إضافة إشعار جديد
    public Task<Void> addNotification(Notifications notification) {
        return dbRef.child(notification.getNotificationId()).setValue(notification);
    }

    // تحويل الحالة لمقروء
    public Task<Void> markAsRead(String notificationId) {
        return dbRef.child(notificationId).child("isRead").setValue(true);
    }

    // نص الحالة (مقروء/غير مقروء) بدون صور
    public String getReadStatusLabel(boolean isRead) {
        return isRead ? "Status: Read" : "Status: Unread";
    }

    // إظهار الإشعار باستخدام أيقونة النظام الافتراضية
    public void showSystemNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "VivaLink Alerts", NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent intent = new Intent(context, DonorNotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                // استخدام أيقونة النظام الافتراضية لتجنب خطأ Drawable
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}