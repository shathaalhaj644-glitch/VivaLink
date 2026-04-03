package com.example.vivalink;

import android.os.Bundle;
import android.text.Editable; // جديد
import android.text.TextWatcher; // جديد
import android.util.Log;
import android.view.View;
import android.widget.EditText; // جديد
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HospitalDonorsActivity extends AppCompatActivity {

    private RecyclerView rvDonors;
    private HospitalDonorsAdapter adapter;
    private List<HospitalDonorsModel> list;
    private List<HospitalDonorsModel> filteredList; // 🔥 قائمة للبحث
    private TextView tvEmpty;
    private EditText etSearchDonor; // 🔥 تعريف شريط البحث

    private String currentCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_donors);

        rvDonors = findViewById(R.id.rvDonors);
        tvEmpty = findViewById(R.id.tvEmptyMessage);
        etSearchDonor = findViewById(R.id.etSearchDonor); // 🔥 ربط شريط البحث

        rvDonors.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        filteredList = new ArrayList<>(); // 🔥 تهيئة قائمة البحث

        // 💡 نمرر filteredList للأدابتر عشان هي اللي رح تتغير وقت البحث
        adapter = new HospitalDonorsAdapter(this, filteredList);
        rvDonors.setAdapter(adapter);

        // 🔥 تفعيل البحث عند الكتابة
        etSearchDonor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString()); // استدعاء دالة الفلترة
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        getHospitalCity();
    }

    // 🔥 دالة البحث (الفلترة)
    private void filter(String text) {
        filteredList.clear();
        for (HospitalDonorsModel item : list) {
            // نبحث بالاسم ونحول الحروف لصغيرة عشان دقة البحث
            if (item.getFullName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        // تحديث حالة القائمة الفارغة
        if (filteredList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvDonors.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvDonors.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

    private void getHospitalCity() {
        String hospitalId = FirebaseAuth.getInstance().getUid();
        if (hospitalId == null) { finish(); return; }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Hospitals").child(hospitalId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            currentCity = snapshot.child("city").getValue(String.class);
                            loadDonors();
                        } else {
                            Log.e("Donors", "المستشفى غير موجود");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadDonors() {
        if (currentCity == null || currentCity.isEmpty()) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Donors");
        Query query = ref.orderByChild("city").equalTo(currentCity);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    HospitalDonorsModel donor = ds.getValue(HospitalDonorsModel.class);
                    if (donor != null) {
                        list.add(donor);
                    }
                }

                // 🔥 عند جلب البيانات لأول مرة، نعرضها كلها في القائمة المفلترة
                filter(etSearchDonor.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Donors", "خطأ: " + error.getMessage());
            }
        });
    }
}