package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class DonateActivity extends AppCompatActivity {

    private TextView tvBloodType, tvHospitalName, tvCity, tvDepartment, tvUnits, btnBack, tvRequestTime;
    private EditText etPhone, etMinutes;
    private Button btnConfirmDonation;
    private String requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        initViews();
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

    private void displayPassedData() {
        if (getIntent() != null) {
            requestId = getIntent().getStringExtra("requestId");

            // إضافة العناوين قبل البيانات لتظهر بشكل مرتب
            tvBloodType.setText("🩸 فصيلة الدم: " + getIntent().getStringExtra("bloodType"));
            tvHospitalName.setText("🏥 المستشفى: " + getIntent().getStringExtra("hospitalName"));
            tvCity.setText("📍 المدينة: " + getIntent().getStringExtra("city"));
            tvDepartment.setText("🏢 القسم: " + getIntent().getStringExtra("department"));
            tvUnits.setText("🧪 الوحدات: " + getIntent().getStringExtra("units"));

            String timeFromIntent = getIntent().getStringExtra("confirmedAt");
            if (timeFromIntent != null) {
                tvRequestTime.setText("📅 تاريخ الطلب: " + timeFromIntent);
            }
        }
    }

    private void handleDonation() {
        String phone = etPhone.getText().toString().trim();
        String minutesStr = etMinutes.getText().toString().trim();

        if (minutesStr.isEmpty()) {
            Toast.makeText(this, "يرجى إدخال وقت الوصول بالدقائق", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() < 9) {
            Toast.makeText(this, "يرجى إدخال رقم هاتف صحيح", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, RequestsDetailsActivity.class);
        intent.putExtra("requestId", requestId);
        intent.putExtra("hospitalName", getIntent().getStringExtra("hospitalName"));
        intent.putExtra("bloodType", getIntent().getStringExtra("bloodType"));
        intent.putExtra("city", getIntent().getStringExtra("city"));
        intent.putExtra("department", getIntent().getStringExtra("department"));
        intent.putExtra("units", getIntent().getStringExtra("units"));
        intent.putExtra("confirmedAt", getIntent().getStringExtra("confirmedAt"));
        intent.putExtra("minutes", Integer.parseInt(minutesStr));

        startActivity(intent);
        Toast.makeText(this, "تم تأكيد طلب التبرع، بدأ العد التنازلي ✅", Toast.LENGTH_SHORT).show();
        finish();
    }
}