package com.example.vivalink;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Locale;

public class RequestsDetailsActivity extends AppCompatActivity {

    private TextView tvBlood, tvHospital, tvCity, tvDept, tvUnits, tvDate, tvTimer, btnBack;
    private CardView cardTimer, cardDonatedSuccess;
    private String requestId, hospitalId, donorName;
    private int minutesToArrive;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        initViews();
        getDataAndDisplay();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        tvBlood = findViewById(R.id.tvDetBlood);
        tvHospital = findViewById(R.id.tvDetHospital);
        tvCity = findViewById(R.id.tvDetCity);
        tvDept = findViewById(R.id.tvDetDept);
        tvUnits = findViewById(R.id.tvDetUnits);
        tvDate = findViewById(R.id.tvDetDate);
        tvTimer = findViewById(R.id.tvTimer);
        btnBack = findViewById(R.id.btnBack);

        cardTimer = findViewById(R.id.cardTimer);
        cardDonatedSuccess = findViewById(R.id.cardDonatedSuccess);
    }

    private void getDataAndDisplay() {

        requestId = getIntent().getStringExtra("requestId");
        hospitalId = getIntent().getStringExtra("hospitalId");
        donorName = getIntent().getStringExtra("donorName");
        minutesToArrive = getIntent().getIntExtra("minutes", 0);
        boolean alreadyDonated = getIntent().getBooleanExtra("isDonated", false);


        String bloodType = getIntent().getStringExtra("bloodType");
        String hospitalName = getIntent().getStringExtra("hospitalName");
        String city = getIntent().getStringExtra("city");
        String department = getIntent().getStringExtra("department");
        String units = getIntent().getStringExtra("units");
        String confirmedAt = getIntent().getStringExtra("confirmedAt");


        tvBlood.setText("🩸 فصيلة الدم: " + (bloodType != null ? bloodType : "غير محدد"));
        tvHospital.setText("🏥 المستشفى: " + (hospitalName != null ? hospitalName : "غير محدد"));
        tvCity.setText("📍 المدينة: " + (city != null ? city : "غير محدد"));
        tvDept.setText("🏢 القسم: " + (department != null ? department : "غير محدد"));
        tvUnits.setText("🧪 الوحدات: " + (units != null ? units : "0"));
        tvDate.setText("📅 تاريخ الطلب: " + (confirmedAt != null ? confirmedAt : "---"));


        if (alreadyDonated) {
            showSuccessStatus();
        } else if (minutesToArrive > 0) {
            cardTimer.setVisibility(View.VISIBLE);
            cardDonatedSuccess.setVisibility(View.GONE);
            startCountdown(minutesToArrive);
        } else {
            cardTimer.setVisibility(View.GONE);
            cardDonatedSuccess.setVisibility(View.GONE);
        }
    }

    private void startCountdown(int minutes) {
        if (countDownTimer != null) countDownTimer.cancel();

        countDownTimer = new CountDownTimer(minutes * 60000L, 1000) {
            public void onTick(long millisUntilFinished) {
                long m = (millisUntilFinished / 1000) / 60;
                long s = (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format(Locale.ENGLISH, "الوقت المتبقي للوصول: %02d:%02d", m, s));
            }

            public void onFinish() {
                tvTimer.setText("انتهى الوقت! هل وصلت؟");
                showArrivalDialog();
            }
        }.start();
    }

    private void showArrivalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تأكيد التبرع")
                .setMessage("هل وصلت للمستشفى وتمت عملية التبرع بنجاح؟")
                .setCancelable(false)
                .setPositiveButton("نعم، تم التبرع", (dialog, which) -> markAsDonated())
                .setNegativeButton("ليس بعد", (dialog, which) -> {
                    Toast.makeText(this, "يرجى الإسراع، الطلب ما زال بانتظارك", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
        builder.show();
    }

    private void markAsDonated() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null || requestId == null) return;

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();


        java.util.Map<String, Object> arrivalData = new java.util.HashMap<>();
        arrivalData.put("donorId", uid);
        arrivalData.put("requestId", requestId);
        arrivalData.put("hospitalId", hospitalId);
        arrivalData.put("status", "وصل المتبرع");
        arrivalData.put("displayName", donorName != null ? donorName : "متبرع");
        arrivalData.put("bloodType", getIntent().getStringExtra("bloodType"));


        db.child("IncomingDonations").child(uid).setValue(arrivalData)
                .addOnSuccessListener(aVoid -> {

                    db.child("Requests").child(requestId).child("currentStatus").setValue("تم التبرع");

                    showSuccessStatus();
                    Toast.makeText(this, "تم إرسال إشعار للمستشفى بوجودك. شكراً لك! ✅", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "خطأ في الاتصال: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showSuccessStatus() {
        if (countDownTimer != null) countDownTimer.cancel();
        cardTimer.setVisibility(View.GONE);
        cardDonatedSuccess.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}