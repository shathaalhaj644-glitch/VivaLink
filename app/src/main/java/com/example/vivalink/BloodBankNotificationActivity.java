package com.example.vivalink;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;

public class BloodBankNotificationActivity extends AppCompatActivity {

    // تعريف العناصر
    TextView tabSend, tabReceive, tvLocationInfo;
    View layoutSend;
    RecyclerView rvIncoming;
    EditText etMessage;
    Button btnSend;
    CheckBox cbAp, cbAn, cbBp, cbBn, cbOp, cbOn, cbABp, cbABn;

    // الفايربيس والأدابتر
    DatabaseReference dbRef;
    BloodBankNotificationAdapter adapter;
    List<BloodBankNotificationModel> incomingList = new ArrayList<>();

    // متغيرات حفظ الموقع
    String currentCity = "";
    String hospitalName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbank_notification);

        // المرجع الرئيسي للإشعارات
        dbRef = FirebaseDatabase.getInstance().getReference("Notifications");

        initViews();
        fetchCurrentHospitalOrStaffData();

        // تنقل بين التبويبات
        tabSend.setOnClickListener(v -> {
            layoutSend.setVisibility(View.VISIBLE);
            rvIncoming.setVisibility(View.GONE);
            tabSend.setTextColor(Color.parseColor("#D32F2F"));
            tabReceive.setTextColor(Color.parseColor("#757575"));
        });

        tabReceive.setOnClickListener(v -> {
            layoutSend.setVisibility(View.GONE);
            rvIncoming.setVisibility(View.VISIBLE);
            tabSend.setTextColor(Color.parseColor("#757575"));
            tabReceive.setTextColor(Color.parseColor("#D32F2F"));
            loadIncomingNotifications();
        });

        btnSend.setOnClickListener(v -> sendNotificationToDonors());
    }

    private void fetchCurrentHospitalOrStaffData() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // المحاولة الأولى: البحث في موظفي بنك الدم
        database.getReference("BloodBankStaff").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentCity = snapshot.child("city").getValue(String.class);
                    hospitalName = snapshot.child("hospitalName").getValue(String.class);
                    applyDataToUI();
                } else {
                    // المحاولة الثانية: البحث في المستشفيات
                    database.getReference("Hospitals").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                currentCity = snapshot.child("city").getValue(String.class);
                                hospitalName = snapshot.child("hospitalName").getValue(String.class);
                                applyDataToUI();
                            } else {
                                tvLocationInfo.setText("خطأ: لم يتم العثور على بيانات الحساب");
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void applyDataToUI() {
        if (currentCity != null && hospitalName != null) {
            tvLocationInfo.setText("المدينة: " + currentCity + " | " + hospitalName);
        }
    }

    private void initViews() {
        tabSend = findViewById(R.id.tabSend);
        tabReceive = findViewById(R.id.tabReceive);
        layoutSend = findViewById(R.id.layoutSend);
        rvIncoming = findViewById(R.id.rvIncomingAdmin);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSendNotification);
        tvLocationInfo = findViewById(R.id.tvLocationInfo);

        cbAp = findViewById(R.id.cbAp); cbAn = findViewById(R.id.cbAn);
        cbBp = findViewById(R.id.cbBp); cbBn = findViewById(R.id.cbBn);
        cbOp = findViewById(R.id.cbOp); cbOn = findViewById(R.id.cbOn);
        cbABp = findViewById(R.id.cbABp); cbABn = findViewById(R.id.cbABn);

        rvIncoming.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BloodBankNotificationAdapter(incomingList);
        rvIncoming.setAdapter(adapter);
    }

    private void sendNotificationToDonors() {
        String msg = etMessage.getText().toString().trim();
        if (msg.isEmpty() || currentCity.isEmpty()) {
            Toast.makeText(this, "يرجى كتابة الرسالة وانتظار تحميل الموقع", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> selectedBloods = new ArrayList<>();
        if (cbAp.isChecked()) selectedBloods.add("A+");
        if (cbAn.isChecked()) selectedBloods.add("A-");
        if (cbBp.isChecked()) selectedBloods.add("B+");
        if (cbBn.isChecked()) selectedBloods.add("B-");
        if (cbOp.isChecked()) selectedBloods.add("O+");
        if (cbOn.isChecked()) selectedBloods.add("O-");
        if (cbABp.isChecked()) selectedBloods.add("AB+");
        if (cbABn.isChecked()) selectedBloods.add("AB-");

        if (selectedBloods.isEmpty()) {
            Toast.makeText(this, "حدد فصيلة واحدة على الأقل", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference donorsRef = FirebaseDatabase.getInstance().getReference("Donors");
        donorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot donorSnap : snapshot.getChildren()) {
                    String dCity = donorSnap.child("city").getValue(String.class);
                    String dBlood = donorSnap.child("bloodType").getValue(String.class);

                    if (currentCity.equalsIgnoreCase(dCity) && selectedBloods.contains(dBlood)) {
                        DatabaseReference newNotif = dbRef.push();
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("notificationId", newNotif.getKey());
                        data.put("title", "🚨 طلب دم: " + hospitalName);
                        data.put("message", "مطلوب " + dBlood + " في " + currentCity + "\n" + msg);
                        data.put("targetType", "DONOR");
                        data.put("targetUserId", donorSnap.getKey());
                        data.put("createdAt", System.currentTimeMillis());
                        data.put("isRead", false);

                        newNotif.setValue(data);
                        count++;
                    }
                }
                Toast.makeText(BloodBankNotificationActivity.this, "تم الإرسال لـ " + count + " متبرع", Toast.LENGTH_SHORT).show();
                etMessage.setText("");
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadIncomingNotifications() {
        dbRef.orderByChild("targetType").equalTo("ADMIN")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        incomingList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                BloodBankNotificationModel m = ds.getValue(BloodBankNotificationModel.class);
                                if (m != null) incomingList.add(0, m);
                            } catch (Exception e) {
                                Log.e("FirebaseError", "Error parsing: " + ds.getKey());
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}