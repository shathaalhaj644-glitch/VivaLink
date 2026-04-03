package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class DonorsHomeActivity extends AppCompatActivity {

    private TextView tvWelcomeDonor, tvHospitalName, tvBloodType, tvUnits, tvLastDonationDate, tvDonationCount;
    private Button btnGoToDonate, btnViewRequests, btnGoToProfile;
    private DatabaseReference dbRef;
    private String currentDonorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donors_home);

        // ربط العناصر
        tvWelcomeDonor = findViewById(R.id.tvWelcomeDonor);
        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvBloodType = findViewById(R.id.tvBloodType);
        tvUnits = findViewById(R.id.tvUnits);
        tvLastDonationDate = findViewById(R.id.tvLastDonationDate);
        tvDonationCount = findViewById(R.id.tvDonationCount);
        btnGoToDonate = findViewById(R.id.btnGoToDonate);
        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnGoToProfile = findViewById(R.id.btnGoToProfile);

        currentDonorId = FirebaseAuth.getInstance().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference();

        if (currentDonorId != null) {
            loadDonorData();
            loadUrgentRequest();
        }

        // الأزرار والتنقل
        btnGoToDonate.setOnClickListener(v -> startActivity(new Intent(this, DonateActivity.class)));
        btnViewRequests.setOnClickListener(v -> startActivity(new Intent(this, RequestsActivity.class)));

        // تعديل: الانتقال لصفحة البروفايل (تأكدي من اسم الملف عندك)
        btnGoToProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void loadDonorData() {
        dbRef.child("Donors").child(currentDonorId).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("fullName").getValue(String.class);
                    String last = snapshot.child("lastDonation").getValue(String.class);
                    Object count = snapshot.child("donationCount").getValue();

                    tvWelcomeDonor.setText("👋 أهلاً " + name + "!");
                    tvLastDonationDate.setText(last != null ? last : "--");
                    tvDonationCount.setText(count != null ? String.valueOf(count) : "0");
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadUrgentRequest() {
        // 🔥 الحل: نجيب آخر طلب تم إضافته لضمان ظهوره دائماً
        dbRef.child("Requests").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.hasChildren()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                tvHospitalName.setText("المستشفى: " + ds.child("hospitalName").getValue(String.class));
                                tvBloodType.setText("الفصيلة المطلوبة: " + ds.child("bloodType").getValue(String.class));
                                tvUnits.setText("عدد الوحدات: " + ds.child("units").getValue(String.class));
                            }
                        } else {
                            tvHospitalName.setText("لا يوجد طلبات حالياً");
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}