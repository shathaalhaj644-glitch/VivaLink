package com.example.vivalink;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class BloodBankCreateRequestActivity extends AppCompatActivity {


    TextView et_city, et_hospitalName, tvPageTitle;
    EditText et_units, et_department;
    Spinner sp_bloodType;
    Button btn_create_request;
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
            if(tvPageTitle != null) tvPageTitle.setText("تعديل الطلب");
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
                                et_hospitalName.setText(name != null ? name : "");
                                et_city.setText(city != null ? city : "");
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


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm-yyyy\\MM\\dd", Locale.ENGLISH);
        String currentTime = sdf.format(new Date());

        String hId = FirebaseAuth.getInstance().getUid();
        String combined = cityStr + "_" + blood;
        String currentId = (requestId != null) ? requestId : db.push().getKey();

        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("requestId", currentId);
        requestMap.put("bloodType", blood);
        requestMap.put("city", cityStr);
        requestMap.put("hospitalName", hospStr);
        requestMap.put("units", unitsStr);
        requestMap.put("department", deptStr);
        requestMap.put("status", "مفتوح");
        requestMap.put("confirmedAt", currentTime);
        requestMap.put("hospitalId", hId);
        requestMap.put("phone", hospitalPhone);
        requestMap.put("city_bloodType", combined);
        requestMap.put("donatedCount", 0);

        if (currentId != null) {
            db.child(currentId).setValue(requestMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "تم الحفظ بنجاح ✅", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}