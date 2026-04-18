package com.example.vivalink;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DonorsHomeActivity extends AppCompatActivity {

    private TextView tvWelcomeDonor, tvDonationCount, tvLastDonationDate;
    private TextView tvUrgentHospital, tvUrgentBlood, tvUrgentUnits, tvRequestDate, tvStatusTitle;
    private TextView tvDaysSinceLastTest;
    private CardView cardBloodTestAlert, cardUrgentRequest, layoutNoRequest;
    private Button btnViewRequests, btnGoToDonate, btnGoToProfile, btnMarkBloodTest;

    private DatabaseReference dbRef;
    private String userId;
    private String lastDonationDateFromDB, donorBloodType, donorCity;
    private String hospitalName, bloodType, units, confirmedAt, department, city, requestId, currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donors_home);

        initViews();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            dbRef = FirebaseDatabase.getInstance().getReference();
            loadDonorData();
        }

        btnViewRequests.setOnClickListener(v -> startActivity(new Intent(this, RequestsActivity.class)));

        // تعديل منطق زر التبرع
        btnGoToDonate.setOnClickListener(v -> {
            if ("مغلق".equals(currentStatus)) {
                // إذا كان مغلق، نذهب مباشرة لصفحة التفاصيل
                goToDetails();
            } else {
                // إذا كان مفتوح أو عاجل، نفحص الأهلية ثم نذهب لصفحة الدونيت
                checkDonationEligibility();
            }
        });

        btnGoToProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnMarkBloodTest.setOnClickListener(v -> updateBloodTestDate());
    }

    private void initViews() {
        tvWelcomeDonor = findViewById(R.id.tvWelcomeDonor);
        tvDonationCount = findViewById(R.id.tvDonationCount);
        tvLastDonationDate = findViewById(R.id.tvLastDonationDate);
        tvUrgentHospital = findViewById(R.id.tvHospitalName);
        tvUrgentBlood = findViewById(R.id.tvBloodType);
        tvUrgentUnits = findViewById(R.id.tvUnits);
        tvRequestDate = findViewById(R.id.tvRequestDate);
        tvStatusTitle = findViewById(R.id.tvStatusTitle); // تأكدي من إضافة هذا الـ ID في XML

        cardBloodTestAlert = findViewById(R.id.cardBloodTestAlert);
        cardUrgentRequest = findViewById(R.id.cardUrgentRequest);
        layoutNoRequest = findViewById(R.id.layoutNoRequest);

        tvDaysSinceLastTest = findViewById(R.id.tvDaysSinceLastTest);
        btnMarkBloodTest = findViewById(R.id.btnMarkBloodTest);

        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnGoToDonate = findViewById(R.id.btnGoToDonate);
        btnGoToProfile = findViewById(R.id.btnGoToProfile);
    }

    private void loadDonorData() {
        dbRef.child("Donors").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("fullName").getValue(String.class);
                    lastDonationDateFromDB = snapshot.child("lastDonation").getValue(String.class);
                    donorBloodType = snapshot.child("bloodType").getValue(String.class);
                    donorCity = snapshot.child("city").getValue(String.class);
                    String lastBloodTest = snapshot.child("lastBloodTest").getValue(String.class);
                    Object countObj = snapshot.child("donationCount").getValue();

                    tvWelcomeDonor.setText("أهلاً " + (name != null ? name : "") + " !");
                    tvLastDonationDate.setText(lastDonationDateFromDB != null ? lastDonationDateFromDB : "--");
                    tvDonationCount.setText(countObj != null ? String.valueOf(countObj) : "0");

                    checkBloodTestInterval(lastBloodTest);

                    if (donorCity != null && donorBloodType != null) {
                        loadFilteredRequest();
                    }
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
                    String status = data.child("status").getValue(String.class);

                    if (reqBlood != null && reqCity != null && status != null) {
                        if (reqBlood.trim().equalsIgnoreCase(donorBloodType.trim()) &&
                                reqCity.trim().equalsIgnoreCase(donorCity.trim())) {

                            if (status.equals("ملغي")) continue;

                            found = true;
                            requestId = data.getKey();
                            currentStatus = status;
                            hospitalName = data.child("hospitalName").getValue(String.class);
                            bloodType = reqBlood;
                            units = data.child("units").getValue(String.class);
                            city = reqCity;
                            department = data.child("department").getValue(String.class);

                            String rawValue = data.child("confirmedAt").getValue(String.class);
                            confirmedAt = formatMyTime(rawValue);

                            updateRequestUI(true, status);
                            break;
                        }
                    }
                }
                if (!found) updateRequestUI(false, "");
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateRequestUI(boolean found, String status) {
        if (found) {
            layoutNoRequest.setVisibility(View.GONE);
            cardUrgentRequest.setVisibility(View.VISIBLE);

            if ("عاجل".equals(status)) {
                tvStatusTitle.setText("🚨 طلب تبرع عاجل!");
                tvStatusTitle.setTextColor(Color.RED);
                btnGoToDonate.setText("تبرع الآن");
            } else if ("مغلق".equals(status)) {
                tvStatusTitle.setText("✅ لقد تم التبرع لهذا الطلب");
                tvStatusTitle.setTextColor(Color.parseColor("#2E7D32"));
                btnGoToDonate.setText("عرض التفاصيل");
            } else {
                tvStatusTitle.setText("🩸 طلب تبرع دم");
                tvStatusTitle.setTextColor(Color.BLACK);
                btnGoToDonate.setText("تبرع الآن");
            }

            tvUrgentHospital.setText("المستشفى: " + hospitalName);
            tvUrgentBlood.setText("الفصيلة المطلوبة: " + bloodType);
            tvUrgentUnits.setText("الوحدات المطلوبة: " + units);
            tvRequestDate.setText("📅 تاريخ الطلب: " + confirmedAt);
        } else {
            layoutNoRequest.setVisibility(View.VISIBLE);
            cardUrgentRequest.setVisibility(View.GONE);
        }
    }

    private void goToDetails() {
        Intent intent = new Intent(this, RequestsDetailsActivity.class);
        intent.putExtra("requestId", requestId);
        intent.putExtra("hospitalName", hospitalName);
        intent.putExtra("city", city);
        intent.putExtra("bloodType", bloodType);
        intent.putExtra("department", department);
        intent.putExtra("units", units);
        intent.putExtra("confirmedAt", confirmedAt);
        intent.putExtra("isDonated", true); // نرسل true لظهور الرسالة الخضراء مباشرة
        startActivity(intent);
    }

    private void checkDonationEligibility() {
        if (lastDonationDateFromDB == null || lastDonationDateFromDB.equals("--") || lastDonationDateFromDB.isEmpty()) {
            goToDonate();
            return;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date lastDate = sdf.parse(normalizeNumbers(lastDonationDateFromDB));
            long diff = new Date().getTime() - lastDate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if (days < 120) {
                showIneligibilityAlert(120 - days, lastDate);
            } else {
                goToDonate();
            }
        } catch (Exception e) { goToDonate(); }
    }

    private void goToDonate() {
        if (hospitalName == null) return;
        Intent intent = new Intent(this, DonateActivity.class);
        intent.putExtra("requestId", requestId);
        intent.putExtra("hospitalName", hospitalName);
        intent.putExtra("bloodType", bloodType);
        intent.putExtra("units", units);
        intent.putExtra("confirmedAt", confirmedAt);
        intent.putExtra("city", city);
        intent.putExtra("department", department);
        startActivity(intent);
    }

    // باقي الدوال المساعدة (checkBloodTestInterval, formatMyTime, updateBloodTestDate, showIneligibilityAlert, normalizeNumbers) تبقى كما هي في كودك الأصلي...

    private String formatMyTime(String raw) {
        if (raw == null || raw.isEmpty() || raw.equals("--")) return "--";
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
            Date d = parser.parse(raw);
            SimpleDateFormat dateOnly = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.ENGLISH);
            return dateOnly.format(d);
        } catch (Exception e) { return raw; }
    }

    private void checkBloodTestInterval(String lastBloodTest) {
        if (lastBloodTest != null && !lastBloodTest.equals("--") && !lastBloodTest.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date lastDate = sdf.parse(normalizeNumbers(lastBloodTest));
                long diff = new Date().getTime() - lastDate.getTime();
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                if (days >= 120) {
                    cardBloodTestAlert.setVisibility(View.VISIBLE);
                    tvDaysSinceLastTest.setText("مر " + days + " يوم على آخر فحص.");
                } else { cardBloodTestAlert.setVisibility(View.GONE); }
            } catch (Exception e) { cardBloodTestAlert.setVisibility(View.GONE); }
        } else {
            cardBloodTestAlert.setVisibility(View.VISIBLE);
            tvDaysSinceLastTest.setText("يُرجى إجراء فحص دم دوري.");
        }
    }

    private void updateBloodTestDate() {
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date());
        dbRef.child("Donors").child(userId).child("lastBloodTest").setValue(today)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "تم تحديث تاريخ الفحص بنجاح ✅", Toast.LENGTH_SHORT).show();
                    cardBloodTestAlert.setVisibility(View.GONE);
                });
    }

    private void showIneligibilityAlert(long daysRemaining, Date lastDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(lastDate);
        c.add(Calendar.DAY_OF_YEAR, 120);
        String nextDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(c.getTime());
        new AlertDialog.Builder(this)
                .setTitle("⚠️ تنبيه")
                .setMessage("باقي " + daysRemaining + " يوم لتتمكن من التبرع مجدداً.\nتاريخك القادم: " + nextDate)
                .setPositiveButton("حسناً", null).show();
    }

    private String normalizeNumbers(String input) {
        if (input == null) return "";
        return input.replace("٠","0").replace("١","1").replace("٢","2").replace("٣","3")
                .replace("٤","4").replace("٥","5").replace("٦","6").replace("٧","7")
                .replace("٨","8").replace("٩","9").replace("-","/");
    }
}