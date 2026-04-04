package com.example.vivalink;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class HospitalEditProfileActivity extends AppCompatActivity {

    private EditText etHospitalName, etCity, etEmail, etPhone;
    private Button btnSaveChanges;
    private ImageView btnBack;

    private DatabaseReference dbRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_edit_profile);


        etHospitalName = findViewById(R.id.etHospitalName);
        etCity = findViewById(R.id.etCity);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnBack = findViewById(R.id.btnBack);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getUid();
            dbRef = FirebaseDatabase.getInstance()
                    .getReference("Hospitals")
                    .child(currentUserId);

            loadData();
        }

        btnBack.setOnClickListener(v -> finish());

        btnSaveChanges.setOnClickListener(v -> updateData());
    }


    private void loadData() {
        dbRef.addValueEventListener(new ValueEventListener() { // 🔥 مهم
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etHospitalName.setText(snapshot.child("hospitalName").getValue(String.class));
                    etCity.setText(snapshot.child("city").getValue(String.class));
                    etEmail.setText(snapshot.child("email").getValue(String.class));
                    etPhone.setText(snapshot.child("phone").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HospitalEditProfileActivity.this, "خطأ في جلب البيانات", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateData() {
        String name = etHospitalName.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(city) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)) {

            Toast.makeText(this, "يرجى تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("hospitalName", name);
        map.put("city", city);
        map.put("email", email);
        map.put("phone", phone);

        dbRef.updateChildren(map)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "تم تحديث البيانات بنجاح ✅", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "فشل التحديث ❌", Toast.LENGTH_SHORT).show();
                });
    }
}