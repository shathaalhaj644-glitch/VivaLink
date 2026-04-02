package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class HospitalHomeActivity extends AppCompatActivity {

    private TextView tvHospitalName, tvHospitalLocation, valTotalRequests, valPending;
    // إضافة الزر الجديد هنا
    private CardView btnCreateRequestCard, btnViewDonorsCard, btnSettingsCard;
    private DatabaseReference dbRef;
    private String currentHospitalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_home);

        currentHospitalId = FirebaseAuth.getInstance().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference();

        // ربط العناصر (تأكد من مطابقة الـ ID مع الـ XML للزر الجديد)
        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvHospitalLocation = findViewById(R.id.tvHospitalLocation);
        valTotalRequests = findViewById(R.id.valTotalRequests);
        valPending = findViewById(R.id.valPending);

        btnCreateRequestCard = findViewById(R.id.btnCreateRequestCard);
        btnViewDonorsCard = findViewById(R.id.btnViewDonorsCard);
        btnSettingsCard = findViewById(R.id.btnSettingsCard); // ربط زر الإعدادات

        if (currentHospitalId != null) {
            loadHospitalData();
        }

        // الانتقال لصفحة الطلبات
        btnCreateRequestCard.setOnClickListener(v -> {
            startActivity(new Intent(HospitalHomeActivity.this, HospitalRequestsActivity.class));
        });

        // الانتقال لصفحة المتبرعين
        btnViewDonorsCard.setOnClickListener(v -> {
            startActivity(new Intent(HospitalHomeActivity.this, HospitalDonorsActivity.class));
        });

        // الانتقال لصفحة الإعدادات (الإضافة الجديدة)
        btnSettingsCard.setOnClickListener(v -> {
            startActivity(new Intent(HospitalHomeActivity.this, HospitalSettingsActivity.class));
        });
    }

    private void loadHospitalData() {
        if (currentHospitalId == null) return;

        dbRef.child("Hospitals").child(currentHospitalId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tvHospitalName.setText(snapshot.child("hospitalName").getValue(String.class));
                    String city = snapshot.child("city").getValue(String.class);
                    tvHospitalLocation.setText(city != null ? city + " 📍" : "نابلس 📍");
                    fetchStatistics();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchStatistics() {
        dbRef.child("Requests").orderByChild("hospitalId").equalTo(currentHospitalId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        valTotalRequests.setText(String.valueOf(snapshot.getChildrenCount()));
                        long pendingCount = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String st = ds.child("status").getValue(String.class);
                            if (st != null && "Pending".equalsIgnoreCase(st)) pendingCount++;
                        }
                        valPending.setText(String.valueOf(pendingCount));
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}