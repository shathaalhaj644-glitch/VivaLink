package com.example.vivalink;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DonorNotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvNoNotifications;
    private ImageView btnBack;

    private DatabaseReference dbRef;
    private String userId, myCity, myBloodType;
    private List<BloodBankNotificationModel> notificationList;
    // تأكدي من وجود NotificationAdapter في مشروعك، سأرفقه لكِ بالأسفل
    private BloodBankNotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_notification);

        // 1. تعريف العناصر
        recyclerView = findViewById(R.id.rvNotifications);
        progressBar = findViewById(R.id.progressBar);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);
        btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationList = new ArrayList<>();
        adapter = new BloodBankNotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> onBackPressed());

        // 2. جلب بيانات المتبرع أولاً (المدينة والفصيلة) لفلترة الإشعارات
        userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            dbRef = FirebaseDatabase.getInstance().getReference();
            loadMyDetailsAndNotifications();
        }
    }

    private void loadMyDetailsAndNotifications() {
        progressBar.setVisibility(View.VISIBLE);

        // نجلب مدينة وفصيلة المتبرع الحالي
        dbRef.child("Donors").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    myCity = snapshot.child("city").getValue(String.class);
                    myBloodType = snapshot.child("bloodType").getValue(String.class);

                    // بعد ما عرفنا المدينة والفصيلة، هسا بنجيب الإشعارات
                    fetchNotifications();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void fetchNotifications() {
        dbRef.child("Notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    BloodBankNotificationModel n = ds.getValue(BloodBankNotificationModel.class);

                    if (n != null && "DONOR".equals(n.targetType)) {
                        // فلترة: إما إشعار عام للمدينة والفصيلة، أو إشعار خاص لهذا المتبرع بالـ ID
                        if ((myCity.equals(n.targetCity) && myBloodType.equals(n.targetBlood))
                                || userId.equals(n.donorId)) {
                            notificationList.add(n);
                        }
                    }
                }

                // ترتيب الإشعارات بحيث الأحدث يظهر أولاً
                Collections.reverse(notificationList);

                progressBar.setVisibility(View.GONE);
                if (notificationList.isEmpty()) {
                    tvNoNotifications.setVisibility(View.VISIBLE);
                } else {
                    tvNoNotifications.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}