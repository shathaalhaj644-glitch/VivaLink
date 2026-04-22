package com.example.vivalink;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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

    private DatabaseReference dbRef;
    private String userId;
    private String lastDonationDateFromDB, donorBloodType, donorCity, donorName;
    private String hospitalName, bloodType, units, confirmedAt, department, city, requestId, currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donors_home);

        initViews();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            dbRef = FirebaseDatabase.getInstance().getReference();
            loadDonorData();
        }

        btnViewRequests.setOnClickListener(v -> startActivity(new Intent(this, RequestsActivity.class)));

        btnGoToDonate.setOnClickListener(v -> {
            // 1. إذا كان طلب المستشفى أصلاً مغلق
            if ("مغلق".equals(currentStatus)) {
                goToDetails();
                return;
            }

            // 2. فحص شروط المتبرع من Firebase
            dbRef.child("Donors").child(userId).get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String testStatus = snapshot.child("bloodTestStatus").getValue(String.class);
                    String lastTest = snapshot.child("lastBloodTest").getValue(String.class);
                    String lastDonation = snapshot.child("lastDonation").getValue(String.class); // تأكدي من جلب هذا السطر

                    // أ- لو لسه الموظف ما وافق على الصورة
                    if ("معلق".equals(testStatus)) {
                        Toast.makeText(this, "⏳ فحصك قيد المراجعة، لا يمكنك التبرع حالياً", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // ب- لو الفحص الدوري منتهي (مر عليه 120 يوم فحص)
                    if (isTestExpired(lastTest)) {
                        showPeriodicTestDialog();
                        return;
                    }

                    // ج- لو لسه ما صار له 4 شهور متبرع (هنا المنع الإضافي)
                    if (isTooSoonToDonate(lastDonation)) {
                        // استدعاء دالة التنبيه اللي بتعرض الأيام المتبقية
                        checkDonationEligibility();
                        return;
                    }

                    // د- لو تخطى كل الشروط بنجاح
                    goToDonate();
                }
            });
        });

        btnGoToProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        // تم تعديل هذا الزر ليفتح الاستوديو بدلاً من التحديث المباشر
        btnMarkBloodTest.setOnClickListener(v -> showPeriodicTestDialog());
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

        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnGoToDonate = findViewById(R.id.btnGoToDonate);
        btnGoToProfile = findViewById(R.id.btnGoToProfile);
    }
    private void showPeriodicTestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // تأكدي أن اسم الملف dialog_periodic_test مطابق للملف عندك في Layout
        View view = getLayoutInflater().inflate(R.layout.dialog_periodic_test, null);

        Button btnUploadNow = view.findViewById(R.id.btnUploadNow);
        TextView tvLater = view.findViewById(R.id.tvLater);

        AlertDialog dialog = builder.setView(view).create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnUploadNow.setOnClickListener(v -> {
            dialog.dismiss();
            showImageSourceOptions(); // هذه الدالة سنضيفها في التعديل القادم
        });

        tvLater.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    private void showImageSourceOptions() {
        String[] options = {"التقاط صورة بالكاميرا", "اختيار من ألبوم الكاميرا"};
        new AlertDialog.Builder(this)
                .setTitle("رفع صورة الفحص")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // فتح الكاميرا
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAMERA_REQUEST);
                    } else {
                        // فتح الاستوديو
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, PICK_IMAGE_REQUEST);
                    }
                }).show();
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

                    // استدعاء منطق الفحص الدوري المطور
                    checkBloodTestInterval(lastBloodTest, bloodTestStatus);

                    if (donorCity != null && donorBloodType != null) {
                        loadFilteredRequest();
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void checkBloodTestInterval(String lastBloodTest, String status) {
        // حالة "قيد المراجعة" (الصورة الثانية)
        if ("معلق".equals(status)) {
            cardBloodTestAlert.setVisibility(View.VISIBLE);
            cardBloodTestAlert.setCardBackgroundColor(Color.parseColor("#FFF3E0")); // برتقالي خفيف
            tvDaysSinceLastTest.setText("⏳ صورة فحصك قيد المراجعة من موظف البنك\nلا يمكنك التبرع حتى يتم القبول.");
            btnMarkBloodTest.setVisibility(View.GONE);
            return;
        }

        // حالة مرور 4 شهور (الصورة الأولى)
        if (lastBloodTest != null && !lastBloodTest.equals("--") && !lastBloodTest.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date lastDate = sdf.parse(normalizeNumbers(lastBloodTest));
                long diff = new Date().getTime() - lastDate.getTime();
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                if (days >= 120) {
                    cardBloodTestAlert.setVisibility(View.VISIBLE);
                    cardBloodTestAlert.setCardBackgroundColor(Color.parseColor("#FCE4EC")); // أحمر خفيف
                    tvDaysSinceLastTest.setText("⏰ حان موعد فحصك الدوري!\nمر " + days + " يوماً على آخر فحص.\nلا يمكنك التبرع قبل إتمام الفحص وقبوله.");
                    btnMarkBloodTest.setVisibility(View.VISIBLE);
                    btnMarkBloodTest.setText("رفع صورة الفحص");
                } else {
                    cardBloodTestAlert.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                cardBloodTestAlert.setVisibility(View.GONE);
            }
        } else {
            // إذا لم يكن هناك فحص سابق أبداً
            cardBloodTestAlert.setVisibility(View.VISIBLE);
            tvDaysSinceLastTest.setText("يُرجى إجراء فحص دم دوري لضمان سلامتك.");
            btnMarkBloodTest.setText("رفع صورة الفحص");
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "اختر صورة الفحص"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = null;
            try {
                if (requestCode == PICK_IMAGE_REQUEST && data.getData() != null) {
                    // حالة 1: الصورة جاية من ألبوم الصور
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } else if (requestCode == CAMERA_REQUEST && data.getExtras() != null) {
                    // حالة 2: الصورة جاية من الكاميرا مباشرة
                    bitmap = (Bitmap) data.getExtras().get("data");
                }

                if (bitmap != null) {
                    // تصغير حجم الصورة وضغطها عشان Base64 ما يكون طويل جداً
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 600, 800, true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    String base64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

                    // نرسل الصورة لـ Firebase
                    uploadTestData(base64Image);
                }
            } catch (IOException e) {
                Toast.makeText(this, "حدث خطأ أثناء معالجة الصورة", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void uploadTestData(String base64Image) {
        java.util.Random r = new java.util.Random();
        String refNum = "REF-2026-" + (r.nextInt(9000)+1000) + "-" + (r.nextInt(9000)+1000);
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(new Date());

        Map<String, Object> updates = new HashMap<>();
        updates.put("bloodTestProofUrl", base64Image);
        updates.put("bloodTestStatus", "معلق");
        updates.put("refNumber", refNum);
        updates.put("testSubmittedAt", timestamp);

        dbRef.child("Donors").child(userId).updateChildren(updates).addOnSuccessListener(aVoid -> {
            // إنشاء إشعار للموظف
            createNotificationForAdmin(refNum);
            // إظهار نافذة النجاح للمتبرع (الصورة الرابعة)
            showSuccessDialog(refNum);
        });
    }

    private void createNotificationForAdmin(String refNum) {
        DatabaseReference notifRef = dbRef.child("Notifications").push();
        Map<String, Object> notif = new HashMap<>();
        notif.put("title", "💉 فحص دم جديد");
        notif.put("message", donorName + " رفع صورة فحصه الدوري. رقم المرجع: " + refNum);
        notif.put("type", "new_test");
        notif.put("donorId", userId);
        notif.put("isRead", false);
        notifRef.setValue(notif);
    }

    private void showSuccessDialog(String refNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.dialog_upload_success, null);
        TextView tvRef = v.findViewById(R.id.tvRefNumber);
        Button btnDone = v.findViewById(R.id.btnDone);

        tvRef.setText(refNumber);
        AlertDialog dialog = builder.setView(v).setCancelable(false).create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnDone.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
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
                        if (reqBlood.trim().equalsIgnoreCase(donorBloodType.trim()) &&
                                reqCity.trim().equalsIgnoreCase(donorCity.trim())) {
                            if (status.equals("ملغي")) continue;
                            found = true;
                            requestId = data.getKey();
                            currentStatus = status;
                            hospitalName = data.child("hospitalName").getValue(String.class);
                            bloodType = reqBlood;
                            units = data.child("units").getValue(String.class);
                            city = reqCity;
                            department = data.child("department").getValue(String.class);
                            confirmedAt = formatMyTime(data.child("confirmedAt").getValue(String.class));
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
            if ("عاجل".equals(status)) {
                tvStatusTitle.setText("🚨 طلب تبرع عاجل!");
                tvStatusTitle.setTextColor(Color.RED);
                btnGoToDonate.setText("تبرع الآن");
            } else if ("مغلق".equals(status)) {
                tvStatusTitle.setText("✅ لقد تم التبرع لهذا الطلب");
                tvStatusTitle.setTextColor(Color.parseColor("#2E7D32"));
                btnGoToDonate.setText("عرض التفاصيل");
            } else {
                tvStatusTitle.setText("🩸 طلب تبرع دم");
                tvStatusTitle.setTextColor(Color.BLACK);
                btnGoToDonate.setText("تبرع الآن");
            }
            tvUrgentHospital.setText("المستشفى: " + hospitalName);
            tvUrgentBlood.setText("الفصيلة المطلوبة: " + bloodType);
            tvUrgentUnits.setText("الوحدات المطلوبة: " + units);
            tvRequestDate.setText("📅 تاريخ الطلب: " + confirmedAt);
        } else {
            layoutNoRequest.setVisibility(View.VISIBLE);
            cardUrgentRequest.setVisibility(View.GONE);
        }
    }

    private void checkDonationEligibility() {
        if (lastDonationDateFromDB == null || lastDonationDateFromDB.equals("--") || lastDonationDateFromDB.isEmpty()) {
            goToDonate();
            return;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date lastDate = sdf.parse(normalizeNumbers(lastDonationDateFromDB));
            long diff = new Date().getTime() - lastDate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            if (days < 120) {
                showIneligibilityAlert(120 - days, lastDate);
            } else {
                goToDonate();
            }
        } catch (Exception e) { goToDonate(); }
    }

    private void goToDonate() {
        if (hospitalName == null) return;
        Intent intent = new Intent(this, DonateActivity.class);
        intent.putExtra("requestId", requestId);
        intent.putExtra("hospitalName", hospitalName);
        intent.putExtra("bloodType", bloodType);
        intent.putExtra("units", units);
        intent.putExtra("confirmedAt", confirmedAt);
        intent.putExtra("city", city);
        intent.putExtra("department", department);
        startActivity(intent);
    }

    private void goToDetails() {
        Intent intent = new Intent(this, RequestsDetailsActivity.class);
        intent.putExtra("requestId", requestId);
        intent.putExtra("hospitalName", hospitalName);
        intent.putExtra("city", city);
        intent.putExtra("bloodType", bloodType);
        intent.putExtra("department", department);
        intent.putExtra("units", units);
        intent.putExtra("confirmedAt", confirmedAt);
        intent.putExtra("isDonated", true);
        startActivity(intent);
    }

    private String formatMyTime(String raw) {
        if (raw == null || raw.isEmpty() || raw.equals("--")) return "--";
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
            Date d = parser.parse(raw);
            SimpleDateFormat dateOnly = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.ENGLISH);
            return dateOnly.format(d);
        } catch (Exception e) { return raw; }
    }

    private void showIneligibilityAlert(long daysRemaining, Date lastDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(lastDate);
        c.add(Calendar.DAY_OF_YEAR, 120);
        String nextDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(c.getTime());
        new AlertDialog.Builder(this)
                .setTitle("⚠️ تنبيه")
                .setMessage("باقي " + daysRemaining + " يوم لتتمكن من التبرع مجدداً.\nتاريخك القادم: " + nextDate)
                .setPositiveButton("حسناً", null).show();
    }

    private String normalizeNumbers(String input) {
        if (input == null) return "";
        return input.replace("٠","0").replace("١","1").replace("٢","2").replace("٣","3")
                .replace("٤","4").replace("٥","5").replace("٦","6").replace("٧","7")
                .replace("٨","8").replace("٩","9").replace("-","/");
    }
    // ... نهاية الدوال الأخرى ...
    private boolean isTestExpired(String lastBloodTest) {
        if (lastBloodTest == null || lastBloodTest.equals("--") || lastBloodTest.isEmpty()) return true;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date lastDate = sdf.parse(normalizeNumbers(lastBloodTest));
            long diff = new Date().getTime() - lastDate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            return days >= 120;
        } catch (Exception e) {
            return true;
        }
    }
    // ضعيها هنا
    private boolean isTooSoonToDonate(String lastDonationDate) {
        if (lastDonationDate == null || lastDonationDate.equals("--") || lastDonationDate.isEmpty()) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date lastDate = sdf.parse(normalizeNumbers(lastDonationDate));
            long diff = new Date().getTime() - lastDate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            return days < 120;
        } catch (Exception e) {
            return false;
        }
    }

}
