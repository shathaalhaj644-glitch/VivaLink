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
    private List<BloodRequests> requestList;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        mAuth = FirebaseAuth.getInstance();
        rvRequests = findViewById(R.id.rvRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        requestList = new ArrayList<>();

        adapter = new RequestsAdapter(requestList, this::showDonateDialog);
        rvRequests.setAdapter(adapter);

        if (mAuth.getCurrentUser() != null) {
            loadUserCityAndRequests();
        }
    }

    private void loadUserCityAndRequests() {
        String uid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Donors").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String userCity = snapshot.child("city").getValue(String.class);
                            if (userCity != null) fetchRequestsByCity(userCity);
                        }
                    }
                    @Override public void onCancelled(DatabaseError error) {}
                });
    }

    private void fetchRequestsByCity(String city) {
        Query cityQuery = FirebaseDatabase.getInstance().getReference("Requests")
                .orderByChild("city").equalTo(city);

        cityQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                requestList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    BloodRequests req = data.getValue(BloodRequests.class);
                    if (req != null) {
                        req.setRequestId(data.getKey());
                        requestList.add(req);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(DatabaseError error) {}
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
}