package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class DonorsHomeActivity extends AppCompatActivity {

    private TextView tvWelcomeDonor, tvHospitalName, tvBloodType, tvUnits, tvLastDonationDate, tvDonationCount;
    private Button btnGoToDonate, btnViewRequests, btnGoToProfile;
    private DatabaseReference dbRef;
    private String currentDonorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donors_home);

        // ربط العناصر مع الـ XML
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

        // زر التبرع الآن
        btnGoToDonate.setOnClickListener(v -> {
            Toast.makeText(this, "الانتقال لصفحة التبرع", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DonorsHomeActivity.this, DonateActivity.class));
        });

        // زر عرض جميع الطلبات
        btnViewRequests.setOnClickListener(v -> {
            startActivity(new Intent(DonorsHomeActivity.this, RequestsActivity.class));
        });

        // زر الرئيسية
        btnGoToProfile.setOnClickListener(v -> {
            startActivity(new Intent(DonorsHomeActivity.this, DonorsHomeActivity.class));
        });
    }

    private void loadDonorData() {
        dbRef.child("Donors").child(currentDonorId).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String lastDonation = snapshot.child("lastDonation").getValue(String.class);
                    Long donationCount = snapshot.child("donationCount").getValue(Long.class);

                    tvWelcomeDonor.setText("👋 أهلاً " + fullName + "! تبرعك قد ينقذ حياة");
                    tvLastDonationDate.setText(lastDonation != null ? lastDonation : "--");
                    tvDonationCount.setText(donationCount != null ? String.valueOf(donationCount) : "0");

                    Log.d("DonorsHome", "Donor: " + fullName + " | LastDonation: " + lastDonation + " | Count: " + donationCount);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DonorsHome", "خطأ في تحميل بيانات المتبرع: " + error.getMessage());
            }
        });
    }

    private void loadUrgentRequest() {
        dbRef.child("Requests").orderByChild("status").equalTo("طارئة جداً")
                .limitToFirst(1) // نجيب أول طلب عاجل
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                String hospitalName = ds.child("hospitalName").getValue(String.class);
                                String bloodType = ds.child("bloodType").getValue(String.class);
                                String units = ds.child("units").getValue(String.class);

                                tvHospitalName.setText("المستشفى: " + (hospitalName != null ? hospitalName : "--"));
                                tvBloodType.setText("الفصيلة المطلوبة: " + (bloodType != null ? bloodType : "--"));
                                tvUnits.setText("عدد الوحدات: " + (units != null ? units : "--"));

                                Log.d("DonorsHome", "Urgent Request: " + hospitalName + " | " + bloodType + " | " + units);
                            }
                        } else {
                            tvHospitalName.setText("لا يوجد طلبات عاجلة حالياً");
                            tvBloodType.setText("الفصيلة المطلوبة: --");
                            tvUnits.setText("عدد الوحدات: --");
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DonorsHome", "خطأ في تحميل الطلبات العاجلة: " + error.getMessage());
                    }
                });
    }
}
