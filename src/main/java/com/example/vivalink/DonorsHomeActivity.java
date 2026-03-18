package com.example.vivalink; // تأكدي من اسم الباكج تبعك

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DonorsHomeActivity extends AppCompatActivity {

    private TextView tvWelcomeDonor, tvDonationCount, tvLastDonationDate;
    private Button btnViewAllRequests;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donors_home);

        // ربط العناصر
        tvWelcomeDonor = findViewById(R.id.tvWelcomeDonor);
        tvDonationCount = findViewById(R.id.tvDonationCount);
        tvLastDonationDate = findViewById(R.id.tvLastDonationDate);
        btnViewAllRequests = findViewById(R.id.btnViewAllRequests);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference("Users"); // دخلنا مباشرة على نود Users

        loadDonorData();

        btnViewAllRequests.setOnClickListener(v -> {
            Toast.makeText(this, "سيتم فتح صفحة جميع الطلبات قريباً", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadDonorData() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();

        mRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // جلب البيانات حسب الأسماء المخزنة في الـ SignUp
                    String name = snapshot.child("fullName").getValue(String.class);

                    // استخدام الاسم الصحيح للحقل "lastDonation" كما في الـ SignUp
                    String lastDonation = snapshot.child("lastDonation").getValue(String.class);

                    // فحص عدد التبرعات (إذا مش موجود حطي 0)
                    Object countObj = snapshot.child("donationCount").getValue();
                    String count = (countObj != null) ? countObj.toString() : "0";

                    tvWelcomeDonor.setText("مرحباً بك، " + (name != null ? name : "متبرع"));
                    tvDonationCount.setText("عدد تبرعاتك: " + count);
                    tvLastDonationDate.setText("آخر تبرع: " + (lastDonation != null ? lastDonation : "لا يوجد"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DonorsHomeActivity.this, "خطأ: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}