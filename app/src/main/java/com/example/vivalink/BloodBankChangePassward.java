package com.example.vivalink;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BloodBankChangePassward extends AppCompatActivity {

    private EditText et_old_password, et_new_password, et_confirm_password;
    private Button btn_confirm_change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbank_change_passward);

        et_old_password = findViewById(R.id.et_old_password);
        et_new_password = findViewById(R.id.et_new_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        btn_confirm_change = findViewById(R.id.btn_confirm_change);

        btn_confirm_change.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPass = et_old_password.getText().toString().trim();
        String newPass = et_new_password.getText().toString().trim();
        String confirmPass = et_confirm_password.getText().toString().trim();

        if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "يرجى تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "كلمة المرور الجديدة غير متطابقة مع التأكيد", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPass).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(this, "تم التحديث بنجاح ✅", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(this, "كلمة المرور الحالية غير صحيحة ❌", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}