package com.example.vivalink;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.*;
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

    // التعديل الجوهري: تغيير نوع القائمة إلى Notifications ليتوافق مع الأدابتر
    List<Notifications> incomingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbank_notification);

        dbRef = FirebaseDatabase.getInstance().getReference("Notifications");
        initViews();

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

    private void initViews() {
        tabSend = findViewById(R.id.tabSend);
        tabReceive = findViewById(R.id.tabReceive);
        layoutSend = findViewById(R.id.layoutSend);
        rvIncoming = findViewById(R.id.rvIncomingAdmin);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSendNotification);

        rvIncoming.setLayoutManager(new LinearLayoutManager(this));

        // الآن القائمة نوعها Notifications والأدابتر رح يقبلها بدون أي إيرور
        adapter = new BloodBankNotificationAdapter(incomingList);
        rvIncoming.setAdapter(adapter);
    }

    private void sendNotificationToDonors() {
        String msg = etMessage.getText().toString().trim();
        if (msg.isEmpty()) {
            Toast.makeText(this, "يرجى كتابة نص الرسالة", Toast.LENGTH_SHORT).show();
            return;
        }

        String requiredBloodType = "A+"; // مثال
        String currentHospitalCity = "نابلس"; // مثال

        DatabaseReference donorsRef = FirebaseDatabase.getInstance().getReference("Donors");

        donorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot donorSnap : snapshot.getChildren()) {
                    String dBlood = donorSnap.child("bloodType").getValue(String.class);
                    String dCity = donorSnap.child("city").getValue(String.class);
                    String donorId = donorSnap.getKey();

                    if (requiredBloodType.equals(dBlood) && currentHospitalCity.equals(dCity)) {
                        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("Notifications").push();
                        String id = notifRef.getKey();

                        HashMap<String, Object> note = new HashMap<>();
                        note.put("notificationId", id);
                        note.put("title", "🚨 طلب دم عاجل في مدينتك");
                        note.put("message", msg);
                        note.put("type", "urgent");
                        note.put("targetType", "DONOR");
                        note.put("userId", donorId); // تأكدي إن الحقل في كلاس Notifications هو userId وليس targetUserId
                        note.put("createdAt", System.currentTimeMillis());
                        note.put("isRead", false);

                        if (id != null) notifRef.setValue(note);
                        count++;
                    }
                }

                if (count > 0) {
                    Toast.makeText(BloodBankNotificationActivity.this, "تم إرسال " + count + " تنبيه ✅", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(BloodBankNotificationActivity.this, "لا يوجد متبرعين مطابقين حالياً", Toast.LENGTH_SHORT).show();
                }
                etMessage.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BloodBankNotificationActivity.this, "خطأ في الوصول للمتبرعين", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadIncomingNotifications() {
        dbRef.orderByChild("targetType").equalTo("ADMIN")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        incomingList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            // التعديل: تحويل البيانات إلى كلاس Notifications الموحد
                            Notifications m = ds.getValue(Notifications.class);
                            if (m != null) incomingList.add(0, m);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}