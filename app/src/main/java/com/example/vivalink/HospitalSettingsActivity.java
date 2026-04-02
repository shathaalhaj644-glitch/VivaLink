package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HospitalSettingsActivity extends AppCompatActivity {

    private TextView tvHospitalName, tvCity, tvEmail;
    private Button btnEditProfile, btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_settings);

        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvCity = findViewById(R.id.tvCity);
        tvEmail = findViewById(R.id.tvEmail);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // مثال تعبئة بيانات (ممكن تجيبها من Firebase)
        tvHospitalName.setText("مستشفى الاستشاري العربي");
        tvCity.setText("رام الله");
        tvEmail.setText("istishari_hosp@gmail.com");

        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(this, HospitalEditProfileActivity.class))
        );

        btnChangePassword.setOnClickListener(v ->
                startActivity(new Intent(this, HospitalChangePassward.class))
        );
    }
}
