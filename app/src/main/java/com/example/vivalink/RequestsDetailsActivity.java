package com.example.vivalink;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RequestsDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        TextView tvBlood = findViewById(R.id.tvDetBlood);
        TextView tvHospital = findViewById(R.id.tvDetHospital);
        TextView tvCity = findViewById(R.id.tvDetCity);
        TextView tvDept = findViewById(R.id.tvDetDept);
        TextView tvUnits = findViewById(R.id.tvDetUnits);
        TextView tvDate = findViewById(R.id.tvDetDate);

        String blood = getIntent().getStringExtra("bloodType");
        String hospital = getIntent().getStringExtra("hospitalName");
        String city = getIntent().getStringExtra("city");
        String dept = getIntent().getStringExtra("department");
        String units = getIntent().getStringExtra("units");
        String rawDate = getIntent().getStringExtra("date");

        if (blood != null) tvBlood.setText("فصيلة الدم: " + blood);
        if (hospital != null) tvHospital.setText(hospital);
        if (city != null) tvCity.setText(city);
        if (dept != null) tvDept.setText(dept);
        if (units != null) tvUnits.setText("الوحدات المطلوبة: " + units);

        // عرض التاريخ بالأرقام الإنجليزية
        tvDate.setText(formatBloodDate(rawDate));
    }

    private String formatBloodDate(String rawDate) {
        if (rawDate == null) return "";
        try {
            // المحلل لصيغة الفايربيس (كما هي في الصور)
            SimpleDateFormat parser = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH);
            Date date = parser.parse(rawDate);

            // التعديل السحري هنا: Locale.ENGLISH بتخلي الأرقام إنجليزية غصب عن لغة الجهاز
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.ENGLISH);
            return formatter.format(date);
        } catch (Exception e) {
            return rawDate;
        }
    }
}