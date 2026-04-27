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
import java.util.*;

public class DonorNotificationActivity extends AppCompatActivity {

    private RecyclerView rv;
    private BloodBankNotificationAdapter adapter;
    private List<BloodBankNotificationModel> list = new ArrayList<>();
    private DatabaseReference dbRef;
    private String myId, myBloodType, myCity;
    private TextView tvNoNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_notification);

        rv = findViewById(R.id.rvNotifications);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BloodBankNotificationAdapter(list);
        rv.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference();
        myId = FirebaseAuth.getInstance().getUid();

        if (myId != null) { fetchDonorDetailsAndLoadNotifications(); }
    }

    private void fetchDonorDetailsAndLoadNotifications() {
        dbRef.child("Donors").child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    myBloodType = snapshot.child("bloodType").getValue(String.class);
                    myCity = snapshot.child("city").getValue(String.class);
                    loadFilteredNotifications();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadFilteredNotifications() {
        dbRef.child("Notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        BloodBankNotificationModel n = ds.getValue(BloodBankNotificationModel.class);
                        if (n != null) {
                            if (myId.equals(n.getTargetUserId())) {
                                list.add(0, n);
                            } else if ("DONOR".equals(n.getTargetType()) && n.getMessage() != null) {
                                if (myCity != null && myBloodType != null && n.getMessage().contains(myCity) && n.getMessage().contains(myBloodType)) {
                                    list.add(0, n);
                                }
                            }
                        }
                    } catch (Exception e) {}
                }
                adapter.notifyDataSetChanged();
                tvNoNotifications.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}