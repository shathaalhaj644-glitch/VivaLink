package com.example.vivalink;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private TextView tvBloodType;
    private EditText etName, etEmail, etPhone, etCity;
    private RadioGroup rgHealth;
    private RadioButton rbYes, rbNo;
    private TextView tvDonationHistory;
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
        tvDonationHistory = findViewById(R.id.tvDonationHistory);
        btnSave = findViewById(R.id.btnSave);

        uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Donors").child(uid);
            loadUserData();
        }

        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tvBloodType.setText("فصيلة الدم: " + snapshot.child("bloodType").getValue(String.class) + " (لا يمكن تغييرها)");
                    etName.setText(snapshot.child("name").getValue(String.class));
                    etEmail.setText(snapshot.child("email").getValue(String.class));
                    etPhone.setText(snapshot.child("phone").getValue(String.class));
                    etCity.setText(snapshot.child("city").getValue(String.class));

                    String health = snapshot.child("chronicDisease").getValue(String.class);
                    if ("نعم".equals(health)) rbYes.setChecked(true);
                    else rbNo.setChecked(true);

                    String lastDonation = snapshot.child("lastDonation").getValue(String.class);
                    if (lastDonation != null && !lastDonation.isEmpty()) {
                        tvDonationHistory.setText("تاريخ آخر تبرع: " + lastDonation);
                    } else {
                        tvDonationHistory.setText("لم أقم بالتبرع من قبل");
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void saveChanges() {
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("name", etName.getText().toString().trim());
        updates.put("email", etEmail.getText().toString().trim());
        updates.put("phone", etPhone.getText().toString().trim());
        updates.put("city", etCity.getText().toString().trim());

        int selectedId = rgHealth.getCheckedRadioButtonId();
        if (selectedId == R.id.rbYes) updates.put("chronicDisease", "نعم");
        else updates.put("chronicDisease", "لا");

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
