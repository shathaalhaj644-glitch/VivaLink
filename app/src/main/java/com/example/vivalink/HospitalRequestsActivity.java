package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class HospitalRequestsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    HospitalRequestsAdapter adapter;
    List<HospitalRequestModel> list;
    DatabaseReference db;


    FloatingActionButton btnAdd;
    String hospitalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_requests);


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            hospitalId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btn_add_new_request);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new HospitalRequestsAdapter(this, list);
        recyclerView.setAdapter(adapter);


        db = FirebaseDatabase.getInstance().getReference("Requests");

        if (hospitalId != null) {
            loadRequests();
        }


        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateRequestActivity.class));
        });
    }

    private void loadRequests() {

        db.orderByChild("hospitalId").equalTo(hospitalId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    HospitalRequestModel model = data.getValue(HospitalRequestModel.class);
                    if (model != null) {
                        model.requestId = data.getKey();
                        list.add(model);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HospitalRequestsActivity.this, "خطأ في تحميل البيانات", Toast.LENGTH_SHORT).show();
            }
        });
    }
}