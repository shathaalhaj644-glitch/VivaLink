package com.example.vivalink;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class HospitalsHomeActivity extends AppCompatActivity {
    private TextView tvHospitalTitle, tvRequestStatus;
    private Button btnNewRequest;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitals_home);

        tvHospitalTitle = findViewById(R.id.tvHospitalTitle);
        tvRequestStatus = findViewById(R.id.tvRequestStatus);
        btnNewRequest = findViewById(R.id.btnNewRequest);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "لم يتم العثور على مستخدم", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // التأكد من أن المسار هو Hospitals
        mRef = FirebaseDatabase.getInstance().getReference("Hospitals").child(uid);

        loadHospitalData();

        btnNewRequest.setOnClickListener(v -> {
            Toast.makeText(this, "هذه الخاصية ستتوفر قريباً", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadHospitalData() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // التعديل هنا: استخدام "name" بدلاً من "hospital_name"
                    String hospitalName = snapshot.child("name").getValue(String.class);
                    String city = snapshot.child("city").getValue(String.class);

                    tvHospitalTitle.setText("مستشفى: " + (hospitalName != null ? hospitalName : "غير معروف"));
                    tvRequestStatus.setText("الموقع: " + (city != null ? city : "غير محدد"));
                } else {
                    tvHospitalTitle.setText("بيانات المستشفى غير موجودة");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HospitalsHomeActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}