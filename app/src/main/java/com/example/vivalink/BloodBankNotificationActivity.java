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

    private TextView tabSend, tabReceive, tvLocationInfo;
    private View layoutSend;
    private RecyclerView rvIncoming;
    private EditText etMessage;
    private Button btnSend;

    private CheckBox cbAp, cbAn, cbBp, cbBn, cbOp, cbOn, cbABp, cbABn;

    private DatabaseReference dbRef;
    private BloodBankNotificationAdapter adapter;
    private List<BloodBankNotificationModel> incomingList = new ArrayList<>();

    private String currentCity = "";
    private String staffCity = "";
    private String hospitalName = "";

    // 🔥 الجديد
    private String hospitalId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbank_notification);

        dbRef = FirebaseDatabase.getInstance().getReference("Notifications");

        initViews();
        fetchCurrentHospitalOrStaffData();

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
        });

        btnSend.setOnClickListener(v -> sendNotificationToDonors());
    }

    private void initViews() {
        tabSend = findViewById(R.id.tabSend);
        tabReceive = findViewById(R.id.tabReceive);
        layoutSend = findViewById(R.id.layoutSend);
        rvIncoming = findViewById(R.id.rvIncomingAdmin);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSendNotification);
        tvLocationInfo = findViewById(R.id.tvLocationInfo);

        cbAp = findViewById(R.id.cbAp);   cbAn = findViewById(R.id.cbAn);
        cbBp = findViewById(R.id.cbBp);   cbBn = findViewById(R.id.cbBn);
        cbOp = findViewById(R.id.cbOp);   cbOn = findViewById(R.id.cbOn);
        cbABp = findViewById(R.id.cbABp); cbABn = findViewById(R.id.cbABn);

        rvIncoming.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BloodBankNotificationAdapter(incomingList);
        rvIncoming.setAdapter(adapter);
    }

    private void fetchCurrentHospitalOrStaffData() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataSnapshot staff = snapshot.child("BloodBankStaff").child(uid);
                        DataSnapshot hosp = snapshot.child("Hospitals").child(uid);

                        if (staff.exists()) {
                            currentCity = staff.child("city").getValue(String.class);
                            hospitalName = staff.child("hospitalName").getValue(String.class);
                            hospitalId = staff.child("hospitalId").getValue(String.class);
                        } else if (hosp.exists()) {
                            currentCity = hosp.child("city").getValue(String.class);
                            hospitalName = hosp.child("hospitalName").getValue(String.class);
                            hospitalId = uid;
                        }

                        tvLocationInfo.setText("المدينة: " + currentCity + " | " + hospitalName);

                        // 🔥 التعديل الجوهري: استدعاء الدالة هنا لضمان وجود hospitalId
                        loadIncomingNotifications();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void sendNotificationToDonors() {
        String msg = etMessage.getText().toString().trim();

        if (msg.isEmpty() || currentCity == null || currentCity.isEmpty()) {
            Toast.makeText(this, "يرجى كتابة الرسالة وانتظار تحميل الموقع", Toast.LENGTH_SHORT).show();
            return;
        }

        if (hospitalId == null || hospitalId.isEmpty()) {
            Toast.makeText(this, "خطأ: لم يتم تحديد المستشفى", Toast.LENGTH_SHORT).show();
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

        // 🔥 الحل: إرسال كل فصيلة في طلب منفصل لضمان عدم اختلاط البيانات
        for (String blood : selectedBloods) {

            // 1. إشعار للمستشفى (حصراً لمستشفى الموظف الحالي)
            pushToFirebase(hospitalId, "ADMIN", blood, msg);

            // 2. إشعار للمتبرعين (يصل لكل من يطابق الفصيلة والمدينة)
            pushToFirebase(null, "DONOR", blood, msg);
        }

        Toast.makeText(this, "تم إرسال " + selectedBloods.size() + " طلبات بنجاح", Toast.LENGTH_LONG).show();
        etMessage.setText("");
    }

    private void pushToFirebase(String targetUserId, String targetType, String blood, String message) {
        DatabaseReference newNotif = dbRef.push(); // إنشاء ID فريد لكل إشعار

        HashMap<String, Object> data = new HashMap<>();
        data.put("notificationId", newNotif.getKey());
        data.put("title", "🚨 طلب دم عاجل: " + blood); // الفصيلة في العنوان
        data.put("message", "مطلوب فصيلة (" + blood + ") في " + hospitalName + "\n" + message);

        data.put("targetType", targetType);
        data.put("targetUserId", targetUserId); // سيأخذ ID المستشفى في حالة ADMIN و null في حالة DONOR

        data.put("bloodType", blood); // تأكد أن هذه القيمة هي blood الحالية في الـ Loop
        data.put("city", currentCity);
        data.put("hospitalName", hospitalName);
        data.put("hospitalId", hospitalId);

        data.put("createdAt", System.currentTimeMillis());
        data.put("isRead", false);
        data.put("type", "urgent_request");

        newNotif.setValue(data);
    }
    // ابحثي عن هذه الدالة في BloodBankNotificationActivity
    private void loadIncomingNotifications() {
        String myId = FirebaseAuth.getInstance().getUid();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                incomingList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        BloodBankNotificationModel n = ds.getValue(BloodBankNotificationModel.class);
                        if (n == null) continue;

                        // 1. فحص إشعارات رفع الفحوصات الجديدة (حسب المدينة)
                        if ("new_test_upload".equals(n.getType())) {
                            // هنا نستخدم currentCity التي تم جلبها عند فتح الصفحة
                            if (currentCity != null && !currentCity.isEmpty() && currentCity.equals(n.getCity())) {
                                incomingList.add(0, n);
                            }
                        }

                        // 2. فحص الإشعارات الموجهة للموظف/المستشفى شخصياً (كطلبات الدم)
                        else if ("ADMIN".equals(n.getTargetType())) {
                            if (myId != null && myId.equals(n.getTargetUserId())) {
                                incomingList.add(0, n);
                            }
                        }
                        else if ("donor_arrival".equals(n.getType())) {
                            // الفلترة تتم بناءً على معرف المستشفى (hospitalId) الخاص بالموظف
                            if (hospitalId != null && hospitalId.equals(n.getTargetUserId())) {
                                incomingList.add(0, n);
                            }
                        }

                    } catch (Exception e) {
                        Log.e("VivaLink", "Read Error: " + e.getMessage());
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}