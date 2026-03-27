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

    // نصوص الترحيب والإحصائيات
    private TextView tvWelcomeDonor, tvDonationCount, tvLastDonationDate;

    // نصوص كرت الطلب العاجل
    private TextView tvUrgentHospital, tvUrgentBlood, tvUrgentUnits;

    // الأزرار
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
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupClickListeners();
    }

    private void initViews() {
        try {
            tvWelcomeDonor = findViewById(R.id.tvWelcomeDonor);
            tvDonationCount = findViewById(R.id.tvDonationCount);
            tvLastDonationDate = findViewById(R.id.tvLastDonationDate);

            tvUrgentHospital = findViewById(R.id.tvHospitalName);
            tvUrgentBlood = findViewById(R.id.tvBloodType);
            tvUrgentUnits = findViewById(R.id.tvUnits);

            btnViewRequests = findViewById(R.id.btnViewRequests);
            btnGoToProfile = findViewById(R.id.btnGoToProfile);
            btnGoToDonate = findViewById(R.id.btnGoToDonate);

        } catch (Exception e) {
            Log.e("Vivalink_Error", "Error in initViews: " + e.getMessage());
        }
    }

    private void setupClickListeners() {
        if (btnViewRequests != null)
            btnViewRequests.setOnClickListener(v -> startActivity(new Intent(this, RequestsActivity.class)));

        if (btnGoToProfile != null)
            btnGoToProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        // --- تعديل المشكلة الأولى: السماح بالدخول حتى لو لم يوجد طلب ---
        if (btnGoToDonate != null) {
            btnGoToDonate.setOnClickListener(v -> {
                Intent intent = new Intent(this, DonateActivity.class);
                if (currentUrgentRequest != null) {
                    // إذا وجد طلب، نرسل البيانات
                    intent.putExtra("bloodType", currentUrgentRequest.getBloodType());
                    intent.putExtra("hospitalName", currentUrgentRequest.getHospitalName());
                    intent.putExtra("city", currentUrgentRequest.getCity());
                    intent.putExtra("department", currentUrgentRequest.getDepartment());
                    intent.putExtra("units", currentUrgentRequest.getUnits());
                    intent.putExtra("time", currentUrgentRequest.getTime());
                    intent.putExtra("status", currentUrgentRequest.getStatus());
                } else {
                    // إذا لم يوجد طلب، نرسل بيانات فارغة مرتبة لمنع الـ null
                    intent.putExtra("bloodType", "--");
                    intent.putExtra("hospitalName", "لا يوجد طلبات حالياً");
                    intent.putExtra("city", userCity != null ? userCity : "--");
                    intent.putExtra("units", "0");
                }
                startActivity(intent);
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

                    Object count = snapshot.child("donationCount").getValue();
                    String lastDate = snapshot.child("lastDonation").getValue(String.class);

                    if (tvWelcomeDonor != null)
                        tvWelcomeDonor.setText("👋 أهلاً بك، " + (name != null ? name : "متبرع"));

                    if (tvDonationCount != null)
                        tvDonationCount.setText(String.valueOf(count != null ? count : "0"));
                    if (tvLastDonationDate != null)
                        tvLastDonationDate.setText(lastDate != null ? lastDate : "--");

                    if (userCity != null) {
                        loadUrgentRequestNearMe(userCity);
                    }
                }
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    private void loadUrgentRequestNearMe(String city) {
        // --- تعديل المشكلة الثانية: التأكد من عرض أي طلب في المدينة وتصفير القديم ---
        Query urgentQuery = dbRef.child("Requests").orderByChild("city").equalTo(city).limitToFirst(1);
        urgentQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        currentUrgentRequest = data.getValue(BloodRequests.class);
                        if (currentUrgentRequest != null) {
                            if (tvUrgentHospital != null)
                                tvUrgentHospital.setText("المستشفى: " + currentUrgentRequest.getHospitalName());
                            if (tvUrgentBlood != null)
                                tvUrgentBlood.setText("الفصيلة المطلوبة: " + currentUrgentRequest.getBloodType());
                            if (tvUrgentUnits != null)
                                tvUrgentUnits.setText("الوحدات المطلوبة: " + currentUrgentRequest.getUnits());
                        }
                    }
                } else {
                    // تصفير الطلب الحالي لكي لا تنتقل بيانات قديمة بالخطأ
                    currentUrgentRequest = null;
                    if (tvUrgentHospital != null) tvUrgentHospital.setText("لا توجد طلبات في " + city);
                    if (tvUrgentBlood != null) tvUrgentBlood.setText("الفصيلة المطلوبة: --");
                    if (tvUrgentUnits != null) tvUrgentUnits.setText("عدد الوحدات: 0");
                }
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }
}