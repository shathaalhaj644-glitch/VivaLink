package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class HospitalSettingsActivity extends AppCompatActivity {

    private TextView tvHospitalNameTop, tvHospitalCityTop, tvHospitalEmailTop;
    private TextView tvDetailsName, tvDetailsCity, tvDetailsEmail;
    private Button btnEditProfile, btnChangePassword, btnLogout;

    private DatabaseReference dbRef;
    private String hospitalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_settings);


        tvHospitalNameTop = findViewById(R.id.tvHospitalNameTop);
        tvHospitalCityTop = findViewById(R.id.tvHospitalCityTop);
        tvHospitalEmailTop = findViewById(R.id.tvHospitalEmailTop);

        tvDetailsName = findViewById(R.id.tvDetailsName);
        tvDetailsCity = findViewById(R.id.tvDetailsCity);
        tvDetailsEmail = findViewById(R.id.tvDetailsEmail);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        hospitalId = FirebaseAuth.getInstance().getUid();

        if (hospitalId != null) {
            dbRef = FirebaseDatabase.getInstance().getReference("Hospitals").child(hospitalId);
            loadHospitalData();
        }


        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(this, HospitalEditProfileActivity.class))
        );


        btnChangePassword.setOnClickListener(v ->
                startActivity(new Intent(this, HospitalChangePassward.class))
        );


        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(HospitalSettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadHospitalData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    String name = snapshot.child("hospitalName").getValue(String.class);
                    String city = snapshot.child("city").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);


                    tvHospitalNameTop.setText(name != null ? name : "---");
                    tvHospitalCityTop.setText(city != null ? city : "---");
                    tvHospitalEmailTop.setText(email != null ? email : "---");


                    tvDetailsName.setText(name != null ? name : "---");
                    tvDetailsCity.setText(city != null ? city : "---");
                    tvDetailsEmail.setText(email != null ? email : "---");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}