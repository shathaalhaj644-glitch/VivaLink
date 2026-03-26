package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class DonorsHomeActivity extends AppCompatActivity {

    private TextView tvWelcomeDonor;
    // تم حذف tvDonationCount و tvLastDonationDate لأنهم مش بالـ XML
    private TextView tvUrgentHospital, tvUrgentBlood, tvUrgentUnits;
    private Button btnViewRequests, btnGoToProfile, btnGoToDonate;
    private DatabaseReference dbRef;
    private String userId, userCity;
    private BloodRequests currentUrgentRequest;

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
            navigateToLogin();
            return;
        }

        setupClickListeners();
    }

    private void initViews() {
        try {
            tvWelcomeDonor = findViewById(R.id.tvWelcomeDonor);
            tvUrgentHospital = findViewById(R.id.tvHospitalName); // ID المستشفى بالـ XML
            tvUrgentBlood = findViewById(R.id.tvBloodType);     // ID الفصيلة بالـ XML
            tvUrgentUnits = findViewById(R.id.tvUnits);         // ID عدد الوحدات بالـ XML

            btnViewRequests = findViewById(R.id.btnViewRequests);
            btnGoToProfile = findViewById(R.id.btnGoToProfile); // تم تعديل الـ ID ليتطابق مع الـ XML
            btnGoToDonate = findViewById(R.id.btnGoToDonate);

        } catch (Exception e) {
            Log.e("Vivalink_Error", "initViews: " + e.getMessage());
        }
    }

    private void setupClickListeners() {
        if (btnViewRequests != null)
            btnViewRequests.setOnClickListener(v -> startActivity(new Intent(this, RequestsActivity.class)));

        // حل مشكلة زر البروفايل
        if (btnGoToProfile != null)
            btnGoToProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        if (btnGoToDonate != null) {
            btnGoToDonate.setOnClickListener(v -> {
                if (currentUrgentRequest != null) {
                    Intent intent = new Intent(this, DonateActivity.class);
                    // تمرير كل البيانات المطلوبة عشان ما تطلع null في صفحة التبرع
                    intent.putExtra("bloodType", currentUrgentRequest.getBloodType());
                    intent.putExtra("hospitalName", currentUrgentRequest.getHospitalName());
                    intent.putExtra("city", currentUrgentRequest.getCity());
                    intent.putExtra("department", currentUrgentRequest.getDepartment());
                    intent.putExtra("units", currentUrgentRequest.getUnits());
                    intent.putExtra("time", currentUrgentRequest.getTime());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "لا يوجد طلب عاجل حالياً", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadDonorData() {
        dbRef.child("Donors").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("fullName").getValue(String.class);
                    userCity = snapshot.child("city").getValue(String.class);

                    if (tvWelcomeDonor != null)
                        tvWelcomeDonor.setText("👋 أهلاً بك، " + (name != null ? name : "متبرع"));

                    // تحميل الطلبات العاجلة بناءً على مدينة المتبرع (الفلترة)
                    if (userCity != null) {
                        loadUrgentRequestNearMe(userCity);
                    }
                }
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    private void loadUrgentRequestNearMe(String city) {
        Query urgentQuery = dbRef.child("Requests").orderByChild("city").equalTo(city).limitToFirst(1);
        urgentQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        currentUrgentRequest = data.getValue(BloodRequests.class);
                        if (currentUrgentRequest != null) {
                            if (tvUrgentHospital != null) tvUrgentHospital.setText(currentUrgentRequest.getHospitalName());
                            if (tvUrgentBlood != null) tvUrgentBlood.setText(currentUrgentRequest.getBloodType());
                            // تعديل عرض الموقع ليظهر عدد الوحدات بدلاً منه
                            if (tvUrgentUnits != null) tvUrgentUnits.setText(currentUrgentRequest.getUnits());
                        }
                    }
                } else {
                    // في حال عدم وجود طلبات في نفس المدينة
                    if (tvUrgentHospital != null) tvUrgentHospital.setText("لا توجد طلبات في " + city);
                }
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}