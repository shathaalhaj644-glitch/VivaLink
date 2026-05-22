package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DonateActivity extends AppCompatActivity {

    private TextView tvBloodType, tvHospitalName, tvCity, tvDepartment, tvUnits, btnBack, tvRequestTime;
    private EditText etPhone, etMinutes;
    private Button btnConfirmDonation;
    private String requestId, hospitalId, donorCity;
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
            FirebaseDatabase.getInstance().getReference("Donors").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.hasChild("fullName")) userName = snapshot.child("fullName").getValue(String.class);
                                else if (snapshot.hasChild("name")) userName = snapshot.child("name").getValue(String.class);

                                donorCity = snapshot.child("city").getValue(String.class);
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }
    }

    private void displayPassedData() {
        if (getIntent() != null) {
            requestId = getIntent().getStringExtra("requestId");
            hospitalId = getIntent().getStringExtra("hospitalId");

            tvBloodType.setText(getIntent().getStringExtra("bloodType"));
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

        final int selectedMinutes = Integer.parseInt(minutesStr);
        final String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;


        sendArrivalNotificationToStaff(selectedMinutes);


        Map<String, Object> incomingData = new HashMap<>();
        incomingData.put("donorId", userId);
        incomingData.put("requestId", requestId);
        incomingData.put("hospitalId", hospitalId);
        incomingData.put("displayName", userName);
        incomingData.put("phone", phone);
        incomingData.put("bloodType", getIntent().getStringExtra("bloodType"));
        incomingData.put("city", getIntent().getStringExtra("city"));
        incomingData.put("status", "قادم");
        incomingData.put("arrivalMinutes", selectedMinutes);

        FirebaseDatabase.getInstance().getReference("IncomingDonations").child(userId).setValue(incomingData)
                .addOnSuccessListener(aVoid -> {

                    Intent intent = new Intent(DonateActivity.this, RequestsDetailsActivity.class);
                    intent.putExtra("requestId", requestId);
                    intent.putExtra("minutes", selectedMinutes);
                    intent.putExtra("hospitalId", hospitalId);
                    intent.putExtra("donorName", userName);

                    intent.putExtra("bloodType", getIntent().getStringExtra("bloodType"));
                    intent.putExtra("hospitalName", getIntent().getStringExtra("hospitalName"));
                    intent.putExtra("city", getIntent().getStringExtra("city"));
                    intent.putExtra("department", getIntent().getStringExtra("department"));
                    intent.putExtra("units", getIntent().getStringExtra("units"));
                    intent.putExtra("confirmedAt", getIntent().getStringExtra("confirmedAt"));

                    startActivity(intent);
                    Toast.makeText(DonateActivity.this, "تم تأكيد الذهاب، رافقتك السلامة ✅", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {

                    Log.e("VivaLink_Error", "فشل في جدول القادمين: " + e.getMessage());
                    Toast.makeText(DonateActivity.this, "خطأ في تسجيل القادمين: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendArrivalNotificationToStaff(int minutes) {
        Log.d("VivaLink_Debug", "تم استدعاء الدالة بنجاح. قيمة hospitalId الحالية هي: " + hospitalId);

        if (hospitalId == null || hospitalId.trim().isEmpty()) {
            Log.e("VivaLink_Debug", "❌ خلل قاتل: hospitalId قيمته null أو فارغة! الفايربيس يرفض الرفع بدون معرف المستشفى.");
            Toast.makeText(this, "فشل إرسال الإشعار للمستشفى بسبب نقص البيانات", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("Notifications").push();
        String notifId = notifRef.getKey();

        if (notifId != null) {
            HashMap<String, Object> notifData = new HashMap<>();

            String actualDepartment = tvDepartment.getText() != null ? tvDepartment.getText().toString().trim() : "القسم المعني";
            String bloodType = tvBloodType.getText() != null ? tvBloodType.getText().toString().trim() : "-";


            String actualHospitalName = tvHospitalName.getText() != null ? tvHospitalName.getText().toString().trim() : "";

            notifData.put("notificationId", notifId);
            notifData.put("title", "متبرع في الطريق 🚒");

            String formattedMessage = "سيصل خلال " + minutes + " دقيقة لـ " + actualDepartment + " - " + userName + " متبرع بفصيلة " + bloodType + ".";
            notifData.put("message", formattedMessage);

            notifData.put("type", "donation_confirmed");
            notifData.put("targetType", "ADMIN");
            notifData.put("targetUserId", hospitalId.trim());


            notifData.put("hospitalName", actualHospitalName);

            notifData.put("requestId", requestId != null ? requestId : "");
            notifData.put("createdAt", System.currentTimeMillis());
            notifData.put("isRead", false);
            notifData.put("city", tvCity.getText() != null ? tvCity.getText().toString().trim() : "");

            notifRef.setValue(notifData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("VivaLink_Debug", "✅ نجاح! تم رفع الإشعار بنجاح إلى Firebase بمفتاح: " + notifId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("VivaLink_Debug", "❌ فشل الرفع إلى Firebase: " + e.getMessage());
                    });
        }
    } }