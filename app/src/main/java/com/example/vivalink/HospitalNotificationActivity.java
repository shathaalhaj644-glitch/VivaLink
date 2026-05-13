package com.example.vivalink;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class HospitalNotificationActivity extends AppCompatActivity {

    private RecyclerView rv;
    private BloodBankNotificationAdapter adapter;
    private List<BloodBankNotificationModel> list = new ArrayList<>();
    private String currentHospitalId;
    private TextView tvNoNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_notification);

        // ربط العناصر بالواجهة
        rv = findViewById(R.id.rvNotifications);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);

        rv.setLayoutManager(new LinearLayoutManager(this));

        // إعداد الأدابتر
        adapter = new BloodBankNotificationAdapter(list);
        rv.setAdapter(adapter);

        // الحصول على معرف المستشفى الحالي
        currentHospitalId = FirebaseAuth.getInstance().getUid();

        if (currentHospitalId != null) {
            loadHospitalNotifications();
        }
    }

    private void loadHospitalNotifications() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications");

        // استماع للتغييرات في جدول الإشعارات
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (currentHospitalId == null) return;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        BloodBankNotificationModel n = ds.getValue(BloodBankNotificationModel.class);
                        if (n == null) continue;

                        String type = n.getType(); // نوع الإشعار (طلب، قبول، رفض)
                        String targetType = n.getTargetType(); // الفئة المستهدفة (HOSPITAL, ADMIN)
                        String targetId = n.getTargetUserId(); // معرف الجهة المستهدفة

                        // --- منطق الفلترة لضمان وصول الإشعارات الصحيحة للمستشفى ---

                        // الشرط الأول: يجب أن يكون الإشعار موجهاً لهذا المستشفى تحديداً
                        if (currentHospitalId.equals(targetId)) {

                            // الشرط الثاني: فحص أنواع الإشعارات التي تهم إدارة المستشفى
                            boolean isTransferAction = "blood_transfer_request".equals(type) ||
                                    "blood_transfer_approved".equals(type) ||
                                    "blood_transfer_rejected".equals(type);

                            boolean isAdminNotif = "ADMIN".equals(targetType);

                            if (isTransferAction || isAdminNotif) {
                                // إضافة الإشعار في بداية القائمة (الأحدث أولاً)
                                if (!list.contains(n)) {
                                    list.add(0, n);
                                }
                            }
                        }

                    } catch (Exception e) {
                        Log.e("VivaLink", "Error parsing notification: " + e.getMessage());
                    }
                }

                // تحديث الواجهة
                adapter.notifyDataSetChanged();

                // إظهار رسالة "لا توجد إشعارات" إذا كانت القائمة فارغة
                if (tvNoNotifications != null) {
                    tvNoNotifications.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("VivaLink", "Database error: " + error.getMessage());
            }
        });
    }
}