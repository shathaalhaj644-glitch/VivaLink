package com.example.vivalink;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class HospitalEditProfileActivity extends AppCompatActivity {

    // التأكد من أن هذه الأسماء تطابق الـ IDs في الـ XML تماماً
    private EditText etHospitalName, etCity, etEmail, etPhone;
    private Button btnSaveChanges;
    private ImageView btnBack;

    private DatabaseReference dbRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_edit_profile);

        // ربط العناصر - لاحظي استخدام الأسماء الجديدة الموحدة
        etHospitalName = findViewById(R.id.etHospitalName);
        etCity = findViewById(R.id.etCity);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnBack = findViewById(R.id.btnBack);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getUid();
            dbRef = FirebaseDatabase.getInstance().getReference("Hospitals").child(currentUserId);

            // جلب البيانات لعرضها قبل التعديل
            loadData();
        }

        btnBack.setOnClickListener(v -> finish());
        btnSaveChanges.setOnClickListener(v -> updateData());
    }

    private void loadData() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateData() {
        String name = etHospitalName.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty() || city.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "يرجى ملئ جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("hospitalName", name);
        map.put("city", city);
        map.put("email", email);
        map.put("phone", phone);

        dbRef.updateChildren(map).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "تم تحديث البيانات بنجاح", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}