package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailOrPhone, etPassword;
    private Button btnLogin, btnGoToSignUp;
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

        btnGoToSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, DonorSignUpActivity.class));
        });
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
                    // الآن نفحص الرتبة أولاً قبل طلب التحقق
                    checkUserRoleAndVerify(user);
                }
            } else {
                Toast.makeText(this, "فشل الدخول: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserRoleAndVerify(FirebaseUser user) {
        String uid = user.getUid();

        // 1. نبحث أولاً في جدول المتبرعين
        mRootRef.child("Donors").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // المستخدم متبرع (donor) -> الآن فقط نفحص رابط التحقق
                    if (user.isEmailVerified()) {
                        startActivity(new Intent(LoginActivity.this, DonorsHomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "يرجى تفعيل حسابك كمتبرع من الرابط المرسل لإيميلك أولاً ✅", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                } else {
                    // 2. إذا لم يكن متبرع، نبحث في الجداول الأخرى (دخول مباشر)
                    checkManagementRoles(uid);
                }
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    private void checkManagementRoles(String uid) {
        // فحص المستشفيات والموظفين - هؤلاء لا يحتاجون رابط تفعيل
        String[] nodes = {"Hospitals", "BloodBankStaff"};

        for (String node : nodes) {
            mRootRef.child(node).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String role = snapshot.child("role").getValue(String.class);
                        Intent intent = null;

                        // تذكري: donor بالسمول، أما hospital و BankStaff حسب ما خزنتيهم
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