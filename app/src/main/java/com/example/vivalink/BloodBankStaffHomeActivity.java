package com.example.vivalink;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class BloodBankStaffHomeActivity extends AppCompatActivity {
    private TextView tvStaffWelcome, tvStaffDetails;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbankstaff_home);

        tvStaffWelcome = findViewById(R.id.tvStaffWelcome);
        tvStaffDetails = findViewById(R.id.tvStaffDetails);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "لم يتم العثور على مستخدم", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // التأكد من أن المسار هو BloodBankStaff كما في الـ JSON
        mRef = FirebaseDatabase.getInstance().getReference("BloodBankStaff").child(currentUid);

        loadStaffData();
    }

    private void loadStaffData() {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String name = snapshot.child("name").getValue(String.class);
                    String role = snapshot.child("role").getValue(String.class);
                    String city = snapshot.child("city").getValue(String.class);

                    tvStaffWelcome.setText("أهلاً بك، " + (name != null ? name : "موظف"));
                    tvStaffDetails.setText("الدور: " + (role != null ? role : "Staff") + " | المدينة: " + city);
                } else {
                    Toast.makeText(BloodBankStaffHomeActivity.this, "بيانات الموظف غير موجودة في القاعدة", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BloodBankStaffHomeActivity.this, "خطأ: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}