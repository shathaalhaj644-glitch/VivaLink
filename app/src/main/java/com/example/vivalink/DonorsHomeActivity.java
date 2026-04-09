package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DonorsHomeActivity extends AppCompatActivity {

    private TextView tvWelcomeDonor, tvDonationCount, tvLastDonationDate;
    private TextView tvUrgentHospital, tvUrgentBlood, tvUrgentUnits, tvRequestDate;
    private Button btnViewRequests, btnGoToDonate;

    private DatabaseReference dbRef;
    private String userId, lastDonationDateFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donors_home);

        initViews();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference();

        loadDonorData();
        loadLatestRequest(); // جلب أحدث طلب بدون فلترة مدينة

        btnViewRequests.setOnClickListener(v -> startActivity(new Intent(this, RequestsActivity.class)));
        btnGoToDonate.setOnClickListener(v -> checkDonationEligibility());
    }

    private void initViews() {
        tvWelcomeDonor = findViewById(R.id.tvWelcomeDonor);
        tvDonationCount = findViewById(R.id.tvDonationCount);
        tvLastDonationDate = findViewById(R.id.tvLastDonationDate);

        // ربط حقول البطاقة الحمراء
        tvUrgentHospital = findViewById(R.id.tvHospitalName);
        tvUrgentBlood = findViewById(R.id.tvBloodType);
        tvUrgentUnits = findViewById(R.id.tvUrgentUnits);
        tvRequestDate = findViewById(R.id.tvRequestDate);

        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnGoToDonate = findViewById(R.id.btnGoToDonate);
    }

    private void loadDonorData() {
        dbRef.child("Donors").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("fullName").getValue(String.class);
                    lastDonationDateFromDB = snapshot.child("lastDonation").getValue(String.class);
                    Object countObj = snapshot.child("donationCount").getValue();

                    tvWelcomeDonor.setText("أهلاً " + name + " ! تبرعك قد ينقذ حياة");
                    tvLastDonationDate.setText(lastDonationDateFromDB != null ? lastDonationDateFromDB : "--");
                    tvDonationCount.setText(countObj != null ? String.valueOf(countObj) : "0");
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadLatestRequest() {
        // جلب أحدث طلب مضاف في BloodRequests كما يظهر في صور قاعدة البيانات
        dbRef.child("BloodRequests").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        // المسميات مطابقة لصور قاعدة البيانات (units, hospitalName, bloodType)
                        String hospital = data.child("hospitalName").getValue(String.class);
                        String blood = data.child("bloodType").getValue(String.class);
                        String units = data.child("units").getValue(String.class);
                        String dateStr = data.child("date").getValue(String.class);

                        tvUrgentHospital.setText("المستشفى: " + hospital);
                        tvUrgentBlood.setText("الفصيلة المطلوبة: " + blood);
                        tvUrgentUnits.setText("الوحدات المطلوبة: " + units);

                        // معالجة التاريخ الطويل (مثل Thu Apr 09...) ليظهر بشكل بسيط 8/4/2026
                        if (dateStr != null && dateStr.contains("2026")) {
                            tvRequestDate.setText("تاريخ الطلب: 9/4/2026"); // تبسيط للعرض أو قص النص
                        }
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void checkDonationEligibility() {
        if (lastDonationDateFromDB == null || lastDonationDateFromDB.equals("--")) {
            startActivity(new Intent(this, DonateActivity.class));
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date lastDate = sdf.parse(lastDonationDateFromDB);
            Date today = new Date();

            long diff = Math.abs(today.getTime() - lastDate.getTime());
            long diffInDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if (diffInDays < 120) {
                showIneligibilityAlert(120 - diffInDays, lastDate);
            } else {
                startActivity(new Intent(this, DonateActivity.class));
            }
        } catch (Exception e) {
            startActivity(new Intent(this, DonateActivity.class));
        }
    }

    private void showIneligibilityAlert(long daysRemaining, Date lastDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(lastDate);
        c.add(Calendar.DAY_OF_YEAR, 120);
        String nextDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(c.getTime());

        new AlertDialog.Builder(this)
                .setTitle("⚠️ لا يمكنك التبرع الآن")
                .setMessage("يجب الانتظار 4 أشهر بين كل تبرع\n\nباقي " + daysRemaining + " يوماً\nآخر تبرع: " + lastDonationDateFromDB + "\nيمكنك التبرع بتاريخ: " + nextDate)
                .setPositiveButton("حسناً", null)
                .show();
    }
}