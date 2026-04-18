package com.example.vivalink;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CreateRequestActivity extends AppCompatActivity {

    TextView et_city, et_hospitalName;
    EditText et_units, et_department;
    Spinner sp_bloodType;
    Button btn_create_request;
    TextView tvPageTitle;
    DatabaseReference db;
    String requestId = null;
    String hospitalPhone = "0590000000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        // 1. ربط العناصر بالـ XML
        tvPageTitle = findViewById(R.id.tvPageTitle);
        et_city = findViewById(R.id.et_city);
        et_hospitalName = findViewById(R.id.et_hospitalName);
        et_units = findViewById(R.id.et_units);
        et_department = findViewById(R.id.et_department);
        sp_bloodType = findViewById(R.id.sp_bloodType);
        btn_create_request = findViewById(R.id.btn_create_request);

        // 2. إعداد قائمة فصائل الدم
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bloodTypes);
        sp_bloodType.setAdapter(adapter);

        db = FirebaseDatabase.getInstance().getReference("Requests");

        // 3. جلب بيانات المستشفى (الاسم والمدينة) تلقائياً
        fetchHospitalProfile();

        // 4. فحص إذا كان "تعديل" لطلب قديم أو "إنشاء" جديد
        requestId = getIntent().getStringExtra("requestId");
        if (requestId != null) {
            tvPageTitle.setText("تعديل الطلب");
            btn_create_request.setText("تحديث الآن");
            et_units.setText(getIntent().getStringExtra("units"));
            et_department.setText(getIntent().getStringExtra("dept"));
        }

        btn_create_request.setOnClickListener(v -> saveRequest());
    }

    private void fetchHospitalProfile() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseDatabase.getInstance().getReference("Hospitals").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String name = snapshot.child("hospitalName").getValue(String.class);
                                String city = snapshot.child("city").getValue(String.class);
                                if (snapshot.hasChild("phone")) {
                                    hospitalPhone = snapshot.child("phone").getValue(String.class);
                                }
                                et_hospitalName.setText(name);
                                et_city.setText(city);
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }
    }

    private void saveRequest() {
        String blood = sp_bloodType.getSelectedItem().toString();
        String unitsStr = et_units.getText().toString().trim();
        String cityStr = et_city.getText().toString().trim();
        String hospStr = et_hospitalName.getText().toString().trim();
        String deptStr = et_department.getText().toString().trim();

        // فحص الحقول الفارغة
        if (TextUtils.isEmpty(unitsStr) || TextUtils.isEmpty(deptStr)) {
            Toast.makeText(this, "يرجى تعبئة كافة الحقول ⚠️", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. إنشاء وقت الطلب بصيغة ISO المتوافقة مع كود المتبرع
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
        String currentTime = sdf.format(new Date());

        String hId = FirebaseAuth.getInstance().getUid();

        // 2. المفتاح المركب (نص صافي للمقارنة الصحيحة في الفلترة)
        String combined = cityStr + "_" + blood;

        String currentId = (requestId != null) ? requestId : db.push().getKey();

        // 3. تخزين البيانات كنصوص صافية (تمت إزالة السمايلات 📍🏥💉🏢 من هنا)
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("requestId", currentId);
        requestMap.put("bloodType", blood);
        requestMap.put("city", cityStr);
        requestMap.put("hospitalName", hospStr);
        requestMap.put("units", unitsStr);
        requestMap.put("department", deptStr);
        requestMap.put("status", "مفتوح");
        requestMap.put("confirmedAt", currentTime); // الحقل الجديد للوقت
        requestMap.put("hospitalId", hId);
        requestMap.put("phone", hospitalPhone);
        requestMap.put("city_bloodType", combined); // حقل الفلترة الأساسي
        requestMap.put("donatedCount", 0);

        if (currentId != null) {
            db.child(currentId).setValue(requestMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "تم الحفظ وإبلاغ المتبرعين بنجاح ✅", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "حدث خطأ في الحفظ، حاول ثانية", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}