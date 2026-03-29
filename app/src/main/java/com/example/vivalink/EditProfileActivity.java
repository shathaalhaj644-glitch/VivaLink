package com.example.vivalink;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private TextView tvBloodType;
    private EditText etName, etEmail, etPhone, etCity, etLastDonation;
    private RadioGroup rgHealth;
    private RadioButton rbYes, rbNo;
    private CheckBox cbNeverDonated;
    private Button btnSave;

    private DatabaseReference userRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // ربط العناصر
        tvBloodType = findViewById(R.id.tvBloodType);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etCity = findViewById(R.id.etCity);
        rgHealth = findViewById(R.id.rgHealth);
        rbYes = findViewById(R.id.rbYes);
        rbNo = findViewById(R.id.rbNo);
        etLastDonation = findViewById(R.id.etLastDonation);
        cbNeverDonated = findViewById(R.id.cbNeverDonated);
        btnSave = findViewById(R.id.btnSave);

        uid = FirebaseAuth.getInstance().getUid();

        if (uid != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Donors").child(uid);
            loadUserData();
        }

        // التحكم بالـ checkbox
        cbNeverDonated.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etLastDonation.setText("");
                etLastDonation.setEnabled(false);
            } else {
                etLastDonation.setEnabled(true);
            }
        });

        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    tvBloodType.setText("فصيلة الدم: " +
                            snapshot.child("bloodType").getValue(String.class) +
                            " (لا يمكن تغييرها)");

                    etName.setText(snapshot.child("name").getValue(String.class));
                    etEmail.setText(snapshot.child("email").getValue(String.class));
                    etPhone.setText(snapshot.child("phone").getValue(String.class));
                    etCity.setText(snapshot.child("city").getValue(String.class));

                    String health = snapshot.child("chronicDisease").getValue(String.class);
                    if ("نعم".equals(health)) rbYes.setChecked(true);
                    else rbNo.setChecked(true);

                    String lastDonation = snapshot.child("lastDonation").getValue(String.class);

                    if (lastDonation != null && !lastDonation.isEmpty()) {
                        etLastDonation.setText(lastDonation);
                        cbNeverDonated.setChecked(false);
                    } else {
                        cbNeverDonated.setChecked(true);
                        etLastDonation.setText("");
                        etLastDonation.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void saveChanges() {
        HashMap<String, Object> updates = new HashMap<>();

        updates.put("name", etName.getText().toString().trim());
        updates.put("email", etEmail.getText().toString().trim());
        updates.put("phone", etPhone.getText().toString().trim());
        updates.put("city", etCity.getText().toString().trim());

        int selectedId = rgHealth.getCheckedRadioButtonId();
        if (selectedId == R.id.rbYes)
            updates.put("chronicDisease", "نعم");
        else
            updates.put("chronicDisease", "لا");

        // حفظ التبرع
        if (cbNeverDonated.isChecked()) {
            updates.put("lastDonation", "");
        } else {
            updates.put("lastDonation", etLastDonation.getText().toString().trim());
        }

        userRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "تم حفظ التعديلات", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "فشل الحفظ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}