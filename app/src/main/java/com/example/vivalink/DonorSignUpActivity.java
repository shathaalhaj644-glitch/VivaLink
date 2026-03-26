package com.example.vivalink;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.HashMap;
import java.util.regex.Pattern;

public class DonorSignUpActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword, etLastDonation, etCity, etDiseaseName;
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

        // إخفاء وإظهار الحقول بناءً على الاختيارات
        rgDonationStatus.setOnCheckedChangeListener((group, checkedId) ->
                etLastDonation.setVisibility(checkedId == R.id.rbNeverDonated ? View.GONE : View.VISIBLE));

        rgHasDisease.setOnCheckedChangeListener((group, checkedId) ->
                etDiseaseName.setVisibility(checkedId == R.id.rbDiseaseYes ? View.VISIBLE : View.GONE));

        // زر إنشاء الحساب
        btnRegister.setOnClickListener(v -> registerUser());

        // زر العودة (السكني)
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

        String lastDonation = rbNeverDonated.isChecked() ? "لم أتبرع من قبل" : etLastDonation.getText().toString().trim();
        String hasDisease = rbDiseaseYes.isChecked() ? "نعم" : "لا";
        String diseaseName = rbDiseaseYes.isChecked() ? etDiseaseName.getText().toString().trim() : "سليم";

        // التحقق (Validation) بالعربي
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

        // شرط كلمة المرور (رقم، حرف كبير، حرف صغير، رمز)
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        if (!Pattern.matches(passwordPattern, password)) {
            etPassword.setError("كلمة السر يجب أن تكون 8 خانات، وتحتوي على حرف كبير وصغير ورقم ورمز"); return;
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
                donorMap.put("donationCount", "0");
                donorMap.put("role", "Donor");

                mRef.child(uid).setValue(donorMap).addOnCompleteListener(saveTask -> {
                    if (saveTask.isSuccessful()) {
                        Toast.makeText(this, "تم إنشاء الحساب بنجاح ✅", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else {
                Toast.makeText(this, "خطأ: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}