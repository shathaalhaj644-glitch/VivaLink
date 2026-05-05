package com.example.vivalink;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
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

        if (myId != null) { fetchDonorDetailsAndLoadNotifications(); }
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

                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        BloodBankNotificationModel n = ds.getValue(BloodBankNotificationModel.class);

                        if (n == null || !"DONOR".equals(n.getTargetType())) continue;

                        // ✅ 1. إشعارات شخصية (مهم)
                        if (n.getUserId() != null && n.getUserId().equals(myId)) {
                            list.add(0, n);
                            continue;
                        }

                        // ✅ 2. إشعارات عامة حسب المدينة والفصيلة
                        if (n.getCity() != null && n.getBloodType() != null) {

                            String cleanMyCity = normalizeArabic(myCity);
                            String cleanNotifCity = normalizeArabic(n.getCity());

                            boolean cityMatch = cleanMyCity.equals(cleanNotifCity);
                            boolean bloodMatch = myBloodType != null && myBloodType.equals(n.getBloodType());

                            if (cityMatch && bloodMatch) {
                                list.add(0, n);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                adapter.notifyDataSetChanged();
                tvNoNotifications.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // دالة توحيد الحروف العربية (مهمة جداً لنجاح المقارنة)
    private String normalizeArabic(String text) {
        if (text == null) return "";
        return text.trim().replace(" ", "").replace("ة", "ه").replace("أ", "ا").replace("إ", "ا").replace("آ", "ا");
    } }