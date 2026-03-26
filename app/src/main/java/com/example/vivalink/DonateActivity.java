package com.example.vivalink;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DonateActivity extends AppCompatActivity {
    // تعريف العناصر - أضفنا التايم والستيتس
    private TextView tvBloodType, tvHospitalName, tvLocation, tvDepartment, tvUnits, tvTimeDisplay, tvStatusDisplay, btnBack;
    private EditText etPhone, etConfirmPhone;
    private Button btnConfirmDonation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        initViews();
        displayPassedData();

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        btnConfirmDonation.setOnClickListener(v -> handleDonation());
    }

    private void initViews() {
        tvBloodType = findViewById(R.id.tvBloodType);
        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvLocation = findViewById(R.id.tvLocation);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvUnits = findViewById(R.id.tvUnits);

        // تأكدي إن هذول الـ IDs موجودين في ملف activity_donate.xml

        btnBack = findViewById(R.id.btnBack);
        etPhone = findViewById(R.id.etPhone);
        etConfirmPhone = findViewById(R.id.etConfirmPhone);
        btnConfirmDonation = findViewById(R.id.btnConfirmDonation);
    }

    private void displayPassedData() {
        // فحص getIntent لمنع الكراش
        if (getIntent() != null) {
            // استخراج البيانات مع وضع قيمة بديلة في حال كانت null لمنع ظهور كلمة "null"
            String blood = getIntent().getStringExtra("bloodType");
            String hospital = getIntent().getStringExtra("hospitalName");
            String city = getIntent().getStringExtra("city");
            String dept = getIntent().getStringExtra("department");
            String units = getIntent().getStringExtra("units");
            String time = getIntent().getStringExtra("time");
            String status = getIntent().getStringExtra("status");

            // العرض في الـ TextViews
            tvBloodType.setText("❤️ فصيلة الدم: " + (blood != null ? blood : "--"));
            tvHospitalName.setText("🏥 اسم المستشفى: " + (hospital != null ? hospital : "غير محدد"));
            tvLocation.setText("📍 الموقع: " + (city != null ? city : "غير محدد"));
            tvDepartment.setText("🚨 القسم: " + (dept != null ? dept : "الطوارئ"));
            tvUnits.setText("🩸 عدد الوحدات المطلوبة: " + (units != null ? units : "0"));

            if (tvTimeDisplay != null) {
                tvTimeDisplay.setText("⏰ الوقت: " + (time != null ? time : "--"));
            }
            if (tvStatusDisplay != null) {
                tvStatusDisplay.setText("📊 الحالة: " + (status != null ? status : "قيد الانتظار"));
            }
        }
    }

    private void handleDonation() {
        String p1 = etPhone.getText().toString().trim();
        String p2 = etConfirmPhone.getText().toString().trim();

        if (p1.isEmpty() || p2.isEmpty()) {
            Toast.makeText(this, "يرجى إدخال رقم الهاتف وتأكيده", Toast.LENGTH_SHORT).show();
            return;
        }

        if (p1.equals(p2)) {
            // هنا الكود اللي ببعت البيانات لقاعدة البيانات لو حبيتي تطوريها
            Toast.makeText(this, "تم تأكيد موعد التبرع بنجاح ✅", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "الأرقام غير متطابقة، يرجى التأكد", Toast.LENGTH_SHORT).show();
        }
    }
}