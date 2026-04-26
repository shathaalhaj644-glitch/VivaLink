package com.example.vivalink;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DonorsHomeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int CAMERA_REQUEST = 102;

    private TextView tvWelcomeDonor, tvDonationCount, tvLastDonationDate;
    private TextView tvUrgentHospital, tvUrgentBlood, tvUrgentUnits, tvRequestDate, tvStatusTitle;
    private TextView tvDaysSinceLastTest;
    private CardView cardBloodTestAlert, cardUrgentRequest, layoutNoRequest;
    private Button btnViewRequests, btnGoToDonate, btnGoToProfile, btnMarkBloodTest;
    private CardView btnNotificationsCard;

    private DatabaseReference dbRef;
    private String userId;
    private String lastDonationDateFromDB, donorBloodType, donorCity, donorName;
    private String hospitalName, bloodType, units, confirmedAt, department, city, requestId, currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donors_home);

        initViews();

        // 1. طلب إذن الإشعارات لأندرويد 13 فما فوق
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            dbRef = FirebaseDatabase.getInstance().getReference();
            loadDonorData();
            startNotificationMonitoring(); // تفعيل مراقب الإشعارات
        }

        // إعداد المستمعين للأزرار (Listeners)
        btnViewRequests.setOnClickListener(v -> startActivity(new Intent(this, RequestsActivity.class)));

        btnNotificationsCard.setOnClickListener(v ->
                startActivity(new Intent(this, DonorNotificationActivity.class)));

        btnGoToProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        btnMarkBloodTest.setOnClickListener(v -> showPeriodicTestDialog());

        btnGoToDonate.setOnClickListener(v -> {
            if ("مغلق".equals(currentStatus)) {
                goToDetails();
                return;
            }

            dbRef.child("Donors").child(userId).get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String testStatus = snapshot.child("bloodTestStatus").getValue(String.class);
                    String lastTest = snapshot.child("lastBloodTest").getValue(String.class);
                    String lastDonation = snapshot.child("lastDonation").getValue(String.class);

                    if ("معلق".equals(testStatus)) {
                        Toast.makeText(this, "⏳ فحصك قيد المراجعة، لا يمكنك التبرع حالياً", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (isTestExpired(lastTest)) {
                        showPeriodicTestDialog();
                        return;
                    }

                    if (isTooSoonToDonate(lastDonation)) {
                        checkDonationEligibility();
                        return;
                    }

                    goToDonate();
                }
            });
        });
    }

    private void initViews() {
        tvWelcomeDonor = findViewById(R.id.tvWelcomeDonor);
        tvDonationCount = findViewById(R.id.tvDonationCount);
        tvLastDonationDate = findViewById(R.id.tvLastDonationDate);
        tvUrgentHospital = findViewById(R.id.tvHospitalName);
        tvUrgentBlood = findViewById(R.id.tvBloodType);
        tvUrgentUnits = findViewById(R.id.tvUnits);
        tvRequestDate = findViewById(R.id.tvRequestDate);
        tvStatusTitle = findViewById(R.id.tvStatusTitle);

        cardBloodTestAlert = findViewById(R.id.cardBloodTestAlert);
        cardUrgentRequest = findViewById(R.id.cardUrgentRequest);
        layoutNoRequest = findViewById(R.id.layoutNoRequest);

        tvDaysSinceLastTest = findViewById(R.id.tvDaysSinceLastTest);
        btnMarkBloodTest = findViewById(R.id.btnMarkBloodTest);
        btnNotificationsCard = findViewById(R.id.btnNotificationsCard);
        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnGoToDonate = findViewById(R.id.btnGoToDonate);
        btnGoToProfile = findViewById(R.id.btnGoToProfile);
    }

    private void startNotificationMonitoring() {
        NotificationsHelper helper = new NotificationsHelper();
        dbRef.child("Notifications").orderByChild("userId").equalTo(userId).limitToLast(1)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Notifications n = snapshot.getValue(Notifications.class);
                        if (n != null && !n.isRead()) {
                            helper.showSystemNotification(DonorsHomeActivity.this, n.getTitle(), n.getMessage());
                        }
                    }
                    @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                    @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                    @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadDonorData() {
        dbRef.child("Donors").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    donorName = snapshot.child("fullName").getValue(String.class);
                    lastDonationDateFromDB = snapshot.child("lastDonation").getValue(String.class);
                    donorBloodType = snapshot.child("bloodType").getValue(String.class);
                    donorCity = snapshot.child("city").getValue(String.class);
                    String lastBloodTest = snapshot.child("lastBloodTest").getValue(String.class);
                    String bloodTestStatus = snapshot.child("bloodTestStatus").getValue(String.class);
                    Object countObj = snapshot.child("donationCount").getValue();

                    tvWelcomeDonor.setText("أهلاً " + (donorName != null ? donorName : "") + " !");
                    tvLastDonationDate.setText(lastDonationDateFromDB != null ? lastDonationDateFromDB : "--");
                    tvDonationCount.setText(countObj != null ? String.valueOf(countObj) : "0");

                    checkBloodTestInterval(lastBloodTest, bloodTestStatus);
                    checkAndNotifyEligibility(lastDonationDateFromDB);

                    if (donorCity != null && donorBloodType != null) {
                        loadFilteredRequest();
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void checkAndNotifyEligibility(String lastDateStr) {
        if (lastDateStr == null || lastDateStr.equals("--") || lastDateStr.isEmpty()) return;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date lastDate = sdf.parse(normalizeNumbers(lastDateStr));
            long diff = new Date().getTime() - lastDate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if (days >= 120) {
                dbRef.child("Donors").child(userId).child("canDonate").setValue(true);
                sendEligibilityNotification();
            }
        } catch (Exception e) { Log.e("EligibilityError", e.getMessage()); }
    }

    private void sendEligibilityNotification() {
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("Notifications").push();
        String id = notifRef.getKey();
        if (id != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("notificationId", id);
            map.put("title", "🌟 حان وقت إنقاذ الأرواح!");
            map.put("message", "لقد مر أكثر من 4 أشهر على تبرعك الأخير. يمكنك الآن التبرع مجدداً!");
            map.put("type", "eligibility_reminder");
            map.put("targetType", "DONOR");
            map.put("userId", userId);
            map.put("createdAt", String.valueOf(System.currentTimeMillis()));
            map.put("isRead", false);
            notifRef.setValue(map);
        }
    }

    private void showPeriodicTestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_periodic_test, null);
        Button btnUploadNow = view.findViewById(R.id.btnUploadNow);
        TextView tvLater = view.findViewById(R.id.tvLater);
        AlertDialog dialog = builder.setView(view).create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnUploadNow.setOnClickListener(v -> {
            dialog.dismiss();
            showImageSourceOptions();
        });
        tvLater.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showImageSourceOptions() {
        String[] options = {"التقاط صورة بالكاميرا", "اختيار من ألبوم الكاميرا"};
        new AlertDialog.Builder(this).setTitle("رفع صورة الفحص").setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = null;
            try {
                if (requestCode == PICK_IMAGE_REQUEST && data.getData() != null) {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } else if (requestCode == CAMERA_REQUEST && data.getExtras() != null) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                }
                if (bitmap != null) {
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 600, 800, true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    String base64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                    uploadTestData(base64Image);
                }
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void uploadTestData(String base64Image) {
        String refNum = "REF-" + System.currentTimeMillis() % 10000;
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(new Date());
        Map<String, Object> updates = new HashMap<>();
        updates.put("bloodTestProofUrl", base64Image);
        updates.put("bloodTestStatus", "معلق");
        updates.put("testSubmittedAt", timestamp);

        dbRef.child("Donors").child(userId).updateChildren(updates).addOnSuccessListener(aVoid -> {
            DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("Notifications").push();
            HashMap<String, Object> notifData = new HashMap<>();
            notifData.put("notificationId", notifRef.getKey());
            notifData.put("title", "فحص دم جديد 🔬");
            notifData.put("message", "قام " + donorName + " برفع صورة فحصه الدوري.");
            notifData.put("type", "new_test");
            notifData.put("targetType", "ADMIN");
            notifData.put("createdAt", String.valueOf(System.currentTimeMillis()));
            notifData.put("isRead", false);
            notifRef.setValue(notifData);
            showSuccessDialog(refNum);
        });
    }

    private void checkBloodTestInterval(String lastBloodTest, String status) {
        if ("معلق".equals(status)) {
            cardBloodTestAlert.setVisibility(View.VISIBLE);
            cardBloodTestAlert.setCardBackgroundColor(Color.parseColor("#FFF3E0"));
            tvDaysSinceLastTest.setText("⏳ صورة فحصك قيد المراجعة...");
            btnMarkBloodTest.setVisibility(View.GONE);
            return;
        }
        if (isTestExpired(lastBloodTest)) {
            cardBloodTestAlert.setVisibility(View.VISIBLE);
            cardBloodTestAlert.setCardBackgroundColor(Color.parseColor("#FCE4EC"));
            tvDaysSinceLastTest.setText("⏰ حان موعد فحصك الدوري!");
            btnMarkBloodTest.setVisibility(View.VISIBLE);
        } else {
            cardBloodTestAlert.setVisibility(View.GONE);
        }
    }

    private void loadFilteredRequest() {
        dbRef.child("Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot data : snapshot.getChildren()) {
                    String reqBlood = data.child("bloodType").getValue(String.class);
                    String reqCity = data.child("city").getValue(String.class);
                    String status = data.child("status").getValue(String.class);
                    if (reqBlood != null && reqCity != null && status != null) {
                        if (reqBlood.equalsIgnoreCase(donorBloodType) && reqCity.equalsIgnoreCase(donorCity)) {
                            if (status.equals("ملغي")) continue;
                            found = true;
                            requestId = data.getKey();
                            currentStatus = status;
                            hospitalName = data.child("hospitalName").getValue(String.class);
                            bloodType = reqBlood;
                            units = String.valueOf(data.child("units").getValue());
                            confirmedAt = formatMyTime(String.valueOf(data.child("confirmedAt").getValue()));
                            updateRequestUI(true, status);
                            break;
                        }
                    }
                }
                if (!found) updateRequestUI(false, "");
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateRequestUI(boolean found, String status) {
        if (found) {
            layoutNoRequest.setVisibility(View.GONE);
            cardUrgentRequest.setVisibility(View.VISIBLE);
            tvUrgentHospital.setText("المستشفى: " + hospitalName);
            tvUrgentBlood.setText("الفصيلة المطلوبة: " + bloodType);
            tvUrgentUnits.setText("الوحدات المطلوبة: " + units);
            tvRequestDate.setText("📅 التاريخ: " + confirmedAt);

            if ("عاجل".equals(status)) {
                tvStatusTitle.setText("🚨 طلب تبرع عاجل!");
                tvStatusTitle.setTextColor(Color.RED);
            } else if ("مغلق".equals(status)) {
                tvStatusTitle.setText("✅ تم اكتمال الطلب");
                tvStatusTitle.setTextColor(Color.GREEN);
            }
        } else {
            layoutNoRequest.setVisibility(View.VISIBLE);
            cardUrgentRequest.setVisibility(View.GONE);
        }
    }

    private boolean isTestExpired(String lastBloodTest) {
        if (lastBloodTest == null || lastBloodTest.equals("--") || lastBloodTest.isEmpty()) return true;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            long diff = new Date().getTime() - sdf.parse(normalizeNumbers(lastBloodTest)).getTime();
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) >= 120;
        } catch (Exception e) { return true; }
    }

    private boolean isTooSoonToDonate(String lastDonationDate) {
        if (lastDonationDate == null || lastDonationDate.equals("--") || lastDonationDate.isEmpty()) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            long diff = new Date().getTime() - sdf.parse(normalizeNumbers(lastDonationDate)).getTime();
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) < 120;
        } catch (Exception e) { return false; }
    }

    private void checkDonationEligibility() {
        Toast.makeText(this, "عذراً، لم يمر 4 أشهر على تبرعك الأخير بعد.", Toast.LENGTH_LONG).show();
    }

    private void goToDonate() {
        Intent intent = new Intent(this, DonateActivity.class);
        intent.putExtra("requestId", requestId);
        intent.putExtra("hospitalName", hospitalName);
        intent.putExtra("bloodType", bloodType);
        startActivity(intent);
    }

    private void goToDetails() {
        Intent intent = new Intent(this, RequestsDetailsActivity.class);
        intent.putExtra("requestId", requestId);
        startActivity(intent);
    }

    private void showSuccessDialog(String refNumber) {
        Toast.makeText(this, "تم رفع الفحص بنجاح. رقم المرجع: " + refNumber, Toast.LENGTH_LONG).show();
    }

    private String formatMyTime(String raw) {
        return (raw == null || raw.equals("null")) ? "--" : raw;
    }

    private String normalizeNumbers(String input) {
        if (input == null) return "";
        return input.replace("٠","0").replace("١","1").replace("٢","2").replace("٣","3")
                .replace("٤","4").replace("٥","5").replace("٦","6").replace("٧","7")
                .replace("٨","8").replace("٩","9").replace("-","/");
    }
}