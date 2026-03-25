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

    private TextView tvWelcomeDonor, tvDonationCount, tvLastDonationDate;
    private TextView tvUrgentHospital, tvUrgentBlood, tvUrgentCity;
    private Button btnViewRequests, btnGoToProfile, btnGoToDonate;
    private DatabaseReference dbRef;
    private String userId, userCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donors_home);

        // 1. ربط العناصر بالـ XML
        initViews();

        // 2. التحقق من تسجيل الدخول والاتصال بـ Firebase
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            dbRef = FirebaseDatabase.getInstance().getReference();
            loadDonorData();
        } else {
            // إذا لم يسجل دخول، نرجعه لصفحة Login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // 3. برمجة أزرار التنقل
        btnViewRequests.setOnClickListener(v -> {
            Intent intent = new Intent(this, RequestsActivity.class);
            startActivity(intent);
        });

        btnGoToProfile.setOnClickListener(v -> {
            // تأكدي من وجود ملف ProfileActivity
            startActivity(new Intent(this, ProfileActivity.class));
        });

        btnGoToDonate.setOnClickListener(v -> {
            // تأكدي من وجود ملف DonateActivity
            startActivity(new Intent(this, DonateActivity.class));
        });
    }

    private void initViews() {
        tvWelcomeDonor = findViewById(R.id.tvWelcomeDonor);
        tvDonationCount = findViewById(R.id.tvDonationCount);
        tvLastDonationDate = findViewById(R.id.tvLastDonationDate);

        // المربع الأحمر (الطلب العاجل)
        tvUrgentHospital = findViewById(R.id.tvHospitalName);
        tvUrgentBlood = findViewById(R.id.tvBloodType);
        tvUrgentCity = findViewById(R.id.tvUrgentCity);

        // الأزرار
        btnViewRequests = findViewById(R.id.btnViewRequests); // الزر الأحمر "عرض جميع الطلبات"
        btnGoToProfile = findViewById(R.id.btnGoToProfile);
        btnGoToDonate = findViewById(R.id.btnGoToDonate);
    }

    private void loadDonorData() {
        dbRef.child("Donors").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("fullName").getValue(String.class);
                    userCity = snapshot.child("city").getValue(String.class);
                    String lastDate = snapshot.child("lastDonation").getValue(String.class);

                    // معالجة عدد التبرعات (String أو Long)
                    Object countObj = snapshot.child("donationCount").getValue();
                    String count = (countObj != null) ? String.valueOf(countObj) : "0";

                    tvWelcomeDonor.setText("👋 أهلاً بك، " + (name != null ? name : "متبرع") + "!");
                    tvDonationCount.setText(count);
                    tvLastDonationDate.setText(lastDate != null ? lastDate : "--");

                    // جلب الطلب العاجل بناءً على مدينة المتبرع
                    if (userCity != null) {
                        loadUrgentRequestNearMe(userCity);
                    }
                } else {
                    tvWelcomeDonor.setText("👋 أهلاً بك! يرجى إكمال بياناتك");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
    }

    private void loadUrgentRequestNearMe(String city) {
        // تم التعديل لـ "Requests" ليطابق قاعدة بياناتك تماماً
        Query urgentQuery = dbRef.child("Requests").orderByChild("city").equalTo(city).limitToFirst(1);

        urgentQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String hName = data.child("hospitalName").getValue(String.class);
                        String bType = data.child("bloodType").getValue(String.class);
                        String cName = data.child("city").getValue(String.class);

                        tvUrgentHospital.setText("المستشفى: " + (hName != null ? hName : "غير متوفر"));
                        tvUrgentBlood.setText("الفصيلة المطلوبة: " + (bType != null ? bType : "--"));
                        tvUrgentCity.setText("الموقع: " + (cName != null ? cName : city));
                    }
                } else {
                    // إذا لم يوجد طلب في نفس المدينة
                    tvUrgentHospital.setText("لا يوجد طلبات عاجلة في مدينتك حالياً");
                    tvUrgentBlood.setText("الفصيلة المطلوبة: --");
                    tvUrgentCity.setText("الموقع: " + city);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Urgent Query Error: " + error.getMessage());
            }
        });
    }
}