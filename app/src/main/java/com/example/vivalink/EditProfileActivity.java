package com.example.vivalink;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private TextView tvBloodTypeDisplay;
    private EditText etName, etEmail, etPhone, etCity, etBloodPercentage, etLastBloodTest;
    private RadioGroup rgHealth;
    private RadioButton rbYes, rbNo;
    private Button btnSave;

    private DatabaseReference userRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        tvBloodTypeDisplay = findViewById(R.id.tvBloodTypeDisplay);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etCity = findViewById(R.id.etCity);
        etBloodPercentage = findViewById(R.id.etBloodPercentage);
        etLastBloodTest = findViewById(R.id.etLastBloodTest);
        rgHealth = findViewById(R.id.rgHealth);
        rbYes = findViewById(R.id.rbYes);
        rbNo = findViewById(R.id.rbNo);
        btnSave = findViewById(R.id.btnSave);

        uid = FirebaseAuth.getInstance().getUid();

        if (uid != null) {

            userRef = FirebaseDatabase.getInstance()
                    .getReference("Donors")
                    .child(uid);

            loadData();
        }

        btnSave.setOnClickListener(v -> saveUpdates());
    }

    private void loadData() {

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) return;


                String blood = snapshot.child("bloodType").getValue(String.class);
                String name = snapshot.child("fullName").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                String city = snapshot.child("city").getValue(String.class);
                String lastTest = snapshot.child("lastBloodTest").getValue(String.class);

                Object perc = snapshot.child("bloodPercentage").getValue();

                String disease = snapshot.child("chronicDisease").getValue(String.class);


                tvBloodTypeDisplay.setText("فصيلة الدم: " + (blood != null ? blood : "--"));

                etName.setText(name != null ? name : "");
                etEmail.setText(email != null ? email : "");
                etPhone.setText(phone != null ? phone : "");
                etCity.setText(city != null ? city : "");
                etLastBloodTest.setText(lastTest != null ? lastTest : "");

                etBloodPercentage.setText(perc != null ? String.valueOf(perc) : "");

                if ("نعم".equals(disease)) {
                    rbYes.setChecked(true);
                } else {
                    rbNo.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void saveUpdates() {

        HashMap<String, Object> map = new HashMap<>();

        map.put("fullName", etName.getText().toString().trim());
        map.put("email", etEmail.getText().toString().trim());
        map.put("phone", etPhone.getText().toString().trim());
        map.put("city", etCity.getText().toString().trim());
        map.put("bloodPercentage", etBloodPercentage.getText().toString().trim());
        map.put("lastBloodTest", etLastBloodTest.getText().toString().trim());
        map.put("chronicDisease", rbYes.isChecked() ? "نعم" : "لا");

        userRef.updateChildren(map).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Toast.makeText(this, "تم الحفظ", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "فشل الحفظ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}