package com.example.vivalink;

import android.content.Intent; // تأكدي من وجود هذا السطر
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BloodBankHomeActivity extends AppCompatActivity {

    private TextView countDonorsCity, countPendingTests, countTodayDonors, countOpenRequests;
    private TextView tvWelcomeMain, tvSubLocation, tvHospitalDetail;
    private DatabaseReference dbRef;
    private String currentStaffCity = "", currentHospitalId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbank_home);

        // تعريف العناصر من الواجهة
        tvWelcomeMain = findViewById(R.id.tvWelcomeMain);
        tvSubLocation = findViewById(R.id.tvSubLocation);
        tvHospitalDetail = findViewById(R.id.tvHospitalDetail);
        countDonorsCity = findViewById(R.id.countDonorsCity);
        countPendingTests = findViewById(R.id.countPendingTests);
        countTodayDonors = findViewById(R.id.countTodayDonors);
        countOpenRequests = findViewById(R.id.countOpenRequests);

        dbRef = FirebaseDatabase.getInstance().getReference();

        // التأكد من تسجيل الدخول وجلب بيانات الموظف
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            loadStaffData(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        // إعداد شريط التنقل السفلي
        setupNavigation();
    }

    private void loadStaffData(String userId) {
        dbRef.child("BloodBankStaff").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String hName = snapshot.child("hospitalName").getValue(String.class);
                    String hCity = snapshot.child("city").getValue(String.class);
                    currentHospitalId = snapshot.child("hospitalId").getValue(String.class);
                    currentStaffCity = (hCity != null) ? hCity : "";

                    tvWelcomeMain.setText("أهلاً موظف بنك " + (hName != null ? hName : ""));
                    tvSubLocation.setText("موظف بنك الدم - " + currentStaffCity);
                    tvHospitalDetail.setText("🏥 " + (hName != null ? hName : ""));

                    if (!currentStaffCity.isEmpty()) calculateStatistics();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void calculateStatistics() {
        // إحصائيات المتبرعين في نفس المدينة
        dbRef.child("Donors").orderByChild("city").equalTo(currentStaffCity)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {
                        countDonorsCity.setText(String.valueOf(s.getChildrenCount()));
                        int pending = 0;
                        for (DataSnapshot ds : s.getChildren()) {
                            String status = ds.child("bloodTestStatus").getValue(String.class);
                            if (status == null || status.isEmpty() || status.equals("معلق")) pending++;
                        }
                        countPendingTests.setText(String.valueOf(pending));
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });

        // إحصائيات تبرعات اليوم
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dbRef.child("Donations").orderByChild("date").equalTo(today)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {
                        countTodayDonors.setText(String.valueOf(s.getChildrenCount()));
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });

        // إحصائيات الطلبات المفتوحة للمستشفى
        if (currentHospitalId != null) {
            dbRef.child("Requests").orderByChild("hospitalId").equalTo(currentHospitalId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot s) {
                            int open = 0;
                            for (DataSnapshot ds : s.getChildren()) {
                                if ("مفتوح".equals(ds.child("status").getValue(String.class))) open++;
                            }
                            countOpenRequests.setText(String.valueOf(open));
                        }
                        @Override public void onCancelled(@NonNull DatabaseError e) {}
                    });
        }
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true; // نحن بالفعل في الهوم
            } else if (id == R.id.nav_donors) {
                // الانتقال لصفحة المتبرعون
                Intent intent = new Intent(BloodBankHomeActivity.this, BloodBankDonorsActivity.class);
                startActivity(intent);
                return true;
            }
            // يمكنك إضافة حالات (else if) لبقية الأزرار هنا
            return false;
        });
    }
}