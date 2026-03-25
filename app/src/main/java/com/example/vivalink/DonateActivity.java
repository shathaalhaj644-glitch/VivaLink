package com.example.vivalink;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
import java.util.HashMap;

public class DonateActivity extends AppCompatActivity {

    private Spinner spinnerHospitals;
    private Button btnDate, btnTime, btnConfirm;
    private TextView tvDate, tvTime;
    private String selectedDate = "", selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        // ربط العناصر مع الـ XML
        spinnerHospitals = findViewById(R.id.spinnerHospitals);
        btnDate = findViewById(R.id.btnSelectDate);
        btnTime = findViewById(R.id.btnSelectTime);
        btnConfirm = findViewById(R.id.btnConfirmDonate);
        tvDate = findViewById(R.id.tvSelectedDate);
        tvTime = findViewById(R.id.tvSelectedTime);

        // تعبئة قائمة المستشفيات من بياناتك
        String[] hospitals = {"Palestine Medical Complex", "Rafidia Government Hospital", "Al-Ahli Hospital", "Ibn Sina Specialized Hospital"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hospitals);
        spinnerHospitals.setAdapter(adapter);

        // اختيار التاريخ
        btnDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                tvDate.setText("التاريخ: " + selectedDate);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // اختيار الوقت
        btnTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                tvTime.setText("الوقت: " + selectedTime);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        // تأكيد الحجز وحفظه في Appointments
        btnConfirm.setOnClickListener(v -> saveAppointment());
    }

    private void saveAppointment() {
        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "يرجى اختيار التاريخ والوقت أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Appointments").push();

        HashMap<String, Object> map = new HashMap<>();
        map.put("donorId", uid);
        map.put("hospitalName", spinnerHospitals.getSelectedItem().toString());
        map.put("date", selectedDate);
        map.put("time", selectedTime);
        map.put("status", "Pending");

        ref.setValue(map).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "تم حجز موعد التبرع بنجاح!", Toast.LENGTH_LONG).show();
            finish(); // العودة لصفحة الهوم
        });
    }
}