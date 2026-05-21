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
        // 1️⃣ إحصائيات المتبرعين في نفس المدينة (شغال صح، صلحنا بس منطق الفحوصات المعلقة جواته)
        dbRef.child("Donors").orderByChild("city").equalTo(currentStaffCity)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {
                        countDonorsCity.setText(String.valueOf(s.getChildrenCount()));

                        int pending = 0;
                        for (DataSnapshot ds : s.getChildren()) {
                            String status = ds.child("bloodTestStatus").getValue(String.class);

                            // 🔥 التصليح الجذري للفحوصات المعلقة: نعد فقط إذا كانت الحالة مكتوبة "معلق" صراحة بالفايربيس
                            // هيك الـ null والـ empty مستحيل ينعدوا، والرقم 3 الوهمي رح يختفي ويصير 0!
                            if ("معلق".equals(status)) {
                                pending++;
                            }
                        }
                        countPendingTests.setText(String.valueOf(pending));
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });

        // 2️⃣ إحصائيات تبرعات اليوم (🔥 تم تصليح الفلترة والتصفير التلقائي)
        // الكود هان بيجيب تاريخ اليوم الحالي بالظبط وبقارنه بجدول Donations
        // 2️⃣ إحصائيات تبرعات اليوم (🔥 التعديل الصحيح بناءً على قاعدة البيانات عندك)
        // بيجيب تاريخ اليوم بالصيغة المخزنة (dd/MM/yyyy) ليتطابق مع lastDonation
        String todayStr = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date());

        dbRef.child("Donors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                int todayDonationsCount = 0;

                for (DataSnapshot ds : s.getChildren()) {
                    String lastDonation = ds.child("lastDonation").getValue(String.class);
                    String donorCity = ds.child("city").getValue(String.class);

                    // الفحص: إذا كان المتبرع تبرع اليوم + وهو من نفس مدينة الموظف (طولكرم)
                    if (lastDonation != null && donorCity != null) {
                        if (lastDonation.trim().equals(todayStr) && donorCity.trim().equalsIgnoreCase(currentStaffCity.trim())) {
                            todayDonationsCount++;
                        }
                    }
                }

                // عرض الرقم الصحيح وتصفيره تلقائياً إذا دخلنا بيوم جديد
                countTodayDonors.setText(String.valueOf(todayDonationsCount));
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
        // 3️⃣ إحصائيات الطلبات المفتوحة للمستشفى (🚨 خليناها زي ما هي بدون أي تغيير)
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
                return true;
            } else if (id == R.id.nav_donors) {
                startActivity(new Intent(BloodBankHomeActivity.this, BloodBankDonorsActivity.class));
                return true;
            } else if (id == R.id.nav_requests) {
                startActivity(new Intent(BloodBankHomeActivity.this, BloodBankRequestsActivity.class));
                return true;
            }
            // 🔥 تفعيل زر الإشعارات هنا
            else if (id == R.id.nav_notifications) { // تأكدي أن هذا الـ ID هو نفسه الموجود في ملف الـ menu
                startActivity(new Intent(BloodBankHomeActivity.this, BloodBankNotificationActivity.class));
                return true;
            }
            else if (id == R.id.nav_settings) {
                startActivity(new Intent(BloodBankHomeActivity.this, BloodBankSettingsActivity.class));
                return true;
            }

            return false;
        });
    } }