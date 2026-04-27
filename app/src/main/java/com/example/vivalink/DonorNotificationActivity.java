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
                        if (n != null) {
                            // 1. إذا كان الإشعار موجهاً للمتبرع الحالي بشكل خاص (عن طريق الـ ID)
                            if (myId != null && myId.equals(n.getTargetUserId())) {
                                list.add(0, n);
                            }
                            // 2. إذا كان إشعاراً عاماً للمتبرعين (يجب أن تطابق المدينة والفصيلة)
                            else if ("DONOR".equals(n.getTargetType())) {

                                // المقارنة البرمجية الصحيحة بدل البحث في النص
                                boolean cityMatch = myCity != null && myCity.equalsIgnoreCase(n.getCity());
                                boolean bloodMatch = myBloodType != null && myBloodType.equals(n.getBloodType());

                                if (cityMatch && bloodMatch) {
                                    list.add(0, n);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();

                // إظهار نص "لا يوجد إشعارات" إذا كانت القائمة فارغة
                if (list.isEmpty()) {
                    tvNoNotifications.setVisibility(View.VISIBLE);
                } else {
                    tvNoNotifications.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DonorNotificationActivity.this, "خطأ في التحميل: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    } }