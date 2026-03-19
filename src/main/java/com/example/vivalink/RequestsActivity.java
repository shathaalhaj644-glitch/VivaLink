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

public class RequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestsAdapter adapter;
    private List<RequestModel> requestList;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        // تعريف الـ RecyclerView
        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestList = new ArrayList<>();
        adapter = new RequestsAdapter(requestList, this);
        recyclerView.setAdapter(adapter);

        // الربط مع عقدة طلبات الدم في الفايربيز
        dbRef = FirebaseDatabase.getInstance().getReference("BloodRequests");

        loadAllRequests();
    }

    private void loadAllRequests() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        // تحويل بيانات الفايربيز إلى Model
                        RequestModel model = data.getValue(RequestModel.class);
                        if (model != null) {
                            requestList.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(RequestsActivity.this, "لا يوجد طلبات حالياً", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestsActivity.this, "فشل في جلب البيانات", Toast.LENGTH_SHORT).show();
            }
        });
    }
} // القوس الأخير لإغلاق الكلاس (تأكدي من وجوده)