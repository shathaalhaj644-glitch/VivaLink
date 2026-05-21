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
    private String hospitalName = "";
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
                        loadIncomingNotifications();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void sendNotificationToDonors() {
        String msg = etMessage.getText().toString().trim();
        String myUid = FirebaseAuth.getInstance().getUid();

        if (msg.isEmpty() || currentCity == null) return;

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

        for (String blood : selectedBloods) {
            // 1. للموظفين الزملاء (ADMIN) - نرسله بـ targetUserId محدد
            pushToFirebase(hospitalId, "ADMIN", blood, msg, myUid);
            // 2. للمتبرعين (DONOR) - نرسله بـ targetUserId = null لكي يصل للكل حسب المدينة والفصيلة
            pushToFirebase(null, "DONOR", blood, msg, myUid);
        }

        Toast.makeText(this, "تم الإرسال بنجاح", Toast.LENGTH_SHORT).show();
        etMessage.setText("");
        clearCheckboxes();
    }

    private void pushToFirebase(String targetUserId, String targetType, String blood, String message, String senderId) {
        DatabaseReference newNotif = dbRef.push();
        String notifId = newNotif.getKey();

        String title = "🚨 طلب دم عاجل: " + blood;
        String fullMessage = "مطلوب فصيلة (" + blood + ") في " + hospitalName + "\n" + message;

        HashMap<String, Object> data = new HashMap<>();
        data.put("notificationId", notifId);
        data.put("title", title);
        data.put("message", fullMessage);
        data.put("targetType", targetType);
        data.put("targetUserId", targetUserId);
        data.put("senderId", senderId);
        data.put("type", "urgent_request");
        data.put("bloodType", blood);
        data.put("city", currentCity);
        data.put("hospitalName", hospitalName);
        data.put("hospitalId", hospitalId);
        data.put("createdAt", System.currentTimeMillis());
        data.put("isRead", false);

        // 1. تخزين في قاعدة البيانات (ليظهر داخل قائمة الإشعارات)
        newNotif.setValue(data).addOnSuccessListener(aVoid -> {

            // 2. إرسال التنبيه للخارج (ليظهر على شاشة التلفون)
            if ("DONOR".equals(targetType)) {
                // نرسل إشعار عام لكل المتبرعين المشتركين في "موضوع" (Topic) التبرع
                sendFcmNotification("donors", title, fullMessage);
            } else if ("ADMIN".equals(targetType) && targetUserId != null) {
                // نرسل إشعار للموظف المسؤول (عن طريق موضوع خاص به أو ID)
                sendFcmNotification("admin_" + targetUserId, title, fullMessage);
            }
        });
    }

    // 🔹 إضافة الدالة الناقصة لحل الإيرور (Cannot resolve method)
    private void sendFcmNotification(String topic, String title, String body) {
        // ملاحظة: لإرسال إشعار FCM حقيقي من داخل التطبيق (بدون سيرفر)،
        // يفضل استخدام واجهة Firebase Console أو Cloud Functions.
        // برمجياً، سنكتفي بطباعة السجل هنا أو استدعاء API خارجي إذا قمتِ بإعداده.
        Log.d("FCM_LOG", "جاري إرسال تنبيه للموضوع: " + topic + " | العنوان: " + title);

        // إذا كنتِ تريدين الإرسال البرمجي المجاني، يجب إعداد مكتبة HTTP لإرسال الطلب لـ https://fcm.googleapis.com/fcm/send
    }

    // دالة وهمية تشرح المنطق (لأن الإرسال الفعلي للخارج يحتاج FCM Server أو مكتبة بسيطة)
    private void sendNotificationToExternal(String title, String message, String city, String blood) {
        // هنا يتم استدعاء FCM لإرسال الإشعار
        // لكي يعمل "من برة" مجاناً، أسهل طريقة هي إرسال طلب HTTP لـ Firebase
        Log.d("FCM", "جاري إرسال إشعار خارجي لمتبرعي " + city + " فصيلة " + blood);
    }
    private void loadIncomingNotifications() {
        final String myUid = FirebaseAuth.getInstance().getUid();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                incomingList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        BloodBankNotificationModel n = ds.getValue(BloodBankNotificationModel.class);
                        if (n == null) continue;

                        String type = n.getType();
                        String targetType = n.getTargetType();
                        String targetUserId = n.getTargetUserId();

                        // 1. استقبال إشعار وصول المتبرع (Donor Arrival)
                        // ابحثي عن هذا الجزء داخل loadIncomingNotifications وعدليه
                        // 1. استقبال إشعار وصول المتبرع (Donor Arrival)
                        // 1. استقبال إشعار وصول المتبرع (Donor Arrival) المطوّر
                        if ("donor_arrival".equals(type) || "donation_confirmed".equals(type)) {

                            // قراءة اسم المستشفى المكتوب داخل الإشعار المرفوع في Firebase
                            String notifHospitalName = ds.child("hospitalName").getValue(String.class);

                            // 🔥 القفل الذكي: الإشعار يظهر فقط إذا كان اسم مستشفى الإشعار مطابق تماماً لاسم مستشفى الموظف الحالي!
                            if (hospitalName != null && notifHospitalName != null
                                    && hospitalName.trim().equalsIgnoreCase(notifHospitalName.trim())) {

                                incomingList.add(0, n);
                            }
                        }
                        // 2. إشعارات الفحوصات (حسب المدينة)
                        else if ("new_test_upload".equals(type)) {
                            if (currentCity != null && currentCity.equals(n.getCity())) {
                                incomingList.add(0, n);
                            }
                        }

                        // 3. طلبات الدم العاجلة (من الموظفين الآخرين)
                        else if ("urgent_request".equals(type) && "ADMIN".equals(targetType)) {
                            String sId = ds.child("senderId").getValue(String.class);
                            if (hospitalId != null && hospitalId.equals(targetUserId)) {
                                // تظهر فقط إذا لم أكن أنا من أرسلها
                                if (sId != null && !sId.equals(myUid)) {
                                    incomingList.add(0, n);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("VivaLink", "خطأ في قراءة الإشعار: " + e.getMessage());
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void clearCheckboxes() {
        cbAp.setChecked(false); cbAn.setChecked(false);
        cbBp.setChecked(false); cbBn.setChecked(false);
        cbOp.setChecked(false); cbOn.setChecked(false);
        cbABp.setChecked(false); cbABn.setChecked(false);
    }
}