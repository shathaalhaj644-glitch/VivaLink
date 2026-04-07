package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Pattern;

public class DonorSignUpActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword,
            etLastDonation, etDonationCount, etDiseaseName, etBloodLevel;
    private Spinner spBloodType, spCity;
    private RadioGroup rgHasDisease, rgDonationStatus;
    private RadioButton rbDiseaseYes, rbNeverDonated;
    private Button btnRegister, btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_sign_up);

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

        spBloodType = findViewById(R.id.spBloodType);
        spCity = findViewById(R.id.spCity);

        rgHasDisease = findViewById(R.id.rgHasDisease);
        rbDiseaseYes = findViewById(R.id.rbDiseaseYes);
        rgDonationStatus = findViewById(R.id.rgDonationStatus);
        rbNeverDonated = findViewById(R.id.rbNeverDonated);

        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
    }

    private void setupSpinners() {
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bloodTypes);
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBloodType.setAdapter(bloodAdapter);

        String[] cities = {"نابلس", "طولكرم", "رام الله", "بيت لحم", "الخليل", "البيرة", "جنين", "سلفيت", "أريحا", "طوباس", "قلقيلية"};
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCity.setAdapter(cityAdapter);
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String city = spCity.getSelectedItem().toString();
        String bloodType = spBloodType.getSelectedItem().toString();
        String bloodLevel = etBloodLevel.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        String lastDonation;
        String donationCount;

        if (rbNeverDonated.isChecked()) {
            lastDonation = "لم أقم بالتبرع من قبل";
            donationCount = "0";
        } else {
            lastDonation = etLastDonation.getText().toString().trim();
            donationCount = etDonationCount.getText().toString().trim();
            if (donationCount.isEmpty()) return;
        }

        String hasDisease = rbDiseaseYes.isChecked() ? "نعم" : "لا";
        String diseaseName = rbDiseaseYes.isChecked() ? etDiseaseName.getText().toString().trim() : "سليم";

        if (fullName.isEmpty() || !Pattern.matches("^[a-zA-Z\\s]{5,}$", fullName)) return;
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) return;
        if (!Pattern.matches("^[0-9]{10}$", phone)) return;
        if (bloodLevel.isEmpty()) return;

        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        if (!Pattern.matches(passwordPattern, password)) return;
        if (!password.equals(confirmPass)) return;

        Intent intent = new Intent(this, VerifyCodeActivity.class);

        intent.putExtra("fullName", fullName);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("city", city);
        intent.putExtra("bloodType", bloodType);
        intent.putExtra("bloodLevel", bloodLevel);
        intent.putExtra("password", password);
        intent.putExtra("lastDonation", lastDonation);
        intent.putExtra("donationCount", donationCount);
        intent.putExtra("hasDisease", hasDisease);
        intent.putExtra("diseaseName", diseaseName);

        startActivity(intent);
    }
}