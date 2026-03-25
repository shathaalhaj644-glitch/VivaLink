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

    // المعلومات الشخصية
    private TextView profile_name, profile_email, profile_phone, profile_city;
    // معلومات التبرع
    private TextView last_donation_date, donation_count;
    // الإعدادات
    private TextView edit_profile_button, change_password_button, notification_settings_button;

    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // ربط العناصر بالـ IDs من XML
        profile_name = findViewById(R.id.profile_name);
        profile_email = findViewById(R.id.profile_email);
        profile_phone = findViewById(R.id.profile_phone);
        profile_city = findViewById(R.id.profile_city);
        last_donation_date = findViewById(R.id.last_donation_date);
        donation_count = findViewById(R.id.donation_count);
        edit_profile_button = findViewById(R.id.edit_profile_button);
        change_password_button = findViewById(R.id.change_password_button);
        notification_settings_button = findViewById(R.id.notification_settings_button);

        // جلب بيانات المستخدم الحالي من Firebase
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Donors").child(uid);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        profile_name.setText("الاسم الكامل: " + snapshot.child("fullName").getValue(String.class));
                        profile_email.setText("البريد الإلكتروني: " + snapshot.child("email").getValue(String.class));
                        profile_phone.setText("رقم الهاتف: " + snapshot.child("phone").getValue(String.class));
                        profile_city.setText("المدينة: " + snapshot.child("city").getValue(String.class));

                        String lastDonation = snapshot.child("lastDonation").getValue(String.class);
                        Long count = snapshot.child("donationCount").getValue(Long.class);

                        last_donation_date.setText("آخر تبرع: " + (lastDonation != null ? lastDonation : "لا يوجد"));
                        donation_count.setText("عدد التبرعات: " + (count != null ? String.valueOf(count) : "0"));
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        // أوامر الانتقال
        edit_profile_button.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));
        change_password_button.setOnClickListener(v -> startActivity(new Intent(this, ChangePasswardActivity.class)));
        notification_settings_button.setOnClickListener(v ->
                Toast.makeText(this, "قريباً إعدادات الإشعارات", Toast.LENGTH_SHORT).show()
        );
    }
}
