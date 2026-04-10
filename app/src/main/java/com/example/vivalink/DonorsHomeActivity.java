package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button btnViewRequests, btnGoToDonate, btnGoToProfile;

    private DatabaseReference dbRef;
    private String userId, lastDonationDateFromDB;

    // ✅ بيانات المتبرع
    private String donorBloodType, donorCity;

    // ✅ الطلب المفلتر (نفسه بكل التطبيق)
    private String hospitalName, bloodType, units, requestDate, department, city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donors_home);

        initViews();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference();

        loadDonorData();

        btnViewRequests.setOnClickListener(v ->
                startActivity(new Intent(this, RequestsActivity.class)));

        btnGoToDonate.setOnClickListener(v -> {

            if (hospitalName == null) {
                Toast.makeText(this, "لا يوجد طلب مناسب لك", Toast.LENGTH_SHORT).show();
                return;
            }

            checkDonationEligibility();
        });

        btnGoToProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class))); // عدل الاسم إذا مختلف
    }

    private void initViews() {
        tvWelcomeDonor = findViewById(R.id.tvWelcomeDonor);
        tvDonationCount = findViewById(R.id.tvDonationCount);
        tvLastDonationDate = findViewById(R.id.tvLastDonationDate);

        tvUrgentHospital = findViewById(R.id.tvHospitalName);
        tvUrgentBlood = findViewById(R.id.tvBloodType);
        tvUrgentUnits = findViewById(R.id.tvUnits);
        tvRequestDate = findViewById(R.id.tvRequestDate);

        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnGoToDonate = findViewById(R.id.btnGoToDonate);
        btnGoToProfile = findViewById(R.id.btnGoToProfile);
    }

    // ✅ تحميل بيانات المتبرع
    private void loadDonorData() {
        dbRef.child("Donors").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            String name = snapshot.child("fullName").getValue(String.class);
                            lastDonationDateFromDB = snapshot.child("lastDonation").getValue(String.class);

                            donorBloodType = snapshot.child("bloodType").getValue(String.class);
                            donorCity = snapshot.child("city").getValue(String.class);

                            Object countObj = snapshot.child("donationCount").getValue();

                            tvWelcomeDonor.setText("👋 أهلاً " + name);
                            tvLastDonationDate.setText(lastDonationDateFromDB != null ? lastDonationDateFromDB : "--");
                            tvDonationCount.setText(countObj != null ? String.valueOf(countObj) : "0");

                            // 🔥 أهم سطر → الفلترة
                            loadFilteredRequest();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DonorsHome", error.getMessage());
                    }
                });
    }

    // ✅ نفس فلترة RequestsActivity
    private void loadFilteredRequest() {
        dbRef.child("Requests").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean found = false;

                for (DataSnapshot data : snapshot.getChildren()) {

                    String reqBlood = data.child("bloodType").getValue(String.class);
                    String reqCity = data.child("city").getValue(String.class);

                    if (reqBlood != null && reqCity != null &&
                            reqBlood.equalsIgnoreCase(donorBloodType) &&
                            reqCity.equalsIgnoreCase(donorCity)) {

                        found = true;

                        hospitalName = data.child("hospitalName").getValue(String.class);
                        bloodType = reqBlood;
                        units = data.child("units").getValue(String.class);
                        requestDate = data.child("date").getValue(String.class);
                        department = data.child("department").getValue(String.class);
                        city = reqCity;

                        // ✅ تنسيق التاريخ (اختياري)
                        if (requestDate != null) {
                            try {
                                SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                requestDate = out.format(in.parse(requestDate));
                            } catch (Exception ignored) {}
                        }

                        // ✅ عرض
                        tvUrgentHospital.setText("المستشفى: " + hospitalName);
                        if (department != null)
                            tvUrgentHospital.append("\nالقسم: " + department);

                        tvUrgentBlood.setText("الفصيلة المطلوبة: " + bloodType);
                        tvUrgentUnits.setText("عدد الوحدات: " + units);
                        tvRequestDate.setText("تاريخ الطلب: " + requestDate);

                        break;
                    }
                }

                if (!found) {
                    hospitalName = null;

                    tvUrgentHospital.setText("لا يوجد طلبات مناسبة لك");
                    tvUrgentBlood.setText("--");
                    tvUrgentUnits.setText("--");
                    tvRequestDate.setText("--");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FilterError", error.getMessage());
            }
        });
    }

    // ✅ التحقق قبل التبرع
    private void checkDonationEligibility() {

        if (lastDonationDateFromDB == null || lastDonationDateFromDB.equals("--")) {
            goToDonate();
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date lastDate = sdf.parse(lastDonationDateFromDB);

            long diff = Math.abs(new Date().getTime() - lastDate.getTime());
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if (days < 120) {
                showIneligibilityAlert(120 - days, lastDate);
            } else {
                goToDonate();
            }

        } catch (Exception e) {
            goToDonate();
        }
    }

    // ✅ إرسال نفس الطلب المفلتر
    private void goToDonate() {
        Intent intent = new Intent(this, DonateActivity.class);

        intent.putExtra("hospitalName", hospitalName);
        intent.putExtra("bloodType", bloodType);
        intent.putExtra("units", units);
        intent.putExtra("requestDate", requestDate);
        intent.putExtra("department", department);
        intent.putExtra("city", city);

        startActivity(intent);
    }

    private void showIneligibilityAlert(long daysRemaining, Date lastDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(lastDate);
        c.add(Calendar.DAY_OF_YEAR, 120);

        String nextDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(c.getTime());

        new AlertDialog.Builder(this)
                .setTitle("⚠️ لا يمكنك التبرع الآن")
                .setMessage("باقي " + daysRemaining + " يوم\nيمكنك التبرع بتاريخ: " + nextDate)
                .setPositiveButton("حسناً", null)
                .show();
    }
}