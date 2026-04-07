package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Random;

public class VerifyCodeActivity extends AppCompatActivity {

    EditText etCode;
    Button btnVerify;
    TextView tvResend;

    String generatedCode;

    String fullName, email, phone, city, bloodType, password,
            hemoglobin, lastDonation, donationCount, hasDisease, diseaseName;

    FirebaseAuth mAuth;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        etCode = findViewById(R.id.etCode);
        btnVerify = findViewById(R.id.btnVerify);
        tvResend = findViewById(R.id.tvResend);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference("Donors");

        Intent intent = getIntent();
        fullName = intent.getStringExtra("fullName");
        email = intent.getStringExtra("email");
        phone = intent.getStringExtra("phone");
        city = intent.getStringExtra("city");
        bloodType = intent.getStringExtra("bloodType");
        password = intent.getStringExtra("password");
        hemoglobin = intent.getStringExtra("hemoglobin");
        lastDonation = intent.getStringExtra("lastDonation");
        donationCount = intent.getStringExtra("donationCount");
        hasDisease = intent.getStringExtra("hasDisease");
        diseaseName = intent.getStringExtra("diseaseName");

        generateCode();

        btnVerify.setOnClickListener(v -> {
            String enteredCode = etCode.getText().toString().trim();

            if (enteredCode.length() != 6) {
                Toast.makeText(this, "أدخل كود من 6 أرقام", Toast.LENGTH_SHORT).show();
                return;
            }

            if (enteredCode.equals(generatedCode)) {
                btnVerify.setEnabled(false);
                createAccount();
            } else {
                Toast.makeText(this, "الكود غير صحيح ❌", Toast.LENGTH_SHORT).show();
            }
        });

        tvResend.setOnClickListener(v -> generateCode());
    }

    private void generateCode() {
        Random random = new Random();
        generatedCode = String.valueOf(100000 + random.nextInt(900000));

        // إظهار الكود بشكل بارز جداً للمستخدم (Simulation)
        Toast.makeText(this, "🔔 نظام التحقق: رمز التأكيد الخاص بك هو [" + generatedCode + "]", Toast.LENGTH_LONG).show();
    }

    private void createAccount() {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                String uid = mAuth.getCurrentUser().getUid();

                HashMap<String, Object> map = new HashMap<>();
                map.put("id", uid);
                map.put("fullName", fullName);
                map.put("email", email);
                map.put("phone", phone);
                map.put("city", city);
                map.put("bloodType", bloodType);
                map.put("hemoglobin", hemoglobin);
                map.put("lastDonation", lastDonation);
                map.put("donationCount", donationCount);
                map.put("hasDisease", hasDisease);
                map.put("diseaseName", diseaseName);
                map.put("role", "Donor");
                map.put("sendCode", true);

                mRef.child(uid).setValue(map).addOnCompleteListener(saveTask -> {
                    if (saveTask.isSuccessful()) {
                        Toast.makeText(this, "تم إنشاء الحساب بنجاح ✅", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finishAffinity();
                    }
                });

            } else {
                btnVerify.setEnabled(true);
                Toast.makeText(this, "خطأ: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}