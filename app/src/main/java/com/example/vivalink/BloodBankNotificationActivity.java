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

    // تعريف الـ 8 فصائل
    private CheckBox cbAp, cbAn, cbBp, cbBn, cbOp, cbOn, cbABp, cbABn;

    private DatabaseReference dbRef;
    private BloodBankNotificationAdapter adapter;
    private List<BloodBankNotificationModel> incomingList = new ArrayList<>();

    private String currentCity = "";
    private String hospitalName = "";

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
        tvLocationInfo = findViewById(R.id.tvLocationInfo);

        // ربط الـ 8 CheckBoxes
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

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot staff = snapshot.child("BloodBankStaff").child(uid);
                DataSnapshot hosp = snapshot.child("Hospitals").child(uid);

                if (staff.exists()) {
                    currentCity = staff.child("city").getValue(String.class);
                    hospitalName = staff.child("hospitalName").getValue(String.class);
                } else if (hosp.exists()) {
                    currentCity = hosp.child("city").getValue(String.class);
                    hospitalName = hosp.child("hospitalName").getValue(String.class);
                }

                if (currentCity != null) {
                    tvLocationInfo.setText("المدينة: " + currentCity + " | " + hospitalName);
                }
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

        // تجميع الفصائل المختارة من الـ 8 خيارات
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

                    if (dCity != null && dBlood != null) {
                        String cleanDonorCity = normalizeArabic(dCity);
                        String cleanCurrentCity = normalizeArabic(currentCity);

                        if (cleanDonorCity.equals(cleanCurrentCity)) {
                            for (String blood : selectedBloods) {
                                if (isBloodMatching(dBlood, blood)) {
                                    pushToFirebase(donorSnap.getKey(), dBlood, msg);
                                    count++;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (count > 0) {
                    Toast.makeText(BloodBankNotificationActivity.this, "تم الإرسال لـ " + count + " متبرع", Toast.LENGTH_LONG).show();
                    etMessage.setText("");
                } else {
                    Toast.makeText(BloodBankNotificationActivity.this, "لم يتم العثور على متطابقين في: " + currentCity, Toast.LENGTH_LONG).show();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private String normalizeArabic(String text) {
        return text.trim().replace(" ", "").replace("ة", "ه").replace("أ", "ا").replace("إ", "ا").replace("آ", "ا");
    }

    private boolean isBloodMatching(String donorBlood, String selectedBlood) {
        String b1 = donorBlood.trim().replace(" ", "");
        String b2 = selectedBlood.trim().replace(" ", "");
        if (b1.equalsIgnoreCase(b2)) return true;
        String reversed = (b1.length() >= 2) ? b1.substring(b1.length()-1) + b1.substring(0, b1.length()-1) : b1;
        return reversed.equalsIgnoreCase(b2);
    }

    private void pushToFirebase(String donorId, String blood, String message) {
        DatabaseReference newNotif = dbRef.push();
        HashMap<String, Object> data = new HashMap<>();
        data.put("notificationId", newNotif.getKey());
        data.put("title", "🚨 طلب دم عاجل: " + hospitalName);
        data.put("message", "مطلوب فصيلة " + blood + " في " + currentCity + "\n" + message);
        data.put("targetType", "DONOR");
        data.put("userId", donorId);
        data.put("createdAt", System.currentTimeMillis());
        data.put("isRead", false);
        data.put("type", "urgent_request");
        newNotif.setValue(data);
    }

    private void loadIncomingNotifications() {
        dbRef.orderByChild("targetType").equalTo("ADMIN").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                incomingList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        BloodBankNotificationModel m = ds.getValue(BloodBankNotificationModel.class);
                        if (m != null) incomingList.add(0, m);
                    } catch (Exception e) { Log.e("Error", "Read Error"); }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}