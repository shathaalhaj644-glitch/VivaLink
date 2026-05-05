package com.example.vivalink;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
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
        createChannel();

        tabSend.setOnClickListener(v -> {
            layoutSend.setVisibility(View.VISIBLE);
            rvIncoming.setVisibility(View.GONE);
        });

        tabReceive.setOnClickListener(v -> {
            layoutSend.setVisibility(View.GONE);
            rvIncoming.setVisibility(View.VISIBLE);
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

        cbAp = findViewById(R.id.cbAp); cbAn = findViewById(R.id.cbAn);
        cbBp = findViewById(R.id.cbBp); cbBn = findViewById(R.id.cbBn);
        cbOp = findViewById(R.id.cbOp); cbOn = findViewById(R.id.cbOn);
        cbABp = findViewById(R.id.cbABp); cbABn = findViewById(R.id.cbABn);

        rvIncoming.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BloodBankNotificationAdapter(incomingList);
        rvIncoming.setAdapter(adapter);
    }

    private void fetchCurrentHospitalOrStaffData() {
        String uid = FirebaseAuth.getInstance().getUid();

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

                        tvLocationInfo.setText(currentCity + " | " + hospitalName);
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void sendNotificationToDonors() {

        String msg = etMessage.getText().toString().trim();
        if (msg.isEmpty()) return;

        List<String> bloods = new ArrayList<>();
        if (cbAp.isChecked()) bloods.add("A+");
        if (cbAn.isChecked()) bloods.add("A-");
        if (cbBp.isChecked()) bloods.add("B+");
        if (cbBn.isChecked()) bloods.add("B-");
        if (cbOp.isChecked()) bloods.add("O+");
        if (cbOn.isChecked()) bloods.add("O-");
        if (cbABp.isChecked()) bloods.add("AB+");
        if (cbABn.isChecked()) bloods.add("AB-");

        for (String blood : bloods) {

            pushToFirebase(hospitalId, "ADMIN", blood, msg);
            pushToFirebase(null, "DONOR", blood, msg);
        }

        Toast.makeText(this, "تم الإرسال", Toast.LENGTH_SHORT).show();
    }

    private void pushToFirebase(String targetUserId, String targetType, String blood, String message) {

        DatabaseReference ref = dbRef.push();

        long time = System.currentTimeMillis();
        String formattedTime = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
                .format(new Date(time));

        HashMap<String, Object> map = new HashMap<>();
        map.put("notificationId", ref.getKey());
        map.put("title", "طلب دم: " + hospitalName);
        map.put("message", message);
        map.put("bloodType", blood);
        map.put("city", currentCity);
        map.put("targetType", targetType);
        map.put("targetUserId", targetUserId);
        map.put("createdAt", time);
        map.put("timeText", formattedTime); // 🔥 الجديد
        map.put("isRead", false);

        ref.setValue(map);

        // 🔔 إشعار للهاتف (مهم)
        if ("ADMIN".equals(targetType)) {
            showNotification("طلب دم جديد", message);
        }
    }

    private void loadIncomingNotifications() {

        String myId = FirebaseAuth.getInstance().getUid();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                incomingList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    BloodBankNotificationModel n = ds.getValue(BloodBankNotificationModel.class);

                    if (n != null &&
                            "ADMIN".equals(n.getTargetType()) &&
                            myId.equals(n.getTargetUserId())) {

                        incomingList.add(0, n);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // 🔔 Notification system
    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "blood_channel",
                    "Blood Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String message) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "blood_channel")
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(new Random().nextInt(), builder.build());
    }
}