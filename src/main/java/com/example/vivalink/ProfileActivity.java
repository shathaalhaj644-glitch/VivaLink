package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone, tvCity, tvBlood;
    private Button btnEdit, btnChangePass, btnLogout;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // ربط العناصر
        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        tvPhone = findViewById(R.id.tvProfilePhone);
        tvCity = findViewById(R.id.tvProfileCity);
        tvBlood = findViewById(R.id.tvProfileBlood);
        btnEdit = findViewById(R.id.btnEditProfile);
        btnChangePass = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance().getReference("Donors").child(uid);
            loadUserData();
        }

        btnEdit.setOnClickListener(v -> showEditDialog());
        btnChangePass.setOnClickListener(v -> showPasswordDialog());
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tvName.setText("الاسم: " + snapshot.child("fullName").getValue(String.class));
                    tvEmail.setText("البريد: " + snapshot.child("email").getValue(String.class));
                    tvPhone.setText("الهاتف: " + snapshot.child("phone").getValue(String.class));
                    tvCity.setText("المدينة: " + snapshot.child("city").getValue(String.class));
                    tvBlood.setText("فصيلة الدم: " + snapshot.child("bloodType").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تعديل الحساب");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 20);

        final EditText inputName = new EditText(this);
        inputName.setHint("الاسم الجديد");
        final EditText inputPhone = new EditText(this);
        inputPhone.setHint("الهاتف الجديد");
        final EditText inputCity = new EditText(this);
        inputCity.setHint("المدينة الجديدة");
        final EditText inputBlood = new EditText(this);
        inputBlood.setHint("فصيلة الدم الجديدة");

        layout.addView(inputName);
        layout.addView(inputPhone);
        layout.addView(inputCity);
        layout.addView(inputBlood);
        builder.setView(layout);

        builder.setPositiveButton("حفظ", (dialog, which) -> {
            HashMap<String, Object> updates = new HashMap<>();
            String name = inputName.getText().toString().trim();
            String phone = inputPhone.getText().toString().trim();
            String city = inputCity.getText().toString().trim();
            String blood = inputBlood.getText().toString().trim();

            if (!name.isEmpty()) updates.put("fullName", name);
            if (!phone.isEmpty()) updates.put("phone", phone);
            if (!city.isEmpty()) updates.put("city", city);
            if (!blood.isEmpty()) updates.put("bloodType", blood);

            if (!updates.isEmpty()) {
                userRef.updateChildren(updates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Toast.makeText(this, "تم التحديث", Toast.LENGTH_SHORT).show();
                });
            }
        });
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }

    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تغيير كلمة المرور");
        final EditText inputPass = new EditText(this);
        inputPass.setHint("كلمة مرور جديدة");
        builder.setView(inputPass);

        builder.setPositiveButton("تحديث", (dialog, which) -> {
            String newPassword = inputPass.getText().toString().trim();
            if (isPasswordValid(newPassword)) {
                updatePasswordInFirebase(newPassword);
            } else {
                Toast.makeText(this, "كلمة مرور ضعيفة!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }

    private boolean isPasswordValid(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
    }

    private void updatePasswordInFirebase(String newPass) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "تم تغيير كلمة المرور", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "فشل التحديث، أعد تسجيل الدخول", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
} // القوس الأخير لإغلاق الكلاس