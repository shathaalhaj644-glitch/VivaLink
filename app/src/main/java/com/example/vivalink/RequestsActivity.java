package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
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
    private List<RequestModel> requestList;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        mAuth = FirebaseAuth.getInstance();
        rvRequests = findViewById(R.id.rvRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        requestList = new ArrayList<>();

        adapter = new RequestsAdapter(requestList, (request, position) -> {
            // 1. الحفظ في جدول المتبرع لضمان بقاء الحالة "تم التبرع" ثابتة
            String uid = mAuth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference("Donors")
                    .child(uid).child("myDonations").child(request.getRequestId()).setValue(true);

            // 2. الانتقال للتفاصيل
            Intent intent = new Intent(this, RequestsDetailsActivity.class);
            intent.putExtra("requestId", request.getRequestId());
            intent.putExtra("hospitalName", request.getHospitalName());
            intent.putExtra("city", request.getCity());
            intent.putExtra("bloodType", request.getBloodType());
            intent.putExtra("department", request.getDepartment());
            intent.putExtra("units", request.getUnits());
            intent.putExtra("date", request.getDate());
            startActivity(intent);
        });

        rvRequests.setAdapter(adapter);
        loadUserFilterAndData();
    }

    private void loadUserFilterAndData() {
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference donorRef = FirebaseDatabase.getInstance().getReference("Donors").child(uid);

        donorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot donorSnapshot) {
                if (donorSnapshot.exists()) {
                    String city = donorSnapshot.child("city").getValue(String.class);
                    String blood = donorSnapshot.child("bloodType").getValue(String.class);
                    // جلب البيانات المفلترة مع فحص حالة التبرع السابقة
                    fetchRequests(city, blood, donorSnapshot);
                }
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    private void fetchRequests(String city, String blood, DataSnapshot donorSnapshot) {
        String combinedKey = city + "_" + blood;
        FirebaseDatabase.getInstance().getReference("Requests")
                .orderByChild("city_bloodType").equalTo(combinedKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        requestList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            RequestModel req = data.getValue(RequestModel.class);
                            if (req != null) {
                                req.setRequestId(data.getKey());
                                // فحص إذا المتبرع تبرع لهذا الطلب سابقاً لتثبيت الحالة
                                req.setDonated(donorSnapshot.child("myDonations").hasChild(data.getKey()));
                                requestList.add(req);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(DatabaseError error) {}
                });
    }
}