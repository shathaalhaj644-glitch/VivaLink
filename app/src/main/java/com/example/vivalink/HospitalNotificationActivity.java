package com.example.vivalink;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;

public class HospitalNotificationActivity extends AppCompatActivity {

    private RecyclerView rv;
    private BloodBankNotificationAdapter adapter;

    // التعديل الجوهري: تغيير نوع القائمة ليتوافق مع الأدابتر الجديد
    private List<Notifications> list = new ArrayList<>();
    private String currentHospitalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_notification);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentHospitalId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        rv = findViewById(R.id.rvNotifications);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // الآن التوافق تام: القائمة Notifications والأدابتر يتوقع Notifications
        adapter = new BloodBankNotificationAdapter(list);
        rv.setAdapter(adapter);

        if (currentHospitalId != null) {
            loadHospitalNotifications();
        } else {
            Toast.makeText(this, "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadHospitalNotifications() {
        FirebaseDatabase.getInstance().getReference("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            // التعديل: جلب البيانات باستخدام كلاس Notifications الموحد
                            Notifications n = ds.getValue(Notifications.class);

                            if (n != null) {
                                // ملاحظة: تأكدي أن كلاس Notifications يحتوي على الدوال getTargetType و getUserId
                                // إذا لم يكن لديه getTargetType، استخدمي n.getUserId() فقط للفلترة

                                String targetUserId = n.getUserId(); // في كلاسنا الموحد استخدمنا userId

                                if (currentHospitalId.equals(targetUserId)) {
                                    list.add(0, n);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HospitalNotificationActivity.this, "فشل تحميل الإشعارات", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}