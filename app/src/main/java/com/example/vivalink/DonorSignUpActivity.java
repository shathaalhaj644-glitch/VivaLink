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

    private EditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword, etLastDonation;
    private Spinner spBloodType, spCity;
    private RadioGroup rgHasDisease, rgDonationStatus;
    private RadioButton rbDiseaseYes, rbNeverDonated;
    private Button btnRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_sign_up);

        initViews();

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference("Donors"); // نخزن في Donors

        // إخفاء حقل التاريخ إذا اختار "لم أتبرع قط"
        rgDonationStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbNeverDonated) {
                etLastDonation.setVisibility(View.GONE);
            } else {
                etLastDonation.setVisibility(View.VISIBLE);
            }
        });

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spBloodType = findViewById(R.id.spBloodType);
        spCity = findViewById(R.id.spCity);
        etLastDonation = findViewById(R.id.etLastDonation);
        rgHasDisease = findViewById(R.id.rgHasDisease);
        rbDiseaseYes = findViewById(R.id.rbDiseaseYes);
        rgDonationStatus = findViewById(R.id.rgDonationStatus);
        rbNeverDonated = findViewById(R.id.rbNeverDonated);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        String bloodType = (spBloodType.getSelectedItem() != null) ? spBloodType.getSelectedItem().toString() : "";
        String city = (spCity.getSelectedItem() != null) ? spCity.getSelectedItem().toString() : "";

        String hasDisease = rbDiseaseYes.isChecked() ? "Yes" : "No";
        String lastDonation = rbNeverDonated.isChecked() ? "Never" : etLastDonation.getText().toString().trim();

        // --- التحقق (Validation) ---
        if (!Pattern.matches("^[a-zA-Z\\s]{5,}$", fullName)) {
            etFullName.setError("الاسم يجب أن يكون بالإنجليزية و 5 حروف على الأقل");
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("يرجى إدخال بريد إلكتروني صحيح");
            return;
        }

        if (!Pattern.matches("^[0-9]{10}$", phone)) {
            etPhone.setError("رقم الهاتف يجب أن يكون 10 أرقام");
            return;
        }

        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        if (!Pattern.matches(passwordPattern, password)) {
            etPassword.setError("كلمة السر يجب أن تكون 8 خانات، وتحتوي على حرف كبير وصغير ورقم ورمز");
            return;
        }

        if (!password.equals(confirmPass)) {
            etConfirmPassword.setError("كلمتا المرور غير متطابقتين");
            return;
        }

        // --- تنفيذ عملية التسجيل ---
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = mAuth.getCurrentUser().getUid();

                HashMap<String, Object> donorMap = new HashMap<>();
                donorMap.put("id", uid);
                donorMap.put("fullName", fullName);
                donorMap.put("email", email);
                donorMap.put("phone", phone);
                donorMap.put("bloodType", bloodType);
                donorMap.put("city", city);
                donorMap.put("hasDiseases", hasDisease);
                donorMap.put("lastDonation", lastDonation);
                donorMap.put("donationCount", "0"); // مهم لصفحة الـ Home
                donorMap.put("role", "Donor");

                mRef.child(uid).setValue(donorMap).addOnCompleteListener(saveTask -> {
                    if (saveTask.isSuccessful()) {
                        Toast.makeText(DonorSignUpActivity.this, "تم إنشاء الحساب بنجاح ✅", Toast.LENGTH_SHORT).show();
                        finish(); // يرجع للصفحة السابقة أو يفتح الـ Home
                    } else {
                        Toast.makeText(this, "فشل حفظ البيانات: " + saveTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "فشل التسجيل: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
