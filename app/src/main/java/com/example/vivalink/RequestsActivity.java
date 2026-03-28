package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; // ضروري لتحديث الوقت
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {
    private RecyclerView rvRequests;
    private RequestsAdapter adapter;
    private List<BloodRequests> requestList;
    private FirebaseAuth mAuth;

    // تعريف الـ Handler لتحديث الوقت كل دقيقة تلقائياً
    private Handler timeHandler = new Handler();
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if (adapter != null) {
                adapter.notifyDataSetChanged(); // إعادة حساب "منذ متى" لكل القائمة
            }
            timeHandler.postDelayed(this, 60000); // تكرار كل 60 ثانية
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        mAuth = FirebaseAuth.getInstance();
        rvRequests = findViewById(R.id.rvRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        requestList = new ArrayList<>();

        // تأكدي أن الـ Adapter يستقبل القائمة والـ Listener بشكل صحيح
        adapter = new RequestsAdapter(requestList, this::showDonateDialog);
        rvRequests.setAdapter(adapter);

        if (mAuth.getCurrentUser() != null) {
            loadUserAndRequests();
        }
    }

    private void loadUserAndRequests() {
        String uid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Donors").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String userCity = snapshot.child("city").getValue(String.class);
                            String userBlood = snapshot.child("bloodType").getValue(String.class);

                            // الفلترة الذكية: البحث عن (المدينة_الزمرة) معاً
                            if (userCity != null && userBlood != null) {
                                fetchFilteredRequests(userCity, userBlood);
                            }
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void fetchFilteredRequests(String city, String blood) {
        // نستخدم الحقل المركب الجديد city_bloodType لضمان عدم ظهور زمر مختلفة
        String compositeKey = city + "_" + blood;

        Query query = FirebaseDatabase.getInstance().getReference("Requests")
                .orderByChild("city_bloodType").equalTo(compositeKey);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        BloodRequests req = data.getValue(BloodRequests.class);
                        if (req != null) {
                            req.setRequestId(data.getKey());
                            requestList.add(req);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showDonateDialog(BloodRequests request) {
        Intent intent = new Intent(this, DonateActivity.class);
        intent.putExtra("bloodType", request.getBloodType());
        intent.putExtra("hospitalName", request.getHospitalName());
        intent.putExtra("city", request.getCity());
        intent.putExtra("department", request.getDepartment());
        intent.putExtra("units", request.getUnits());
        startActivity(intent);
    }

    // إدارة دورة حياة المؤقت (Handler) لضمان عدم استهلاك البطارية
    @Override
    protected void onResume() {
        super.onResume();
        timeHandler.post(timeRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timeHandler.removeCallbacks(timeRunnable);
    }
}