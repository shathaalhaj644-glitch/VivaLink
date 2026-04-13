package com.example.vivalink;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class DonateActivity extends AppCompatActivity {
    private TextView tvBloodType, tvHospitalName, tvCity, tvDepartment, tvUnits, btnBack;
    private EditText etPhone;
    private Button btnConfirmDonation;
    private Spinner spinnerTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        initViews();
        setupTimeSpinner();
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
        btnConfirmDonation = findViewById(R.id.btnConfirmDonation);
        spinnerTime = findViewById(R.id.spinnerTime);
    }

    private void setupTimeSpinner() {
        ArrayList<String> times = new ArrayList<>();
        times.add("اختر وقت الوصول");
        times.add("خلال 30 دقيقة");
        times.add("خلال ساعة");
        times.add("خلال ساعتين");
        times.add("بعد 3 ساعات");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, times);
        spinnerTime.setAdapter(adapter);
    }

    private void displayPassedData() {
        if (getIntent() != null) {
            tvBloodType.setText("فصيلة الدم: " + getIntent().getStringExtra("bloodType"));
            tvHospitalName.setText(getIntent().getStringExtra("hospitalName"));
            tvCity.setText(getIntent().getStringExtra("city"));
            tvDepartment.setText(getIntent().getStringExtra("department"));
            tvUnits.setText("الوحدات: " + getIntent().getStringExtra("units") + " ");
        }
    }

    private void handleDonation() {
        String phone = etPhone.getText().toString().trim();
        String time = spinnerTime.getSelectedItem().toString();

        if (time.equals("اختر وقت الوصول")) {
            Toast.makeText(this, "يرجى اختيار وقت الوصول أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() < 9) {
            Toast.makeText(this, "يرجى إدخال رقم هاتف صحيح", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "شكراً لك! تم تأكيد طلب التبرع ✅", Toast.LENGTH_LONG).show();
        finish();
    }
}