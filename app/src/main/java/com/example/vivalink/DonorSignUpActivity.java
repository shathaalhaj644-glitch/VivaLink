package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.HashMap;
import java.util.regex.Pattern;

public class DonorSignUpActivity extends AppCompatActivity {

    // ضفت etDonationCount هنا
    private EditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword, etLastDonation, etDonationCount, etCity, etDiseaseName;
    private Spinner spBloodType;
    private RadioGroup rgHasDisease, rgDonationStatus;
    private RadioButton rbDiseaseYes, rbNeverDonated;
    private Button btnRegister, btnBackToLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_sign_up);

        initViews();
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference("Donors");

        // تعديل: إظهار وإخفاء حقل التاريخ وحقل "عدد التبرعات" معاً
        rgDonationStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbNeverDonated) {
                etLastDonation.setVisibility(View.GONE);
                etDonationCount.setVisibility(View.GONE); // إخفاء عدد التبرعات
            } else {
                etLastDonation.setVisibility(View.VISIBLE);
                etDonationCount.setVisibility(View.VISIBLE); // إظهار عدد التبرعات
            }
        });

        rgHasDisease.setOnCheckedChangeListener((group, checkedId) ->
                etDiseaseName.setVisibility(checkedId == R.id.rbDiseaseYes ? View.VISIBLE : View.GONE));

        btnRegister.setOnClickListener(v -> registerUser());
        btnBackToLogin.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etCity = findViewById(R.id.etCity);
        etDiseaseName = findViewById(R.id.etDiseaseName);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spBloodType = findViewById(R.id.spBloodType);
        etLastDonation = findViewById(R.id.etLastDonation);

        // ربط حقل عدد التبرعات (تأكدي من إضافة هذا الـ ID في الـ XML عندك)
        etDonationCount = findViewById(R.id.etDonationCount);

        rgHasDisease = findViewById(R.id.rgHasDisease);
        rbDiseaseYes = findViewById(R.id.rbDiseaseYes);
        rgDonationStatus = findViewById(R.id.rgDonationStatus);
        rbNeverDonated = findViewById(R.id.rbNeverDonated);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        // منطق التاريخ وعدد التبرعات
        String lastDonation;
        String donationCount;

        if (rbNeverDonated.isChecked()) {
            lastDonation = "لم أتبرع من قبل";
            donationCount = "0";
        } else {
            lastDonation = etLastDonation.getText().toString().trim();
            donationCount = etDonationCount.getText().toString().trim();

            // فحص بسيط عشان ما يترك عدد التبرعات فاضي إذا اختار "نعم"
            if (donationCount.isEmpty()) {
                etDonationCount.setError("يرجى إدخال عدد مرات التبرع");
                return;
            }
        }

        String hasDisease = rbDiseaseYes.isChecked() ? "نعم" : "لا";
        String diseaseName = rbDiseaseYes.isChecked() ? etDiseaseName.getText().toString().trim() : "سليم";

        // التحقق (Validation)
        if (fullName.isEmpty() || !Pattern.matches("^[a-zA-Z\\s]{5,}$", fullName)) {
            etFullName.setError("الاسم يجب أن يكون بالإنجليزية و 5 حروف على الأقل"); return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("يرجى إدخال بريد إلكتروني صحيح"); return;
        }
        if (!Pattern.matches("^[0-9]{10}$", phone)) {
            etPhone.setError("رقم الهاتف يجب أن يتكون من 10 أرقام"); return;
        }
        if (city.isEmpty()) { etCity.setError("يرجى إدخال اسم المدينة"); return; }

        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        if (!Pattern.matches(passwordPattern, password)) {
            etPassword.setError("كلمة السر ضعيفة"); return;
        }

        if (!password.equals(confirmPass)) {
            etConfirmPassword.setError("كلمتا المرور غير متطابقتين"); return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = mAuth.getCurrentUser().getUid();
                HashMap<String, Object> donorMap = new HashMap<>();
                donorMap.put("id", uid);
                donorMap.put("fullName", fullName);
                donorMap.put("email", email);
                donorMap.put("phone", phone);
                donorMap.put("city", city);
                donorMap.put("hasDiseases", hasDisease);
                donorMap.put("diseaseName", diseaseName);
                donorMap.put("lastDonation", lastDonation);

                // تخزين عدد التبرعات المدخل بدلاً من "0" ثابتة
                donorMap.put("donationCount", donationCount);

                donorMap.put("role", "Donor");

                mRef.child(uid).setValue(donorMap).addOnCompleteListener(saveTask -> {
                    if (saveTask.isSuccessful()) {
                        mAuth.signOut();
                        Toast.makeText(this, "تم إنشاء الحساب بنجاح! سجل دخولك الآن ✅", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(DonorSignUpActivity.this, LoginActivity.class));
                        finish();
                    }
                });
            } else {
                Toast.makeText(this, "خطأ: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}