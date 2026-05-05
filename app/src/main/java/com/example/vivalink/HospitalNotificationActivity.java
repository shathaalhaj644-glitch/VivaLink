package com.example.vivalink;

import android.os.Bundle;
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
    private TextView tvNo;

    private String myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_notification);

        rv = findViewById(R.id.rvNotifications);
        tvNo = findViewById(R.id.tvNoNotifications);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BloodBankNotificationAdapter(list);
        rv.setAdapter(adapter);

        myId = FirebaseAuth.getInstance().getUid();

        loadData();
    }

    private void loadData() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    BloodBankNotificationModel n = ds.getValue(BloodBankNotificationModel.class);

                    if (n != null &&
                            "ADMIN".equals(n.getTargetType()) &&
                            myId.equals(n.getTargetUserId())) {

                        list.add(0, n);
                    }
                }

                adapter.notifyDataSetChanged();
                tvNo.setVisibility(list.isEmpty() ? TextView.VISIBLE : TextView.GONE);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}