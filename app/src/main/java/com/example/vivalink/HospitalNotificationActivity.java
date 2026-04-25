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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HospitalNotificationActivity extends AppCompatActivity {

    private RecyclerView rv;
    private BloodBankNotificationAdapter adapter;
    private List<BloodBankNotificationModel> list;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ImageView btnBack;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_notification);

        // 1. تعريف العناصر
        rv = findViewById(R.id.rvHospitalNotifications);
        progressBar = findViewById(R.id.progressBarHospital);
        tvEmpty = findViewById(R.id.tvNoHospitalNotifications);
        btnBack = findViewById(R.id.btnBackHospital);

        // 2. إعداد الـ RecyclerView
        rv.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new BloodBankNotificationAdapter(list);
        rv.setAdapter(adapter);

        // 3. زر الرجوع
        btnBack.setOnClickListener(v -> finish());

        // 4. جلب الإشعارات الموجهة للمستشفيات (HOSPITAL) من بنك الدم
        dbRef = FirebaseDatabase.getInstance().getReference("Notifications");
        loadHospitalNotifications();
    }

    private void loadHospitalNotifications() {
        progressBar.setVisibility(View.VISIBLE);

        // الفلترة هنا تعني أن المستشفى سيرى فقط ما يرسله له الموظف تحت وسم HOSPITAL
        dbRef.orderByChild("targetType").equalTo("HOSPITAL")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            BloodBankNotificationModel model = ds.getValue(BloodBankNotificationModel.class);
                            if (model != null) {
                                // إضافة في البداية (index 0) ليظهر الأحدث أولاً
                                list.add(0, model);
                            }
                        }

                        progressBar.setVisibility(View.GONE);

                        if (list.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
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