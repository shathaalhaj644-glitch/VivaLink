package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vivalink.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailOrPhone, etPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. تعريف العناصر والفايربيس
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference("Users");
        etEmailOrPhone = findViewById(R.id.etLoginEmailOrPhone);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnSubmitLogin);

        // 2. عند الضغط على زر الدخول
        btnLogin.setOnClickListener(v -> {
            String input = etEmailOrPhone.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (input.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "يرجى تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
                return;
            }

            // إذا المدخل إيميل سجّل دخول مباشرة، إذا رقم هاتف ابحث عن الإيميل أولاً
            if (input.contains("@")) {
                signIn(input, pass);
            } else {
                findEmailByPhone(input, pass);
            }
        });
    }

    // البحث عن الإيميل باستخدام رقم الهاتف
    private void findEmailByPhone(String phone, String pass) {
        mRef.orderByChild("phoneNumber").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                        String email = userSnap.child("email").getValue(String.class);
                        signIn(email, pass);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "رقم الهاتف غير مسجل", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    // تسجيل الدخول الفعلي (Firebase Auth)
    private void signIn(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                checkUserRole(mAuth.getCurrentUser().getUid());
            } else {
                Toast.makeText(this, "فشل الدخول: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // التحقق من صلاحية المستخدم (Role) وتوجيهه للشاشة الصحيحة
    private void checkUserRole(String uid) {
        mRef.child(uid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);
                Intent intent;
                if ("donor".equals(role)) intent = new Intent(LoginActivity.this, DonorsHomeActivity.class);
                else if ("hospital".equals(role)) intent = new Intent(LoginActivity.this, HospitalsHomeActivity.class);
                else if ("staff".equals(role)) intent = new Intent(LoginActivity.this, BloodBankStaffHomeActivity.class);
                else intent = new Intent(LoginActivity.this, MainActivity.class);

                startActivity(intent);
                finish();
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }
}