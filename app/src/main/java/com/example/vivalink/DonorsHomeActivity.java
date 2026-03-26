package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class DonorsHomeActivity extends AppCompatActivity {

    private TextView tvWelcomeDonor, tvDonationCount, tvLastDonationDate;
    private TextView tvUrgentHospital, tvUrgentBlood, tvUrgentCity;
    private Button btnViewRequests, btnGoToProfile, btnGoToDonate;
    private DatabaseReference dbRef;
    private String userId, userCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donors_home);

        initViews();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            dbRef = FirebaseDatabase.getInstance().getReference();
            loadDonorData();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // البرمجة للأزرار
        btnViewRequests.setOnClickListener(v -> startActivity(new Intent(this, RequestsActivity.class)));
        btnGoToProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnGoToDonate.setOnClickListener(v -> startActivity(new Intent(this, DonateActivity.class)));
    }

    private void initViews() {
        tvWelcomeDonor = findViewById(R.id.tvWelcomeDonor);
        tvDonationCount = findViewById(R.id.tvDonationCount);
        tvLastDonationDate = findViewById(R.id.tvLastDonationDate);
        tvUrgentHospital = findViewById(R.id.tvHospitalName);
        tvUrgentBlood = findViewById(R.id.tvBloodType);
        tvUrgentCity = findViewById(R.id.tvUrgentCity);
        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnGoToProfile = findViewById(R.id.btnGoToProfile);
        btnGoToDonate = findViewById(R.id.btnGoToDonate);
    }

    private void loadDonorData() {
        dbRef.child("Donors").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // قراءة آمنة لكل القيم لتجنب الـ Crash
                    String name = String.valueOf(snapshot.child("fullName").getValue());
                    userCity = String.valueOf(snapshot.child("city").getValue());
                    String lastDate = String.valueOf(snapshot.child("lastDonation").getValue());
                    String count = String.valueOf(snapshot.child("donationCount").getValue());

                    tvWelcomeDonor.setText("👋 أهلاً بك، " + (name.equals("null") ? "متبرع" : name) + "!");
                    tvDonationCount.setText(count.equals("null") ? "0" : count);
                    tvLastDonationDate.setText(lastDate.equals("null") ? "--" : lastDate);

                    if (userCity != null && !userCity.equals("null")) {
                        loadUrgentRequestNearMe(userCity);
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadUrgentRequestNearMe(String city) {
        Query urgentQuery = dbRef.child("Requests").orderByChild("city").equalTo(city).limitToFirst(1);
        urgentQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        tvUrgentHospital.setText("المستشفى: " + data.child("hospitalName").getValue());
                        tvUrgentBlood.setText("الفصيلة المطلوبة: " + data.child("bloodType").getValue());
                        tvUrgentCity.setText("الموقع: " + data.child("city").getValue());
                    }
                } else {
                    tvUrgentHospital.setText("لا يوجد طلبات عاجلة في مدينتك حالياً");
                    tvUrgentCity.setText("الموقع: " + city);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}