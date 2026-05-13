package com.example.vivalink;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.Random;

public class NotificationService extends Service {

    private long serviceStartTime;

    @Override
    public void onCreate() {
        super.onCreate();

        // وقت تشغيل الخدمة لمنع الإشعارات القديمة
        serviceStartTime = System.currentTimeMillis();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String uid = FirebaseAuth.getInstance().getUid();

        if (uid == null) return START_STICKY;

        FirebaseDatabase.getInstance().getReference("Notifications")
                .addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot,
                                             @Nullable String previousChildName) {

                        Boolean isRead =
                                snapshot.child("isRead").getValue(Boolean.class);

                        Long createdAt =
                                snapshot.child("createdAt").getValue(Long.class);

                        String targetType =
                                snapshot.child("targetType").getValue(String.class);

                        String targetUserId =
                                snapshot.child("targetUserId").getValue(String.class);

                        // فقط الإشعارات الجديدة وغير المقروءة
                        if (isRead != null
                                && !isRead
                                && createdAt != null
                                && createdAt >= serviceStartTime) {

                            boolean shouldShow = false;

                            // ==================================================
                            // 1. إشعارات الموظف / المستشفى
                            // ==================================================

                            if (uid.equals(targetUserId)) {

                                shouldShow = true;
                            }

                            // ==================================================
                            // 2. إشعارات المتبرعين
                            // ==================================================

                            else if ("DONOR".equals(targetType)
                                    && (targetUserId == null
                                    || targetUserId.isEmpty())) {

                                String notifBlood =
                                        snapshot.child("bloodType")
                                                .getValue(String.class);

                                String notifCity =
                                        snapshot.child("city")
                                                .getValue(String.class);

                                FirebaseDatabase.getInstance()
                                        .getReference("Donors")
                                        .child(uid)
                                        .addListenerForSingleValueEvent(
                                                new ValueEventListener() {

                                                    @Override
                                                    public void onDataChange(
                                                            @NonNull DataSnapshot donorSnap) {

                                                        String donorBlood =
                                                                donorSnap.child("bloodType")
                                                                        .getValue(String.class);

                                                        String donorCity =
                                                                donorSnap.child("city")
                                                                        .getValue(String.class);

                                                        boolean bloodMatch =
                                                                donorBlood != null
                                                                        && notifBlood != null
                                                                        && donorBlood.trim()
                                                                        .equalsIgnoreCase(
                                                                                notifBlood.trim());

                                                        boolean cityMatch =
                                                                donorCity != null
                                                                        && notifCity != null
                                                                        && donorCity.trim()
                                                                        .equalsIgnoreCase(
                                                                                notifCity.trim());

                                                        if (bloodMatch && cityMatch) {

                                                            String title =
                                                                    snapshot.child("title")
                                                                            .getValue(String.class);

                                                            String message =
                                                                    snapshot.child("message")
                                                                            .getValue(String.class);

                                                            showNotification(title, message);

                                                            snapshot.getRef()
                                                                    .child("isRead")
                                                                    .setValue(true);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(
                                                            @NonNull DatabaseError error) {

                                                    }
                                                });
                            }

                            // ==================================================
                            // إشعارات الموظفين فقط
                            // ==================================================

                            if (shouldShow) {

                                String title =
                                        snapshot.child("title")
                                                .getValue(String.class);

                                String message =
                                        snapshot.child("message")
                                                .getValue(String.class);

                                showNotification(title, message);

                                snapshot.getRef()
                                        .child("isRead")
                                        .setValue(true);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot,
                                               @Nullable String previousChildName) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot,
                                             @Nullable String previousChildName) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        return START_STICKY;
    }

    private void showNotification(String title, String message) {

        String channelId = "vivalink_urgent_channel";

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (manager == null) return;

        Uri alarmSound =
                RingtoneManager.getDefaultUri(
                        RingtoneManager.TYPE_NOTIFICATION);

        // ==================================================
        // Android 8+
        // ==================================================

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            channelId,
                            "VivaLink Notifications",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);

            AudioAttributes audioAttributes =
                    new AudioAttributes.Builder()
                            .setContentType(
                                    AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(
                                    AudioAttributes.USAGE_NOTIFICATION)
                            .build();

            channel.setSound(alarmSound, audioAttributes);

            manager.createNotificationChannel(channel);
        }

        // ==================================================
        // فتح التطبيق عند الضغط على الإشعار
        // ==================================================

        Intent intent = new Intent(this, LoginActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int pendingFlags =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        ? PendingIntent.FLAG_IMMUTABLE
                        | PendingIntent.FLAG_UPDATE_CURRENT
                        : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        pendingFlags
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSound(alarmSound)
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentIntent(pendingIntent);

        manager.notify(
                new Random().nextInt(100000),
                builder.build()
        );
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}