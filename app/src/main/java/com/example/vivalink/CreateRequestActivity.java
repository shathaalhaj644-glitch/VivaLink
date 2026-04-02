package com.example.vivalink;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.Calendar;

public class CreateRequestActivity extends AppCompatActivity {

    EditText et_bloodType, et_city, et_hospitalName, et_units, et_status, et_department;
    Button btn_create_request;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        et_bloodType = findViewById(R.id.et_bloodType);
        et_city = findViewById(R.id.et_city);
        et_hospitalName = findViewById(R.id.et_hospitalName);
        et_units = findViewById(R.id.et_units);
        et_status = findViewById(R.id.et_status);
        et_department = findViewById(R.id.et_department);
        btn_create_request = findViewById(R.id.btn_create_request);

        db = FirebaseDatabase.getInstance().getReference("Requests");

        btn_create_request.setOnClickListener(v -> saveRequest());
    }

    private void saveRequest() {
        String blood = et_bloodType.getText().toString().trim();
        String unitsStr = et_units.getText().toString().trim();
        String city = et_city.getText().toString().trim();
        String hospitalName = et_hospitalName.getText().toString().trim();
        String status = et_status.getText().toString().trim();
        String department = et_department.getText().toString().trim();

        if (TextUtils.isEmpty(blood) || TextUtils.isEmpty(unitsStr)) {
            Toast.makeText(this, "يرجى إكمال البيانات الأساسية", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = db.push().getKey();
        String hId = FirebaseAuth.getInstance().getUid();
        String date = Calendar.getInstance().getTime().toString();

        // التعديل الجوهري هنا: مررنا unitsStr مباشرة كـ String
        // ولم نستخدم Integer.parseInt لتجنب الـ Incompatible types error
        HospitalRequestModel request = new HospitalRequestModel(
                id,
                blood,
                city,
                hospitalName,
                unitsStr, // تم التعديل هنا لتكون String
                status,
                department,
                date,
                hId
        );

        if (id != null) {
            db.child(id).setValue(request).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "تمت الإضافة بنجاح ✅", Toast.LENGTH_SHORT).show();
                    finish(); // العودة لصفحة قائمة الطلبات وتحديثها تلقائياً
                } else {
                    Toast.makeText(this, "فشل في الإضافة: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}