package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailOrPhone, etPassword;
    private Button btnLogin, btnGoToSignUp;
    private TextView tvForgotPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        etEmailOrPhone = findViewById(R.id.etLoginEmailOrPhone);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnSubmitLogin);
        btnGoToSignUp = findViewById(R.id.btnGoToSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> {
            String input = etEmailOrPhone.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (input.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "يرجى تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
                return;
            }

            if (input.contains("@")) {
                signIn(input, pass);
            } else {
                findEmailByPhone(input, pass);
            }
        });


        tvForgotPassword.setOnClickListener(v -> {
            showForgotPasswordDialog();
        });

        btnGoToSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, DonorSignUpActivity.class));
        });
    }


    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إعادة تعيين كلمة المرور");
        builder.setMessage("يرجى إدخال بريدك الإلكتروني لإرسال رابط التغيير:");

        final EditText etResetEmail = new EditText(this);
        etResetEmail.setHint("example@email.com");
        etResetEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(50, 20, 50, 0);
        etResetEmail.setLayoutParams(params);
        container.addView(etResetEmail);
        builder.setView(container);

        builder.setPositiveButton("إرسال", (dialog, which) -> {
            String email = etResetEmail.getText().toString().trim();
            if (!email.isEmpty()) {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "تم إرسال الرابط بنجاح ✅", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "فشل الإرسال: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }

    private void findEmailByPhone(String phone, String pass) {
        mRootRef.child("Donors").orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void signIn(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    checkUserRoleAndVerify(user);
                }
            } else {
                Toast.makeText(this, "فشل الدخول: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserRoleAndVerify(FirebaseUser user) {
        String uid = user.getUid();
        mRootRef.child("Donors").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (user.isEmailVerified()) {
                        startActivity(new Intent(LoginActivity.this, DonorsHomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "يرجى تفعيل الحساب من الإيميل أولاً ✅", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                } else {
                    checkManagementRoles(uid);
                }
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    private void checkManagementRoles(String uid) {
        String[] nodes = {"Hospitals", "BloodBankStaff"};
        for (String node : nodes) {
            mRootRef.child(node).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String role = snapshot.child("role").getValue(String.class);
                        Intent intent = null;
                        if ("hospital".equalsIgnoreCase(role)) {
                            intent = new Intent(LoginActivity.this, HospitalHomeActivity.class);
                        } else if ("BankStaff".equalsIgnoreCase(role)) {
                            intent = new Intent(LoginActivity.this, BloodBankStaffHomeActivity.class);
                        }
                        if (intent != null) {
                            startActivity(intent);
                            finish();
                        }
                    }
                }
                @Override public void onCancelled(DatabaseError error) {}
            });
        }
    }
}