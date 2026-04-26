package com.example.vivalink;

import android.os.Bundle;
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
    // التعديل هنا: القائمة يجب أن تكون من نوع Notifications
    private List<Notifications> list = new ArrayList<>();
    private DatabaseReference dbRef;
    private String myId;
    private String myBloodType, myCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_notification);

        rv = findViewById(R.id.rvNotifications);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // الآن القائمة متوافقة مع الأدابتر المعدل
        adapter = new BloodBankNotificationAdapter(list);
        rv.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference();
        myId = FirebaseAuth.getInstance().getUid();

        if (myId != null) {
            fetchDonorDetailsAndLoadNotifications();
        }
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
                    // التعديل هنا: جلب البيانات كـ Notifications
                    Notifications n = ds.getValue(Notifications.class);

                    if (n != null) {
                        // 1. إشعارات موجهة لهذا المتبرع شخصياً
                        if (myId.equals(n.getUserId())) {
                            list.add(0, n);
                        }
                        // 2. إشعارات عامة (طلبات تبرع) تعتمد على الفصيلة والمدينة
                        else if (n.getMessage() != null && myCity != null && myBloodType != null) {
                            if (n.getMessage().contains(myCity) && n.getMessage().contains(myBloodType)) {
                                list.add(0, n);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}