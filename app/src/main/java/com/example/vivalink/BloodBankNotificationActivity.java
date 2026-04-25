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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BloodBankNotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private ProgressBar progressBar;
    private TextView tvNoNotifications;
    private ImageView btnBack;
    private TabLayout tabLayout;

    private DatabaseReference dbRef;
    private List<BloodBankNotificationModel> allNotifications = new ArrayList<>();
    private List<BloodBankNotificationModel> filteredList = new ArrayList<>();
    private BloodBankNotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbank_notification); // تأكدي من تحديث الـ XML بالأسفل

        // 1. تعريف العناصر
        rvNotifications = findViewById(R.id.rvNotifications);
        progressBar = findViewById(R.id.progressBar);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);
        btnBack = findViewById(R.id.btnBack);
        tabLayout = findViewById(R.id.tabLayout);

        // 2. إعداد الـ RecyclerView
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BloodBankNotificationAdapter(filteredList);
        rvNotifications.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        // 3. مستمع التبديل بين التابين
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterNotifications(tab.getPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // 4. جلب البيانات
        dbRef = FirebaseDatabase.getInstance().getReference("Notifications");
        fetchData();
    }

    private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allNotifications.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    BloodBankNotificationModel model = ds.getValue(BloodBankNotificationModel.class);
                    if (model != null) allNotifications.add(model);
                }
                Collections.reverse(allNotifications);
                filterNotifications(tabLayout.getSelectedTabPosition());
                progressBar.setVisibility(View.GONE);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { progressBar.setVisibility(View.GONE); }
        });
    }

    private void filterNotifications(int position) {
        filteredList.clear();
        for (BloodBankNotificationModel n : allNotifications) {
            if (position == 0) { // التاب الأول: مثلاً إشعارات المتبرعين
                if ("DONOR".equals(n.targetType)) filteredList.add(n);
            } else { // التاب الثاني: إشعارات الموظف والمستشفى
                if ("ADMIN".equals(n.targetType) || "HOSPITAL".equals(n.targetType)) filteredList.add(n);
            }
        }

        if (filteredList.isEmpty()) tvNoNotifications.setVisibility(View.VISIBLE);
        else tvNoNotifications.setVisibility(View.GONE);

        adapter.notifyDataSetChanged();
    }
}