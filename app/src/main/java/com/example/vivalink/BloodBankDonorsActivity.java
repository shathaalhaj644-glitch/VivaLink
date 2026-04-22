package com.example.vivalink;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class BloodBankDonorsActivity extends AppCompatActivity {

    private RecyclerView rvDonorsList;
    private BloodBankDonorsAdapter adapter;
    private List<BloodBankDonorsModel> allDonors = new ArrayList<>();
    private List<BloodBankDonorsModel> filteredList = new ArrayList<>();
    private String selectedBloodType = "الكل", staffCity = "";
    private TabLayout tabLayout;
    private EditText etSearchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbank_donors);

        // ربط العناصر
        rvDonorsList = findViewById(R.id.rvDonorsList);
        tabLayout = findViewById(R.id.tabLayout);
        etSearchName = findViewById(R.id.etSearchName);

        // إعداد التابات
        tabLayout.addTab(tabLayout.newTab().setText("المتبرعون"));
        tabLayout.addTab(tabLayout.newTab().setText("الواصلون"));
        tabLayout.addTab(tabLayout.newTab().setText("الفحوصات"));
        tabLayout.addTab(tabLayout.newTab().setText("السجل"));

        // إعداد الـ RecyclerView
        rvDonorsList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BloodBankDonorsAdapter(filteredList, new BloodBankDonorsAdapter.OnDonorActionListener() {
            @Override public void onRegisterDonation(BloodBankDonorsModel d) { registerDonation(d); }
            @Override public void onAddNote(BloodBankDonorsModel d) { showNoteDialog(d); }
        });
        rvDonorsList.setAdapter(adapter);

        // جلب البيانات
        getStaffCityAndLoadDonors();
        setupListeners();
    }

    private void getStaffCityAndLoadDonors() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance().getReference("BloodBankStaff").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {
                        staffCity = s.child("city").getValue(String.class);
                        loadDonors();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });
    }

    private void loadDonors() {
        FirebaseDatabase.getInstance().getReference("Donors")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allDonors.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            BloodBankDonorsModel d = ds.getValue(BloodBankDonorsModel.class);
                            if (d != null) {
                                d.setUid(ds.getKey());
                                // الفلترة الأساسية: فقط المتبرعين من نفس مدينة الموظف
                                if (staffCity != null && d.getCity() != null && d.getCity().equals(staffCity)) {
                                    allDonors.add(d);
                                }
                            }
                        }
                        filter();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });
    }

    private void setupListeners() {
        // البحث بالاسم
        etSearchName.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filter(); }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        // المستمع الخاص بالتابات
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                rvDonorsList.setVisibility(tab.getPosition() == 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // أزرار الفلترة
        findViewById(R.id.btnToday).setOnClickListener(v -> { selectedBloodType = "اليوم"; filter(); });
        findViewById(R.id.btnAll).setOnClickListener(v -> { selectedBloodType = "الكل"; filter(); });
        findViewById(R.id.btnAPlus).setOnClickListener(v -> { selectedBloodType = "A+"; filter(); });
        findViewById(R.id.btnAMinus).setOnClickListener(v -> { selectedBloodType = "A-"; filter(); });
        findViewById(R.id.btnBPlus).setOnClickListener(v -> { selectedBloodType = "B+"; filter(); });
        findViewById(R.id.btnBMinus).setOnClickListener(v -> { selectedBloodType = "B-"; filter(); });
        findViewById(R.id.btnOPlus).setOnClickListener(v -> { selectedBloodType = "O+"; filter(); });
        findViewById(R.id.btnOMinus).setOnClickListener(v -> { selectedBloodType = "O-"; filter(); });
        findViewById(R.id.btnABPlus).setOnClickListener(v -> { selectedBloodType = "AB+"; filter(); });
        findViewById(R.id.btnABMinus).setOnClickListener(v -> { selectedBloodType = "AB-"; filter(); });
    }

    private void filter() {
        String query = etSearchName.getText().toString().toLowerCase().trim();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

        filteredList.clear();
        for (BloodBankDonorsModel d : allDonors) {
            boolean matchesName = d.getDisplayName().toLowerCase().contains(query);
            boolean matchesFilter = false;

            if (selectedBloodType.equals("الكل")) {
                matchesFilter = true;
            } else if (selectedBloodType.equals("اليوم")) {
                matchesFilter = d.getLastDonation() != null && d.getLastDonation().equals(todayDate);
            } else {
                matchesFilter = d.getBloodType() != null && d.getBloodType().equals(selectedBloodType);
            }

            if (matchesName && matchesFilter) {
                filteredList.add(d);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void registerDonation(BloodBankDonorsModel d) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
        int currentCount = 0;
        try {
            currentCount = Integer.parseInt(d.getDonationCount());
        } catch (Exception e) {
            currentCount = 0;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Donors").child(d.getUid());
        Map<String, Object> updates = new HashMap<>();
        updates.put("lastDonation", today);
        updates.put("donationCount", String.valueOf(currentCount + 1));

        ref.updateChildren(updates).addOnSuccessListener(aVoid ->
                Toast.makeText(this, "تم تسجيل التبرع بنجاح", Toast.LENGTH_SHORT).show());
    }

    private void showNoteDialog(BloodBankDonorsModel d) {
        EditText etNote = new EditText(this);
        etNote.setText(d.getNote());
        new AlertDialog.Builder(this)
                .setTitle("إضافة ملاحظة لـ " + d.getDisplayName())
                .setView(etNote)
                .setPositiveButton("حفظ", (dialog, which) -> {
                    String noteText = etNote.getText().toString();
                    FirebaseDatabase.getInstance().getReference("Donors").child(d.getUid())
                            .child("note").setValue(noteText);
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }
}