package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BloodBankSettingsActivity extends AppCompatActivity {

    private TextView tv_profile_name, tv_profile_hosp, tv_profile_city;
    private TextView tv_detail_name, tv_detail_email, tv_detail_phone, tv_detail_hospital, tv_detail_city;
    private Button btn_change_password, btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbank_settings);

        tv_profile_name = findViewById(R.id.tv_profile_name);
        tv_profile_hosp = findViewById(R.id.tv_profile_hosp);
        tv_profile_city = findViewById(R.id.tv_profile_city);

        tv_detail_name = findViewById(R.id.tv_detail_name);
        tv_detail_email = findViewById(R.id.tv_detail_email);
        tv_detail_phone = findViewById(R.id.tv_detail_phone);
        tv_detail_hospital = findViewById(R.id.tv_detail_hospital);
        tv_detail_city = findViewById(R.id.tv_detail_city);

        btn_change_password = findViewById(R.id.btn_change_password);
        btn_logout = findViewById(R.id.btn_logout);

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("BloodBankStaff").child(uid);
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String phone = String.valueOf(snapshot.child("phone").getValue());
                        String hosp = snapshot.child("hospitalName").getValue(String.class);
                        String city = snapshot.child("city").getValue(String.class);

                        tv_profile_name.setText(name);
                        tv_profile_hosp.setText(hosp);
                        tv_profile_city.setText(city);

                        tv_detail_name.setText(name);
                        tv_detail_email.setText(email);
                        tv_detail_phone.setText(phone);
                        tv_detail_hospital.setText(hosp);
                        tv_detail_city.setText(city);
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        btn_change_password.setOnClickListener(v -> {
            startActivity(new Intent(this, BloodBankChangePassward.class));
        });

        btn_logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(BloodBankSettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}