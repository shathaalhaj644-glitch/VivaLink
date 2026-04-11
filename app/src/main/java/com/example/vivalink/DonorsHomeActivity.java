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
    private String donorBloodType, donorCity;
    private String hospitalName, bloodType, units, requestDate, department, city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donors_home);
        initViews();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference();
        loadDonorData();

        btnViewRequests.setOnClickListener(v -> startActivity(new Intent(this, RequestsActivity.class)));
        btnGoToDonate.setOnClickListener(v -> checkDonationEligibility());
        btnGoToProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
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

    private void loadDonorData() {
        dbRef.child("Donors").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("fullName").getValue(String.class);
                    lastDonationDateFromDB = snapshot.child("lastDonation").getValue(String.class);
                    donorBloodType = snapshot.child("bloodType").getValue(String.class);
                    donorCity = snapshot.child("city").getValue(String.class);
                    Object countObj = snapshot.child("donationCount").getValue();

                    tvWelcomeDonor.setText("أهلاً " + name + " ! تبرعك قد ينقذ حياة 🖐️");
                    tvLastDonationDate.setText(lastDonationDateFromDB != null ? lastDonationDateFromDB : "--");
                    tvDonationCount.setText(countObj != null ? String.valueOf(countObj) : "0");
                    loadFilteredRequest();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

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
                        city = reqCity;
                        department = data.child("department").getValue(String.class);

                        // تنسيق التاريخ
                        if (requestDate != null) {
                            try {
                                SimpleDateFormat outputFormat = new SimpleDateFormat("d/M/yyyy", Locale.ENGLISH);
                                Date dateObj = new Date(requestDate);
                                requestDate = outputFormat.format(dateObj);
                            } catch (Exception e) {}
                        }

                        tvUrgentHospital.setText("المستشفى: " + hospitalName); // تم حذف إضافة القسم هنا
                        tvUrgentBlood.setText("الفصيلة المطلوبة: " + bloodType);
                        tvUrgentUnits.setText("الوحدات المطلوبة: " + units);
                        tvRequestDate.setText("📅 تاريخ الطلب: " + requestDate);
                        break;
                    }
                }
                if (!found) {
                    tvUrgentHospital.setText("لا يوجد طلبات عاجلة حالياً");
                    tvUrgentBlood.setText("--");
                    tvUrgentUnits.setText("--");
                    tvRequestDate.setText("--");
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void checkDonationEligibility() {
        if (lastDonationDateFromDB == null || lastDonationDateFromDB.equals("--")) {
            goToDonate();
            return;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date lastDate = sdf.parse(lastDonationDateFromDB);
            long diff = Math.abs(new Date().getTime() - lastDate.getTime());
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            if (days < 120) {
                showIneligibilityAlert(120 - days, lastDate);
            } else {
                goToDonate();
            }
        } catch (Exception e) { goToDonate(); }
    }

    private void goToDonate() {
        if (hospitalName == null) {
            Toast.makeText(this, "لا يوجد طلب حالي للتبرع له", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, DonateActivity.class);
        intent.putExtra("hospitalName", hospitalName);
        intent.putExtra("bloodType", bloodType);
        intent.putExtra("units", units);
        intent.putExtra("requestDate", requestDate);
        intent.putExtra("city", city);
        intent.putExtra("department", department);
        startActivity(intent);
    }

    private void showIneligibilityAlert(long daysRemaining, Date lastDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(lastDate);
        c.add(Calendar.DAY_OF_YEAR, 120);
        String nextDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(c.getTime());
        new AlertDialog.Builder(this)
                .setTitle("⚠️ تنبيه")
                .setMessage("باقي " + daysRemaining + " يوم لتتمكن من التبرع مجدداً.\nتاريخ تبرعك القادم: " + nextDate)
                .setPositiveButton("حسناً", null).show();
    }
}