package com.example.vivalink;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;

public class DonateActivity extends AppCompatActivity {
    private TextView tvBloodType, tvHospitalName, tvLocation, tvDepartment, tvUnits, tvTimeDisplay, btnBack;
    private EditText etPhone, etConfirmPhone;
    private Button btnConfirmDonation;
    private String selectedTime = ""; // هذا المتغير سيحفظ الوقت مع (صباحاً/مساءً) لرفعه لقاعدة البيانات

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        initViews();
        displayPassedData();

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // عند الضغط على حقل الوقت يفتح الساعة
        tvTimeDisplay.setOnClickListener(v -> openTimePicker());

        btnConfirmDonation.setOnClickListener(v -> handleDonation());
    }

    private void initViews() {
        tvBloodType = findViewById(R.id.tvBloodType);
        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvLocation = findViewById(R.id.tvCity);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvUnits = findViewById(R.id.tvUnits);
        tvTimeDisplay = findViewById(R.id.tvTimeDisplay);

        btnBack = findViewById(R.id.btnBack);
        etPhone = findViewById(R.id.etPhone);
        etConfirmPhone = findViewById(R.id.etConfirmPhone);
        btnConfirmDonation = findViewById(R.id.btnConfirmDonation);
    }

    private void openTimePicker() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        // تم تغيير true إلى false لتفعيل نظام AM/PM
        TimePickerDialog timePicker = new TimePickerDialog(DonateActivity.this, (view, hourOfDay, selectedMinute) -> {

            // تحديد الفترة (صباحاً أو مساءً)
            String am_pm = (hourOfDay < 12) ? "صباحاً" : "مساءً";

            // تحويل الساعة من نظام 24 ساعة إلى نظام 12 ساعة
            int hourIn12Format = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
            if (hourIn12Format == 0) hourIn12Format = 12;

            // تنسيق النص النهائي المخزن
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hourIn12Format, selectedMinute, am_pm);

            // عرض الوقت المختار للمستخدم
            tvTimeDisplay.setText("⏰ الوقت المختار: " + selectedTime);

        }, hour, minute, false); // false تظهر خيارات AM/PM في الساعة

        timePicker.setTitle("اختر وقت التبرع");
        timePicker.show();
    }

    private void displayPassedData() {
        if (getIntent() != null) {
            String blood = getIntent().getStringExtra("bloodType");
            String hospital = getIntent().getStringExtra("hospitalName");
            String city = getIntent().getStringExtra("city");
            String dept = getIntent().getStringExtra("department");
            String units = getIntent().getStringExtra("units");

            if (hospital == null || hospital.isEmpty() || hospital.contains("لا يوجد")) {
                tvHospitalName.setText("لا يوجد طلبات حالياً لتبرعك");
                hideViews();
            } else {
                showViews();
                tvHospitalName.setText("🏥 " + hospital);
                tvBloodType.setText("❤️ فصيلة الدم: " + (blood != null ? blood : "--"));
                tvLocation.setText("📍 المدينة: " + (city != null ? city : "غير محدد"));
                tvDepartment.setText("🚨 القسم: " + (dept != null ? dept : "--"));
                tvUnits.setText("🩸 الوحدات: " + (units != null ? units : "0"));
            }
        }
    }

    private void hideViews() {
        tvBloodType.setVisibility(View.GONE);
        tvLocation.setVisibility(View.GONE);
        tvDepartment.setVisibility(View.GONE);
        tvUnits.setVisibility(View.GONE);
        tvTimeDisplay.setVisibility(View.GONE);
        btnConfirmDonation.setEnabled(false);
        btnConfirmDonation.setAlpha(0.5f);
    }

    private void showViews() {
        tvBloodType.setVisibility(View.VISIBLE);
        tvLocation.setVisibility(View.VISIBLE);
        tvDepartment.setVisibility(View.VISIBLE);
        tvUnits.setVisibility(View.VISIBLE);
        tvTimeDisplay.setVisibility(View.VISIBLE);
        btnConfirmDonation.setEnabled(true);
        btnConfirmDonation.setAlpha(1.0f);
    }

    private void handleDonation() {
        String p1 = etPhone.getText().toString().trim();
        String p2 = etConfirmPhone.getText().toString().trim();

        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "يرجى اختيار وقت أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        if (p1.isEmpty() || !p1.equals(p2)) {
            Toast.makeText(this, "يرجى التأكد من تطابق أرقام الهاتف", Toast.LENGTH_SHORT).show();
            return;
        }

        // نجاح العملية وإظهار الوقت المختار مع الفترة (صباحاً/مساءً)
        Toast.makeText(this, "تم تأكيد موعدك الساعة " + selectedTime + " ✅", Toast.LENGTH_LONG).show();
        finish();
    }
}