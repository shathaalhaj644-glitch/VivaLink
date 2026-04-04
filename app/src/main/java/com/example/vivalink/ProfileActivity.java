package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class ProfileActivity extends AppCompatActivity {

    private TextView profile_name, profile_email, profile_phone, profile_city;
    private TextView last_donation_date, donation_count;
    private TextView edit_profile_button, change_password_button, notification_settings_button;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Donors").child(uid);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = String.valueOf(snapshot.child("fullName").getValue());
                        String email = String.valueOf(snapshot.child("email").getValue());
                        String phone = String.valueOf(snapshot.child("phone").getValue());
                        String city = String.valueOf(snapshot.child("city").getValue());
                        String lastDonation = String.valueOf(snapshot.child("lastDonation").getValue());
                        String count = String.valueOf(snapshot.child("donationCount").getValue());

                        profile_name.setText("الاسم الكامل: " + (name.equals("null") ? "غير متوفر" : name));
                        profile_email.setText("البريد الإلكتروني: " + (email.equals("null") ? "غير متوفر" : email));
                        profile_phone.setText("رقم الهاتف: " + (phone.equals("null") ? "غير متوفر" : phone));
                        profile_city.setText("المدينة: " + (city.equals("null") ? "غير متوفر" : city));

                        last_donation_date.setText("آخر تبرع: " + (lastDonation.equals("null") ? "لا يوجد" : lastDonation));
                        donation_count.setText("عدد التبرعات: " + (count.equals("null") ? "0" : count));
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        // التنقل بين الصفحات
        edit_profile_button.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));
        change_password_button.setOnClickListener(v -> startActivity(new Intent(this, ChangePasswardActivity.class)));
        notification_settings_button.setOnClickListener(v ->
                Toast.makeText(this, "قريباً إعدادات الإشعارات", Toast.LENGTH_SHORT).show());
    }

    private void initViews() {
        profile_name = findViewById(R.id.profile_name);
        profile_email = findViewById(R.id.profile_email);
        profile_phone = findViewById(R.id.profile_phone);
        profile_city = findViewById(R.id.profile_city);
        last_donation_date = findViewById(R.id.last_donation_date);
        donation_count = findViewById(R.id.donation_count);
        edit_profile_button = findViewById(R.id.edit_profile_button);
        change_password_button = findViewById(R.id.change_password_button);
        notification_settings_button = findViewById(R.id.notification_settings_button);
    }
}
