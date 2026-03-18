package com.example.vivalink;

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

public class HospitalsHomeActivity extends AppCompatActivity {

    private TextView tvHospitalTitle, tvRequestStatus;
    private Button btnNewRequest;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitals_home);

        // ربط العناصر
        tvHospitalTitle = findViewById(R.id.tvHospitalTitle);
        tvRequestStatus = findViewById(R.id.tvRequestStatus);
        btnNewRequest = findViewById(R.id.btnNewRequest);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "لم يتم العثور على مستخدم", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                    String hospitalName = snapshot.child("hospital_name").getValue(String.class);
                    String requestStatus = snapshot.child("request_status").getValue(String.class);

                    tvHospitalTitle.setText("مستشفى: " + (hospitalName != null ? hospitalName : "غير معروف"));
                    tvRequestStatus.setText("حالة الطلبات: " + (requestStatus != null ? requestStatus : "لا يوجد"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HospitalsHomeActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}