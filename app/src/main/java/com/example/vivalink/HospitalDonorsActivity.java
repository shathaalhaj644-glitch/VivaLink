package com.example.vivalink;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class HospitalDonorsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    HospitalDonorsAdapter adapter;
    List<HospitalDonorsModel> donorList;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_donors);

        recyclerView = findViewById(R.id.rvDonors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        donorList = new ArrayList<>();
        adapter = new HospitalDonorsAdapter(this, donorList);
        recyclerView.setAdapter(adapter);

        // المرجع هو "Donors" كابيتال
        dbRef = FirebaseDatabase.getInstance().getReference("Donors");

        loadDonors();
    }

    private void loadDonors() {
        // الفلترة على كلمة "Donor" (أول حرف كابيتال وبدون s)
        dbRef.orderByChild("role").equalTo("Donor")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        donorList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                HospitalDonorsModel donor = ds.getValue(HospitalDonorsModel.class);
                                if (donor != null) {
                                    donorList.add(donor);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(HospitalDonorsActivity.this, "لا يوجد متبرعون حالياً", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HospitalDonorsActivity.this, "خطأ: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}