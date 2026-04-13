package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvTopName, tvTopBlood, tvTopCity;
    private TextView tvProfileFullName, tvProfileEmail, tvProfilePhone;
    private TextView tvLastDonationDate, tvTotalDonations, tvLastTestDate;
    private TextView btnEditProfile, btnChangePass, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();

        String uid = FirebaseAuth.getInstance().getUid();

        if (uid != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("Donors").child(uid);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (!snapshot.exists()) return;

                    String name = getValue(snapshot, "fullName");
                    String email = getValue(snapshot, "email");
                    String phone = getValue(snapshot, "phone");
                    String blood = getValue(snapshot, "bloodType");
                    String city = getValue(snapshot, "city");

                    String lastDonation = getValue(snapshot, "lastDonation");
                    String lastTest = getValue(snapshot, "lastBloodTest");

                    String count = String.valueOf(snapshot.child("donationCount").getValue());

                    // ✅ بيانات أساسية
                    tvTopName.setText(name);
                    tvTopBlood.setText("🩸 فصيلة الدم: " + blood);
                    tvTopCity.setText("📍 " + city);

                    tvProfileFullName.setText(name);
                    tvProfileEmail.setText(email);
                    tvProfilePhone.setText(phone);

                    tvTotalDonations.setText(count);


                    tvLastDonationDate.setText(formatDate(lastDonation));
                    tvLastTestDate.setText(formatDate(lastTest));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));

        btnChangePass.setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswardActivity.class)));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void initViews() {
        tvTopName = findViewById(R.id.tvTopName);
        tvTopBlood = findViewById(R.id.tvTopBlood);
        tvTopCity = findViewById(R.id.tvTopCity);

        tvProfileFullName = findViewById(R.id.tvProfileFullName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);

        tvLastDonationDate = findViewById(R.id.tvLastDonationDate);
        tvTotalDonations = findViewById(R.id.tvTotalDonations);
        tvLastTestDate = findViewById(R.id.tvLastTestDate);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePass = findViewById(R.id.btnChangePass);
        btnLogout = findViewById(R.id.btnLogout);
    }


    private String getValue(DataSnapshot snapshot, String key) {
        Object value = snapshot.child(key).getValue();
        return value != null ? value.toString() : "--";
    }


    private String formatDate(String input) {

        if (input == null || input.equals("--") || input.isEmpty())
            return "--";

        try {
            Date date;


            if (input.contains("GMT")) {
                SimpleDateFormat f =
                        new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                date = f.parse(input);

            } else if (input.contains("-")) {
                // yyyy-MM-dd
                SimpleDateFormat f =
                        new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                date = f.parse(input);

            } else {
                return input;
            }


            SimpleDateFormat out =
                    new SimpleDateFormat("d/M/yyyy", Locale.ENGLISH);

            return out.format(date);

        } catch (Exception e) {
            return input;
        }
    }
}