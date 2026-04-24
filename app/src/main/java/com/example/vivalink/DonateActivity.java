package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DonateActivity extends AppCompatActivity {

    private TextView tvBloodType, tvHospitalName, tvCity, tvDepartment, tvUnits, btnBack, tvRequestTime;
    private EditText etPhone, etMinutes;
    private Button btnConfirmDonation;
    private String requestId;
    private String userName = "متبرع";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        initViews();
        getUserNameFromFirebase();
        displayPassedData();

        btnBack.setOnClickListener(v -> finish());
        btnConfirmDonation.setOnClickListener(v -> handleDonation());
    }

    private void initViews() {
        tvBloodType = findViewById(R.id.tvBloodType);
        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvCity = findViewById(R.id.tvCity);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvUnits = findViewById(R.id.tvUnits);
        btnBack = findViewById(R.id.btnBack);
        etPhone = findViewById(R.id.etPhone);
        etMinutes = findViewById(R.id.etMinutes);
        btnConfirmDonation = findViewById(R.id.btnConfirmDonation);
        tvRequestTime = findViewById(R.id.tvRequestDate);
    }

    private void getUserNameFromFirebase() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            // ملاحظة: تأكدي أن اسم الحقل في الفايربيس هو "fullName" أو "name" كما في الـ Model
            FirebaseDatabase.getInstance().getReference("Donors").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // محاولة جلب الاسم من أكثر من حقل لضمان عدم بقائه "متبرع"
                                if (snapshot.hasChild("fullName")) userName = snapshot.child("fullName").getValue(String.class);
                                else if (snapshot.hasChild("name")) userName = snapshot.child("name").getValue(String.class);
                                else if (snapshot.hasChild("displayName")) userName = snapshot.child("displayName").getValue(String.class);
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }
    }

    private void displayPassedData() {
        if (getIntent() != null) {
            requestId = getIntent().getStringExtra("requestId");
            tvBloodType.setText(getIntent().getStringExtra("bloodType")); // أزلت النص الثابت لأن الـ XML غالباً يحتوي عليه أو لتجنب التكرار
            tvHospitalName.setText(getIntent().getStringExtra("hospitalName"));
            tvCity.setText(getIntent().getStringExtra("city"));
            tvDepartment.setText(getIntent().getStringExtra("department"));
            tvUnits.setText(getIntent().getStringExtra("units"));
            tvRequestTime.setText(getIntent().getStringExtra("confirmedAt"));
        }
    }

    private void handleDonation() {
        String phone = etPhone.getText().toString().trim();
        String minutesStr = etMinutes.getText().toString().trim();

        if (minutesStr.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "يرجى تعبئة جميع البيانات", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedMinutes = Integer.parseInt(minutesStr);
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        // تجهيز البيانات لجدول القادمين (IncomingDonations) لكي يراها الموظف
        Map<String, Object> incomingData = new HashMap<>();
        incomingData.put("uid", userId); // معرف المتبرع ليكون هو مفتاح الطلب
        incomingData.put("donorId", userId);
        incomingData.put("requestId", requestId); // ربطه بطلب المستشفى الأصلي
        incomingData.put("displayName", userName);
        incomingData.put("phone", phone);
        incomingData.put("bloodType", getIntent().getStringExtra("bloodType"));
        incomingData.put("city", getIntent().getStringExtra("city"));
        incomingData.put("status", "قادم"); // نفس الكلمة اللي بفلتر عليها الموظف
        incomingData.put("arrivalMinutes", selectedMinutes);

        // الحفظ في جدول IncomingDonations
        FirebaseDatabase.getInstance().getReference("IncomingDonations")
                .child(userId) // نستخدم ID المتبرع كمفتاح لسهولة الوصول إليه وحذفه لاحقاً
                .setValue(incomingData)
                .addOnSuccessListener(aVoid -> {

                    // الانتقال لصفحة التفاصيل (التايمر)
                    Intent intent = new Intent(this, RequestsDetailsActivity.class);
                    intent.putExtra("requestId", requestId);
                    intent.putExtra("minutes", selectedMinutes); // نمرر الدقائق للتايمر
                    intent.putExtra("bloodType", getIntent().getStringExtra("bloodType"));
                    intent.putExtra("hospitalName", getIntent().getStringExtra("hospitalName"));
                    intent.putExtra("city", getIntent().getStringExtra("city"));
                    intent.putExtra("department", getIntent().getStringExtra("department"));
                    intent.putExtra("units", getIntent().getStringExtra("units"));
                    intent.putExtra("confirmedAt", getIntent().getStringExtra("confirmedAt"));
                    // نمرر اسم المتبرع لصفحة التفاصيل عشان نستخدمه هناك إذا احتجنا
                    intent.putExtra("donorName", userName);

                    startActivity(intent);
                    Toast.makeText(this, "تم تأكيد الذهاب، رافقتك السلامة ✅", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "خطأ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}