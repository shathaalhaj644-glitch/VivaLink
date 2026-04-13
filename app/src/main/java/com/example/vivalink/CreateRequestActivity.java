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


        tvPageTitle = findViewById(R.id.tvPageTitle);
        et_city = findViewById(R.id.et_city);
        et_hospitalName = findViewById(R.id.et_hospitalName);
        et_units = findViewById(R.id.et_units);
        et_department = findViewById(R.id.et_department);
        sp_bloodType = findViewById(R.id.sp_bloodType);
        btn_create_request = findViewById(R.id.btn_create_request);


        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bloodTypes);
        sp_bloodType.setAdapter(adapter);

        db = FirebaseDatabase.getInstance().getReference("Requests");


        fetchHospitalProfile();


        requestId = getIntent().getStringExtra("requestId");
        if (requestId != null) {
            tvPageTitle.setText("تعديل الطلب ✏️");
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

        if (TextUtils.isEmpty(unitsStr) || TextUtils.isEmpty(deptStr)) {
            Toast.makeText(this, "يرجى تعبئة كافة الحقول ⚠️", Toast.LENGTH_SHORT).show();
            return;
        }



        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.US);
        String fullDateTime = sdf.format(new Date());

        String hId = FirebaseAuth.getInstance().getUid();


        String combined = cityStr + "_" + blood;

        String currentId = (requestId != null) ? requestId : db.push().getKey();


        HospitalRequestModel request = new HospitalRequestModel(
                currentId,
                blood,
                "📍 " + cityStr,
                "🏥 " + hospStr,
                "💉 " + unitsStr,
                "مفتوح",
                "🏢 " + deptStr,
                fullDateTime,
                "",
                hId,
                hospitalPhone,
                combined
        );

        if (currentId != null) {
            db.child(currentId).setValue(request).addOnCompleteListener(task -> {
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