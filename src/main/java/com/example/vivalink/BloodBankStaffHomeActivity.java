package com.example.vivalink; // تأكدي من اسم الباكج تبعك

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

        // فحص إذا في مستخدم مسجل دخول
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "لم يتم العثور على مستخدم مسجل دخول", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // الدخول المباشر لنود الموظفين باستخدام الـ UID
        mRef = FirebaseDatabase.getInstance().getReference("BloodBankStaff").child(currentUid);

        loadStaffData();
    }

    private void loadStaffData() {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // سحب البيانات مباشرة بدون Loop
                    String name = snapshot.child("full_name").getValue(String.class);
                    String pos = snapshot.child("position").getValue(String.class);

                    tvStaffWelcome.setText("أهلاً بك، " + (name != null && !name.isEmpty() ? name : "موظف"));
                    tvStaffDetails.setText("المسمى الوظيفي: " + (pos != null && !pos.isEmpty() ? pos : "غير محدد"));
                } else {
                    Toast.makeText(BloodBankStaffHomeActivity.this, "بيانات الموظف غير موجودة", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BloodBankStaffHomeActivity.this, "خطأ: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
