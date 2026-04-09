package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DonorSignUpActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword,
            etLastDonation, etDonationCount, etDiseaseName, etBloodLevel, etLastBloodTest;
    private Spinner spBloodType, spCity, spCountryCode;
    private RadioGroup rgHasDisease, rgDonationStatus;
    private RadioButton rbDiseaseYes, rbNeverDonated;
    private Button btnRegister, btnBackToLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference("Donors");

        initViews();
        setupSpinners();

        rgDonationStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbNeverDonated) {
                etLastDonation.setVisibility(View.GONE);
                etDonationCount.setVisibility(View.GONE);
            } else {
                etLastDonation.setVisibility(View.VISIBLE);
                etDonationCount.setVisibility(View.VISIBLE);
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
        etDiseaseName = findViewById(R.id.etDiseaseName);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etLastDonation = findViewById(R.id.etLastDonation);
        etDonationCount = findViewById(R.id.etDonationCount);
        etBloodLevel = findViewById(R.id.etBloodLevel);
        etLastBloodTest = findViewById(R.id.etLastBloodTest);
        spBloodType = findViewById(R.id.spBloodType);
        spCity = findViewById(R.id.spCity);
        spCountryCode = findViewById(R.id.spCountryCode);
        rgHasDisease = findViewById(R.id.rgHasDisease);
        rbDiseaseYes = findViewById(R.id.rbDiseaseYes);
        rgDonationStatus = findViewById(R.id.rgDonationStatus);
        rbNeverDonated = findViewById(R.id.rbNeverDonated);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
    }

    private void setupSpinners() {
        String[] codes = {"+970", "+972"};
        ArrayAdapter<String> codeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, codes);
        spCountryCode.setAdapter(codeAdapter);

        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bloodTypes);
        spBloodType.setAdapter(bloodAdapter);

        String[] cities = {"نابلس", "طولكرم", "رام الله", "بيت لحم", "الخليل", "البيرة", "جنين", "سلفيت", "أريحا", "طوباس", "قلقيلية"};
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        spCity.setAdapter(cityAdapter);
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "يرجى تعبئة الحقول الأساسية", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPass)) {
            Toast.makeText(this, "كلمات المرور غير متطابقة", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);

        // 1. إنشاء الحساب
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // 2. إرسال رابط التفعيل فوراً
                    user.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                        if (verifyTask.isSuccessful()) {

                            // 3. حفظ البيانات في الداتابيز مع رول donor سمول
                            saveUserData(user.getUid());

                            // 4. تسجيل خروج عشان يضطر يفعل من الرابط قبل ما يسجل دخول
                            mAuth.signOut();

                            Toast.makeText(DonorSignUpActivity.this, "تم التسجيل بنجاح! يرجى تفعيل حسابك من الرابط المرسل لبريدك ✅", Toast.LENGTH_LONG).show();

                            // 5. الانتقال لصفحة تسجيل الدخول
                            Intent intent = new Intent(DonorSignUpActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            } else {
                btnRegister.setEnabled(true);
                Toast.makeText(this, "خطأ: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(String uid) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", uid);
        map.put("fullName", etFullName.getText().toString().trim());
        map.put("email", etEmail.getText().toString().trim());
        map.put("phone", spCountryCode.getSelectedItem().toString() + etPhone.getText().toString().trim());
        map.put("city", spCity.getSelectedItem().toString());
        map.put("bloodType", spBloodType.getSelectedItem().toString());
        map.put("bloodLevel", etBloodLevel.getText().toString().trim());
        map.put("lastBloodTest", etLastBloodTest.getText().toString().trim());


        map.put("role", "donor");

        mRef.child(uid).setValue(map);
    }
}