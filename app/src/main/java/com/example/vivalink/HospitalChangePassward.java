package com.example.vivalink;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HospitalChangePassward extends AppCompatActivity {

    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_change_passward);

        // ربط العناصر
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {

        String oldPass = etOldPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        // 🔴 تحقق من الحقول
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "كلمة السر الجديدة غير متطابقة", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(this, "كلمة السر يجب أن تكون 6 أحرف على الأقل", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "حدث خطأ، الرجاء تسجيل الدخول مرة أخرى", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔥 إعادة التوثيق (مهم جداً)
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), oldPass);

        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        // 🔥 تغيير كلمة المرور
                        user.updatePassword(newPass)
                                .addOnCompleteListener(task1 -> {

                                    if (task1.isSuccessful()) {
                                        Toast.makeText(this, "تم تغيير كلمة المرور بنجاح", Toast.LENGTH_SHORT).show();

                                        // 🔥 تسجيل خروج بعد التغيير (اختياري لكنه الأفضل)
                                        FirebaseAuth.getInstance().signOut();
                                        finish();

                                    } else {
                                        Toast.makeText(this, "فشل تغيير كلمة المرور", Toast.LENGTH_SHORT).show();
                                    }

                                });

                    } else {
                        Toast.makeText(this, "كلمة المرور القديمة غير صحيحة", Toast.LENGTH_SHORT).show();
                    }

                });
    }
}