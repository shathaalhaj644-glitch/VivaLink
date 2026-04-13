package com.example.vivalink;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class HospitalDonorsActivity extends AppCompatActivity {

    private RecyclerView rvDonors;
    private HospitalDonorsAdapter adapter;


    private List<RequestModel> list;
    private String currentCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_donors);

        rvDonors = findViewById(R.id.rvDonors);
        rvDonors.setLayoutManager(new LinearLayoutManager(this));


        list = new ArrayList<>();


        adapter = new HospitalDonorsAdapter(this, list, new HospitalDonorsAdapter.OnDonorClickListener() {
            @Override
            public void onDonorClick(RequestModel request) {

            }
        });

        rvDonors.setAdapter(adapter);
        getHospitalCity();
    }

    private void getHospitalCity() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance().getReference("Hospitals").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            currentCity = snapshot.child("city").getValue(String.class);
                            loadRequests();
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadRequests() {
        FirebaseDatabase.getInstance().getReference("Requests")
                .orderByChild("city").equalTo(currentCity)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            // الحل هنا: قراءة البيانات كـ RequestModel حصراً
                            RequestModel req = ds.getValue(RequestModel.class);
                            if (req != null) {
                                req.setRequestId(ds.getKey());
                                list.add(req);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}