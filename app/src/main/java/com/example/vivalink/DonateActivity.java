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
        if (getIntent() != null) {
            String blood = getIntent().getStringExtra("bloodType");
            String hospital = getIntent().getStringExtra("hospitalName");
            String city = getIntent().getStringExtra("city");
            String dept = getIntent().getStringExtra("department");
            String units = getIntent().getStringExtra("units");

            // الفحص: هل البيانات المرسلة تدل على عدم وجود طلب؟
            if (hospital == null || hospital.equals("لا يوجد طلبات حالياً") || hospital.isEmpty()) {

                // 1. نظهر جملة واحدة فقط في أول TextView
                tvHospitalName.setText("لا يوجد طلبات حالياً");
                tvHospitalName.setGravity(android.view.Gravity.CENTER); // لجعل النص في المنتصف (اختياري)

                // 2. نخفي باقي الحقول تماماً من المربع السكني عشان ما يضل عناوين فاضية
                tvBloodType.setVisibility(android.view.View.GONE);
                tvLocation.setVisibility(android.view.View.GONE);
                tvDepartment.setVisibility(android.view.View.GONE);
                tvUnits.setVisibility(android.view.View.GONE);

                // 3. تعطيل الزر
                btnConfirmDonation.setEnabled(false);
                btnConfirmDonation.setAlpha(0.5f);

            } else {
                // في حال وجود طلب حقيقي: نرجع كل شيء ظاهر ونعرض البيانات
                tvHospitalName.setVisibility(android.view.View.VISIBLE);
                tvBloodType.setVisibility(android.view.View.VISIBLE);
                tvLocation.setVisibility(android.view.View.VISIBLE);
                tvDepartment.setVisibility(android.view.View.VISIBLE);
                tvUnits.setVisibility(android.view.View.VISIBLE);

                tvHospitalName.setText("🏥 اسم المستشفى: " + hospital);
                tvBloodType.setText("❤️ فصيلة الدم: " + (blood != null ? blood : "--"));
                tvLocation.setText("📍 الموقع: " + (city != null ? city : "غير محدد"));
                tvDepartment.setText("🚨 القسم: " + (dept != null && !dept.isEmpty() ? dept : "--"));
                tvUnits.setText("🩸 الوحدات المطلوبة: " + (units != null ? units : "0"));

                btnConfirmDonation.setEnabled(true);
                btnConfirmDonation.setAlpha(1.0f);
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