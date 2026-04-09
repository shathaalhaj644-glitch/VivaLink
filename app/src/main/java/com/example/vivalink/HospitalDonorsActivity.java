package com.example.vivalink;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HospitalDonorsActivity extends AppCompatActivity {

    private RecyclerView rvDonors;
    private HospitalDonorsAdapter adapter;
    private List<HospitalDonorsModel> list;
    private List<HospitalDonorsModel> filteredList;
    private TextView tvEmpty;
    private EditText etSearchDonor;
    private String currentCity = "";
    private String selectedBloodType = "الكل";

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

        adapter = new HospitalDonorsAdapter(this, filteredList, new HospitalDonorsAdapter.OnDonorClickListener() {
            @Override
            public void onDonorClick(HospitalDonorsModel donor) {
                showMedicalUpdateDialog(donor);
            }
        });

        rvDonors.setAdapter(adapter);
    }

    private void showMedicalUpdateDialog(HospitalDonorsModel donor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_donor_medical_update, null);

        TextView tvName = view.findViewById(R.id.tvDialogDonorName);
        RadioButton rbFit = view.findViewById(R.id.rbFit);
        RadioButton rbUnfit = view.findViewById(R.id.rbUnfit);
        EditText etNote = view.findViewById(R.id.etNote);
        Button btnSave = view.findViewById(R.id.btnSave);

        if (tvName != null) tvName.setText("تحديث حالة: " + donor.getFullName());
        if (etNote != null) etNote.setText(donor.getHospitalNote());

        AlertDialog dialog = builder.setView(view).create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnSave.setOnClickListener(v -> {
            String status = "";
            if (rbFit != null && rbFit.isChecked()) {
                status = "لائق طبياً";
            } else if (rbUnfit != null && rbUnfit.isChecked()) {
                status = "غير لائق";
            }

            if (status.isEmpty()) {
                Toast.makeText(this, "يرجى اختيار الحالة الصحية", Toast.LENGTH_SHORT).show();
                return;
            }

            if (donor.getId() == null) {
                Toast.makeText(this, "خطأ في معرف المتبرع", Toast.LENGTH_SHORT).show();
                return;
            }

            String note = etNote.getText().toString().trim();

            // --- التعديل الجوهري هنا ---
            // جلب تاريخ اليوم بصيغة (السنة-الشهر-اليوم) لتجديد صلاحية الـ 4 أشهر
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            HashMap<String, Object> updateMap = new HashMap<>();
            updateMap.put("isVerifiedByHospital", true);
            updateMap.put("officialStatus", status);
            updateMap.put("hospitalNote", note);
            updateMap.put("lastBloodTest", todayDate); // تحديث تاريخ الفحص لتصفير عداد المتبرع

            FirebaseDatabase.getInstance().getReference("Donors")
                    .child(donor.getId())
                    .updateChildren(updateMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "تم توثيق الفحص وتجديد الصلاحية ✅", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "خطأ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        dialog.show();
    }

    private void setupSearch() {
        etSearchDonor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupBloodFilterButtons() {
        int[] buttonIds = {R.id.btnAll, R.id.btnAPlus, R.id.btnAMinus, R.id.btnBPlus, R.id.btnBMinus,
                R.id.btnOPlus, R.id.btnOMinus, R.id.btnABPlus, R.id.btnABMinus};

        for (int id : buttonIds) {
            Button btn = findViewById(id);
            if (btn != null) {
                btn.setOnClickListener(v -> {
                    selectedBloodType = btn.getText().toString();
                    updateButtonColors(buttonIds, id);
                    applyFilters();
                });
            }
        }
    }

    private void updateButtonColors(int[] ids, int selectedId) {
        for (int id : ids) {
            Button btn = findViewById(id);
            if (btn != null) {
                if (id == selectedId) {
                    btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFD32F2F));
                    btn.setTextColor(0xFFFFFFFF);
                } else {
                    btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFF5F5F5));
                    btn.setTextColor(0xFFD32F2F);
                }
            }
        }
    }

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
                            if (donor != null) {
                                donor.setId(ds.getKey());
                                list.add(donor);
                            }
                        }
                        applyFilters();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}