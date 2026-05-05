package com.example.vivalink;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HospitalNotificationActivity extends AppCompatActivity {

    private RecyclerView rv;
    private BloodBankNotificationAdapter adapter;
    private List<BloodBankNotificationModel> list = new ArrayList<>();
    private String currentHospitalId;
    private TextView tvNoNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_notification);

        rv = findViewById(R.id.rvNotifications);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);

        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BloodBankNotificationAdapter(list);
        rv.setAdapter(adapter);

        currentHospitalId = FirebaseAuth.getInstance().getUid();

        if (currentHospitalId != null) {
            loadHospitalNotifications();
        }
    }

    private void loadHospitalNotifications() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        BloodBankNotificationModel n = ds.getValue(BloodBankNotificationModel.class);

                        if (n == null) continue;

                        // ✅ فقط إشعارات موجهة للمستشفى
                        if ("ADMIN".equals(n.getTargetType())
                                && n.getTargetUserId() != null
                                && n.getTargetUserId().equals(currentHospitalId)) {

                            list.add(0, n);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                adapter.notifyDataSetChanged();
                tvNoNotifications.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}