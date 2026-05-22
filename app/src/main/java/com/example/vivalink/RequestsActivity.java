package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
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
    private TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        mAuth = FirebaseAuth.getInstance();
        rvRequests = findViewById(R.id.rvRequests);
        btnBack = findViewById(R.id.btnBack);

        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        requestList = new ArrayList<>();

        btnBack.setOnClickListener(v -> finish());

        adapter = new RequestsAdapter(requestList, (request, position) -> {
            Intent intent = new Intent(this, RequestsDetailsActivity.class);
            intent.putExtra("requestId", request.getRequestId());
            intent.putExtra("hospitalName", request.getHospitalName());
            intent.putExtra("city", request.getCity());
            intent.putExtra("bloodType", request.getBloodType());
            intent.putExtra("department", request.getDepartment());
            intent.putExtra("units", request.getUnits());
            intent.putExtra("confirmedAt", request.getFormattedDate());
            intent.putExtra("isDonated", request.isDonated());
            startActivity(intent);
        });

        rvRequests.setAdapter(adapter);
        loadUserFilterAndData();
    }

    private void loadUserFilterAndData() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference donorRef = FirebaseDatabase.getInstance().getReference("Donors").child(uid);

        donorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot donorSnapshot) {
                if (donorSnapshot.exists()) {
                    String city = donorSnapshot.child("city").getValue(String.class);
                    String blood = donorSnapshot.child("bloodType").getValue(String.class);
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
                                String status = data.child("status").getValue(String.class);


                                if ("ملغي".equals(status)) {
                                    continue;
                                }

                                req.setRequestId(data.getKey());


                                boolean isClosed = "مغلق".equals(status);
                                boolean isUserDonated = donorSnapshot.child("myDonations").hasChild(data.getKey());


                                req.setDonated(isClosed || isUserDonated);

                                requestList.add(req);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(DatabaseError error) {}
                });
    }
}