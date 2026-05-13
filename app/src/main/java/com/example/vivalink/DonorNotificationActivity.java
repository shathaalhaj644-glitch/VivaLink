package com.example.vivalink;

import android.os.Bundle;
import android.util.Log; // تم إضافة هذا السطر لحل مشكلة Log
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;

public class DonorNotificationActivity extends AppCompatActivity {

    private RecyclerView rv;
    private BloodBankNotificationAdapter adapter;
    private List<BloodBankNotificationModel> list = new ArrayList<>();
    private DatabaseReference dbRef;
    private String myId, myBloodType, myCity;
    private TextView tvNoNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_notification);

        rv = findViewById(R.id.rvNotifications);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BloodBankNotificationAdapter(list);
        rv.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference();
        myId = FirebaseAuth.getInstance().getUid();

        if (myId != null) {
            fetchDonorDetailsAndLoadNotifications();
        }
    }

    private void fetchDonorDetailsAndLoadNotifications() {
        dbRef.child("Donors").child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    myBloodType = snapshot.child("bloodType").getValue(String.class);
                    myCity = snapshot.child("city").getValue(String.class);
                    loadFilteredNotifications();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadFilteredNotifications() {
        dbRef.child("Notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                long fourMonthsMillis = 4L * 30 * 24 * 60 * 60 * 1000;
                long currentTime = System.currentTimeMillis();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        BloodBankNotificationModel n = ds.getValue(BloodBankNotificationModel.class);
                        if (n == null || !"DONOR".equals(n.getTargetType())) continue;

                        // -------------------------------------------------------
                        // 1. الجزء الخاص بإشعارات "نتائج الفحص" (قبول/رفض)
                        // -------------------------------------------------------
                        if ("test_result".equals(n.getType())) {
                            // يظهر فقط للمتبرع المعني (صاحب الفحص)
                            if (myId != null && myId.equals(n.getTargetUserId())) {
                                if (!list.contains(n)) {
                                    list.add(0, n);
                                }
                            }
                            continue; // ننتقل للإشعار التالي ولا نطبق شروط المدينة والفصيلة هنا
                        }

                        // -------------------------------------------------------
                        // 2. الجزء الخاص بـ "طلبات الدم العاجلة" (كود الموظف)
                        // -------------------------------------------------------
                        // هذا الجزء يبقى كما هو لضمان وصول طلبات الموظف حسب الفصيلة والمدينة
                        if ("urgent_request".equals(n.getType())) {
                            String cleanMyCity = normalizeArabic(myCity);
                            String cleanNotifCity = normalizeArabic(n.getCity());

                            if (cleanMyCity.equals(cleanNotifCity)) {
                                // فحص مطابقة فصيلة الدم
                                if (myBloodType != null && n.getBloodType() != null &&
                                        myBloodType.trim().equalsIgnoreCase(n.getBloodType().trim())) {

                                    // فحص أهلية المتبرع (مرور 4 شهور)
                                    checkDonationEligibilityAndAdd(n, fourMonthsMillis, currentTime);
                                }
                            }
                        }

                    } catch (Exception e) {
                        Log.e("VivaLink", "Donor Notif Error: " + e.getMessage());
                    }
                }
                adapter.notifyDataSetChanged();
                tvNoNotifications.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    // تم دمج الدالتين في دالة واحدة صحيحة لمنع تكرار الـ Error
    private void checkDonationEligibilityAndAdd(BloodBankNotificationModel n, long period, long now) {
        // استخدمنا lastDonation لأنه الحقل المعتمد في بيانات المتبرع عندك
        dbRef.child("Donors").child(myId).child("lastDonation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isEligible = true;
                String lastDateStr = snapshot.getValue(String.class);

                // إذا كان المتبرع قد تبرع سابقاً (الحقل ليس فارغاً وليس --)
                if (lastDateStr != null && !lastDateStr.isEmpty() && !lastDateStr.equals("--")) {
                    try {
                        // تحويل النص إلى تاريخ لمقارنته
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ENGLISH);
                        // تنظيف النص من أي أرقام عربية أو رموز غريبة
                        String cleanDate = lastDateStr.replace("٠","0").replace("١","1").replace("٢","2")
                                .replace("٣","3").replace("٤","4").replace("٥","5")
                                .replace("٦","6").replace("٧","7").replace("٨","8")
                                .replace("٩","9").replace("-","/");

                        java.util.Date lastDonationDate = sdf.parse(cleanDate);
                        long diff = now - lastDonationDate.getTime();

                        // إذا كان الفرق أصغر من 120 يوم (period) فهو غير مؤهل
                        if (diff < period) {
                            isEligible = false;
                        }
                    } catch (Exception e) {
                        // في حال حدث خطأ في التاريخ، نعتبره مؤهل احتياطاً لكي لا يضيع عليه الطلب
                        isEligible = true;
                    }
                }

                // إذا كان مؤهلاً (مر 4 شهور أو لم يتبرع أبداً) نُظهر الإشعار
                if (isEligible) {
                    if (!list.contains(n)) {
                        list.add(0, n);
                        adapter.notifyDataSetChanged();
                    }
                }
                tvNoNotifications.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private String normalizeArabic(String text) {
        if (text == null) return "";
        return text.trim().replace(" ", "").replace("ة", "ه").replace("أ", "ا").replace("إ", "ا").replace("آ", "ا");
    }
}