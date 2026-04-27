package com.example.vivalink;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.*;

public class BloodBankNotificationActivity extends AppCompatActivity {

    TextView tabSend, tabReceive;
    View layoutSend;
    RecyclerView rvIncoming;
    EditText etMessage;
    Button btnSend;
    DatabaseReference dbRef;
    BloodBankNotificationAdapter adapter;

    // الحل الجذري: توحيد نوع القائمة لتطابق الموديل والأدابتر تماماً
    List<BloodBankNotificationModel> incomingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbank_notification);

        dbRef = FirebaseDatabase.getInstance().getReference("Notifications");
        initViews();

        // منطق تبديل التبويبات (Tabs)
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
            loadIncomingNotifications(); // تحميل الإشعارات الواردة للموظف
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

        rvIncoming.setLayoutManager(new LinearLayoutManager(this));

        // ربط الأدابتر بالقائمة الموحدة BloodBankNotificationModel
        adapter = new BloodBankNotificationAdapter(incomingList);
        rvIncoming.setAdapter(adapter);
    }

    private void sendNotificationToDonors() {
        String msg = etMessage.getText().toString().trim();
        if (msg.isEmpty()) {
            Toast.makeText(this, "يرجى كتابة نص الرسالة", Toast.LENGTH_SHORT).show();
            return;
        }

        // ملاحظة: هنا يمكنك وضع منطق اختيار المدينة والفصيلة
        DatabaseReference donorsRef = FirebaseDatabase.getInstance().getReference("Donors");

        donorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot donorSnap : snapshot.getChildren()) {
                    String donorId = donorSnap.getKey();

                    // إنشاء إشعار جديد يتبع الموديل المعتمد
                    DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("Notifications").push();
                    String id = notifRef.getKey();

                    HashMap<String, Object> note = new HashMap<>();
                    note.put("notificationId", id);
                    note.put("title", "🚨 طلب دم عاجل");
                    note.put("message", msg);
                    note.put("type", "urgent");
                    note.put("targetType", "DONOR"); // موجه للمتبرعين
                    note.put("targetUserId", donorId); // المعرف الخاص بالمتبرع
                    note.put("createdAt", System.currentTimeMillis());
                    note.put("isRead", false);

                    if (id != null) notifRef.setValue(note);
                    count++;
                }
                Toast.makeText(BloodBankNotificationActivity.this, "تم الإرسال لـ " + count + " متبرع", Toast.LENGTH_SHORT).show();
                etMessage.setText("");
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadIncomingNotifications() {
        // فلترة الإشعارات الموجهة للموظف (ADMIN)
        dbRef.orderByChild("targetType").equalTo("ADMIN")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        incomingList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                // التحويل للموديل الموحد لمنع الكراش
                                BloodBankNotificationModel m = ds.getValue(BloodBankNotificationModel.class);
                                if (m != null) incomingList.add(0, m);
                            } catch (Exception e) {
                                // تجاهل البيانات التالفة
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}