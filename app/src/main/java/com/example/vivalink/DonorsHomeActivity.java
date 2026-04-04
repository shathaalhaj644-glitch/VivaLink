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


    private String urgentBlood = "", urgentHospital = "", urgentCity = "", urgentDept = "", urgentUnits = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donors_home);


        initViews();

        currentDonorId = FirebaseAuth.getInstance().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference();

        if (currentDonorId != null) {
            loadDonorData();
            loadUrgentRequest();
        }


        btnGoToDonate.setOnClickListener(v -> {
            if (!urgentHospital.isEmpty()) {
                Intent intent = new Intent(DonorsHomeActivity.this, DonateActivity.class);
                intent.putExtra("hospitalName", urgentHospital);
                intent.putExtra("bloodType", urgentBlood);
                intent.putExtra("city", urgentCity);
                intent.putExtra("department", urgentDept);
                intent.putExtra("units", urgentUnits);
                startActivity(intent);
            } else {
                Toast.makeText(this, "لا يوجد طلبات عاجلة حالياً للتبرع لها", Toast.LENGTH_SHORT).show();
            }
        });


        btnViewRequests.setOnClickListener(v -> startActivity(new Intent(this, RequestsActivity.class)));
        btnGoToProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void initViews() {
        tvWelcomeDonor = findViewById(R.id.tvWelcomeDonor);
        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvBloodType = findViewById(R.id.tvBloodType);
        tvUnits = findViewById(R.id.tvUnits);
        tvLastDonationDate = findViewById(R.id.tvLastDonationDate);
        tvDonationCount = findViewById(R.id.tvDonationCount);
        btnGoToDonate = findViewById(R.id.btnGoToDonate);
        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnGoToProfile = findViewById(R.id.btnGoToProfile);
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

        dbRef.child("Requests").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.hasChildren()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {

                                urgentHospital = ds.child("hospitalName").getValue(String.class);
                                urgentBlood = ds.child("bloodType").getValue(String.class);
                                urgentCity = ds.child("city").getValue(String.class);
                                urgentDept = ds.child("department").getValue(String.class);
                                urgentUnits = String.valueOf(ds.child("units").getValue());


                                tvHospitalName.setText("المستشفى: " + (urgentHospital != null ? urgentHospital : "غير محدد"));
                                tvBloodType.setText("الفصيلة المطلوبة: " + (urgentBlood != null ? urgentBlood : "--"));
                                tvUnits.setText("عدد الوحدات: " + (urgentUnits != null ? urgentUnits : "0"));
                            }
                        } else {
                            tvHospitalName.setText("لا يوجد طلبات حالياً");
                            urgentHospital = "";
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", error.getMessage());
                    }
                });
    }
}