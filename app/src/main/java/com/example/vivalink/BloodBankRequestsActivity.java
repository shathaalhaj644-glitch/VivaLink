package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class BloodBankRequestsActivity extends AppCompatActivity {

    private RecyclerView rvRequests;
    private BloodBankRequestsAdapter adapter;
    private List<BloodBankRequestsModel> allRequests = new ArrayList<>();
    private List<BloodBankRequestsModel> filteredList = new ArrayList<>();
    private DatabaseReference dbRef;
    private String currentHospitalId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbank_requests);

        dbRef = FirebaseDatabase.getInstance().getReference();
        rvRequests = findViewById(R.id.rvRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BloodBankRequestsAdapter(filteredList, request -> {
            dbRef.child("Requests").child(request.getRequestId()).child("status").setValue("مغلق");
        });
        rvRequests.setAdapter(adapter);

        loadStaffAndRequests();

        findViewById(R.id.btnNewRequest).setOnClickListener(v ->
                startActivity(new Intent(this, BloodBankCreateRequestActivity.class)));

        findViewById(R.id.btnFilterAll).setOnClickListener(v -> filter("الكل"));
        findViewById(R.id.btnFilterOpen).setOnClickListener(v -> filter("مفتوح"));
        findViewById(R.id.btnFilterClosed).setOnClickListener(v -> filter("مغلق"));
    }

    private void loadStaffAndRequests() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على مستخدم مسجل", Toast.LENGTH_SHORT).show();
            return;
        }


        dbRef.child("BloodBankStaff").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentHospitalId = snapshot.child("hospitalId").getValue(String.class);

                    if (currentHospitalId != null && !currentHospitalId.isEmpty()) {
                        fetchSpecificHospitalRequests();
                    } else {

                        Toast.makeText(BloodBankRequestsActivity.this, "حسابك لا يحتوي على ID مستشفى", Toast.LENGTH_LONG).show();
                    }
                } else {

                    Toast.makeText(BloodBankRequestsActivity.this, "فشل الوصول لبيانات الموظف", Toast.LENGTH_LONG).show();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchSpecificHospitalRequests() {

        dbRef.child("Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allRequests.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    BloodBankRequestsModel req = ds.getValue(BloodBankRequestsModel.class);

                    if (req != null && currentHospitalId.equals(req.getHospitalId())) {
                        allRequests.add(req);
                    }
                }
                filter("الكل");

                if (allRequests.isEmpty()) {
                    Toast.makeText(BloodBankRequestsActivity.this, "لا يوجد طلبات لهذا المستشفى حالياً", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void filter(String status) {
        filteredList.clear();
        for (BloodBankRequestsModel r : allRequests) {
            if (status.equals("الكل") || r.getStatus().equals(status)) {
                filteredList.add(r);
            }
        }
        adapter.notifyDataSetChanged();
    }
}