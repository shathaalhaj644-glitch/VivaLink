package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
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

            // 1. جلب بيانات المتبرع (الاسم، المدينة، الإحصائيات)
            loadDonorData();
        }

        // البرمجة الخاصة بالتنقل بين الصفحات
        btnViewRequests.setOnClickListener(v -> startActivity(new Intent(this, RequestsActivity.class)));
        btnGoToProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnGoToDonate.setOnClickListener(v -> startActivity(new Intent(this, DonateActivity.class)));
    }

    private void initViews() {
        tvWelcomeDonor = findViewById(R.id.tvWelcomeDonor);
        tvDonationCount = findViewById(R.id.tvDonationCount);
        tvLastDonationDate = findViewById(R.id.tvLastDonationDate);

        // عناصر الطلب العاجل
        tvUrgentHospital = findViewById(R.id.tvHospitalName);
        tvUrgentBlood = findViewById(R.id.tvBloodType);
        tvUrgentCity = findViewById(R.id.tvUrgentCity); // تأكدي من وجوده في XML

        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnGoToProfile = findViewById(R.id.btnGoToProfile);
        btnGoToDonate = findViewById(R.id.btnGoToDonate);
    }

    private void loadDonorData() {
        dbRef.child("Donors").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    userCity = snapshot.child("city").getValue(String.class);
                    String lastDonation = snapshot.child("lastDonation").getValue(String.class);

                    // تحديث واجهة الإحصائيات
                    tvWelcomeDonor.setText("أهلاً بك، " + name);
                    tvLastDonationDate.setText("آخر تبرع: " + lastDonation);

                    // جلب عدد التبرعات من عقدة Appointments (مثال للربط الحقيقي)
                    getDonationStats();

                    // 2. بعد معرفة مدينة المستخدم، نبحث عن طلب عاجل في نفس المدينة
                    loadUrgentRequestNearMe(userCity);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void getDonationStats() {
        // نعد عدد المواعيد التي حالتها "Completed" لهذا المستخدم
        dbRef.child("Appointments").orderByChild("donorId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if ("Completed".equals(ds.child("status").getValue(String.class))) {
                                count++;
                            }
                        }
                        tvDonationCount.setText(String.valueOf(count));
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadUrgentRequestNearMe(String city) {
        // البحث في BloodRequests عن أول طلب يطابق مدينة المتبرع
        Query urgentQuery = dbRef.child("BloodRequests").orderByChild("city").equalTo(city).limitToFirst(1);

        urgentQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        tvUrgentHospital.setText(data.child("hospitalName").getValue(String.class));
                        tvUrgentBlood.setText("الفصيلة: " + data.child("bloodType").getValue(String.class));
                        tvUrgentCity.setText("الموقع: " + data.child("city").getValue(String.class));
                    }
                } else {
                    tvUrgentHospital.setText("لا يوجد طلبات عاجلة حالياً في مدينتك");
                    tvUrgentBlood.setText("-");
                    tvUrgentCity.setText("-");
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}