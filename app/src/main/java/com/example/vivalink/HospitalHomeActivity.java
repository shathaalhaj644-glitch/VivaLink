package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HospitalHomeActivity extends AppCompatActivity {

    private TextView tvHospitalName, tvHospitalLocation, valTotalRequests, valPending, valDonors;
    private CardView btnCreateRequestCard, btnViewDonorsCard, btnSettingsCard;
    private DatabaseReference dbRef;
    private String currentHospitalUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_home);

        // 1. ربط العناصر بالـ XML
        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvHospitalLocation = findViewById(R.id.tvHospitalLocation);
        valTotalRequests = findViewById(R.id.valTotalRequests);
        valPending = findViewById(R.id.valPending);
        valDonors = findViewById(R.id.valDonors);

        btnCreateRequestCard = findViewById(R.id.btnCreateRequestCard);
        btnViewDonorsCard = findViewById(R.id.btnViewDonorsCard);
        btnSettingsCard = findViewById(R.id.btnSettingsCard);

        currentHospitalUid = FirebaseAuth.getInstance().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference();

        if (currentHospitalUid != null) {
            loadStats();
        }

        // 2. التنقل بين الصفحات
        btnCreateRequestCard.setOnClickListener(v -> {
            startActivity(new Intent(this, HospitalRequestsActivity.class));
        });

        btnViewDonorsCard.setOnClickListener(v -> {
            startActivity(new Intent(this, HospitalDonorsActivity.class));
        });

        btnSettingsCard.setOnClickListener(v -> {
            startActivity(new Intent(this, HospitalSettingsActivity.class));
        });
    }

    private void loadStats() {
        // جلب اسم المستشفى وموقعه
        dbRef.child("Hospitals").child(currentHospitalUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("hospitalName").getValue(String.class);
                    String city = snapshot.child("city").getValue(String.class);
                    tvHospitalName.setText(name);
                    tvHospitalLocation.setText(city + " 📍");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // دالة ضرورية لمنع أخطاء الـ Abstract Method
            }
        });

        // عد الطلبات (إجمالي + المفتوحة فقط)
        dbRef.child("Requests").orderByChild("hospitalId").equalTo(currentHospitalUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = (int) snapshot.getChildrenCount();
                int openCount = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    // فحص الحالة إذا كانت "مفتوح"
                    String status = ds.child("status").getValue(String.class);
                    if ("مفتوح".equals(status)) {
                        openCount++;
                    }
                }

                valTotalRequests.setText(String.valueOf(total));
                valPending.setText(String.valueOf(openCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // دالة ضرورية لمنع أخطاء الـ Abstract Method
            }
        });

        // عد المتبرعين
        dbRef.child("Donors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                valDonors.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // دالة ضرورية لمنع أخطاء الـ Abstract Method
            }
        });
    }
}