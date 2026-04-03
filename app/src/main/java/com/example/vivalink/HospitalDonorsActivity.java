package com.example.vivalink;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private List<HospitalDonorsModel> filteredList;
    private TextView tvEmpty;
    private EditText etSearchDonor;
    private String currentCity = "";
    private String selectedBloodType = "الكل"; // لحفظ الفصيلة المختارة حالياً

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_donors);

        initViews();
        setupSearch();
        setupBloodFilterButtons();
        getHospitalCity();
    }

    private void initViews() {
        rvDonors = findViewById(R.id.rvDonors);
        tvEmpty = findViewById(R.id.tvEmptyMessage);
        etSearchDonor = findViewById(R.id.etSearchDonor);
        rvDonors.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new HospitalDonorsAdapter(this, filteredList);
        rvDonors.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearchDonor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters(); // فلترة بناءً على الاسم والفصيلة معاً
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // دالة لربط أزرار الفصائل وتلوينها
    private void setupBloodFilterButtons() {
        int[] buttonIds = {R.id.btnAll, R.id.btnAPlus, R.id.btnAMinus, R.id.btnBPlus, R.id.btnBMinus,
                R.id.btnOPlus, R.id.btnOMinus, R.id.btnABPlus, R.id.btnABMinus};

        for (int id : buttonIds) {
            Button btn = findViewById(id);
            btn.setOnClickListener(v -> {
                selectedBloodType = btn.getText().toString();
                updateButtonColors(buttonIds, id);
                applyFilters();
            });
        }
    }

    // لتغيير لون الزر المختار للأحمر والباقي للسكني الفاتح
    private void updateButtonColors(int[] ids, int selectedId) {
        for (int id : ids) {
            Button btn = findViewById(id);
            if (id == selectedId) {
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFD32F2F)); // أحمر
                btn.setTextColor(0xFFFFFFFF); // أبيض
            } else {
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFF5F5F5)); // سكني فاتح
                btn.setTextColor(0xFFD32F2F); // نص أحمر
            }
        }
    }

    // الدالة السحرية: تفلتر بناءً على الاسم المكتوب + الفصيلة المختارة
    private void applyFilters() {
        String searchText = etSearchDonor.getText().toString().toLowerCase();
        filteredList.clear();

        for (HospitalDonorsModel donor : list) {
            boolean matchesName = donor.getFullName().toLowerCase().contains(searchText);
            boolean matchesBlood = selectedBloodType.equals("الكل") || donor.getBloodType().equals(selectedBloodType);

            if (matchesName && matchesBlood) {
                filteredList.add(donor);
            }
        }

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
        if (hospitalId == null) return;

        FirebaseDatabase.getInstance().getReference("Hospitals").child(hospitalId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            currentCity = snapshot.child("city").getValue(String.class);
                            loadDonors();
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadDonors() {
        FirebaseDatabase.getInstance().getReference("Donors")
                .orderByChild("city").equalTo(currentCity)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            HospitalDonorsModel donor = ds.getValue(HospitalDonorsModel.class);
                            if (donor != null) list.add(donor);
                        }
                        applyFilters();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}