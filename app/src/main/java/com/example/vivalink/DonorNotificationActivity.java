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

                        String cleanMyCity = normalizeArabic(myCity);
                        String cleanNotifCity = normalizeArabic(n.getCity());

                        if (cleanMyCity.equals(cleanNotifCity)) {
                            if (myBloodType != null && n.getBloodType() != null &&
                                    myBloodType.trim().equalsIgnoreCase(n.getBloodType().trim())) {

                                checkDonationEligibilityAndAdd(n, fourMonthsMillis, currentTime);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("VivaLink", "Donor Notif Error: " + e.getMessage());
                    }
                }
                // تحديث الواجهة في حال كانت القائمة فارغة
                if (snapshot.getChildrenCount() == 0) {
                    tvNoNotifications.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // تم دمج الدالتين في دالة واحدة صحيحة لمنع تكرار الـ Error
    private void checkDonationEligibilityAndAdd(BloodBankNotificationModel n, long period, long now) {
        dbRef.child("Donors").child(myId).child("lastDonationDate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isEligible = true;

                Object value = snapshot.getValue();
                if (value instanceof Long) {
                    if (now - (Long) value < period) isEligible = false;
                } else if (value instanceof String) {
                    String dateStr = (String) value;
                    if (!dateStr.isEmpty()) {
                        // يمكنك هنا إضافة منطق لتحويل الـ String لـ Long إذا أردتِ
                        // حالياً سنعتبره مؤهل إذا كان الحقل فارغاً
                    }
                }

                if (isEligible) {
                    if (!list.contains(n)) {
                        list.add(0, n);
                        adapter.notifyDataSetChanged();
                    }
                }
                tvNoNotifications.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private String normalizeArabic(String text) {
        if (text == null) return "";
        return text.trim().replace(" ", "").replace("ة", "ه").replace("أ", "ا").replace("إ", "ا").replace("آ", "ا");
    }
}