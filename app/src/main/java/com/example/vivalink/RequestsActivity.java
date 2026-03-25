package com.example.vivalink;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {
    private RecyclerView rvRequests;
    private RequestsAdapter adapter;
    private List<BloodRequests> requestList;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        // ربط الواجهة
        rvRequests = findViewById(R.id.rvRequests);
        if (rvRequests == null) {
            Log.e("Error", "RecyclerView ID 'rvRequests' not found in layout!");
            return;
        }

        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        requestList = new ArrayList<>();

        // إعداد الأدابتر مع أكشن زر التبرع
        adapter = new RequestsAdapter(requestList, request -> {
            showDonateDialog(request);
        });
        rvRequests.setAdapter(adapter);

        // الربط مع Firebase (تأكدي أن الاسم Requests بحرف كبير)
        dbRef = FirebaseDatabase.getInstance().getReference("Requests");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        BloodRequests req = data.getValue(BloodRequests.class);
                        if (req != null) {
                            req.setRequestId(data.getKey());
                            requestList.add(req);
                        }
                    } catch (Exception e) {
                        Log.e("Firebase", "Error parsing data: " + e.getMessage());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestsActivity.this, "فشل جلب البيانات", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDonateDialog(BloodRequests request) {
        new AlertDialog.Builder(this)
                .setTitle("تأكيد التبرع")
                .setMessage("هل تود التبرع لمستشفى " + request.getHospitalName() + "؟")
                .setPositiveButton("تأكيد", (d, w) -> Toast.makeText(this, "شكراً لمساهمتك!", Toast.LENGTH_LONG).show())
                .setNegativeButton("إلغاء", null)
                .show();
    }
}