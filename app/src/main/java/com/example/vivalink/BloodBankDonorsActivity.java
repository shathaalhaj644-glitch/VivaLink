package com.example.vivalink;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private String currentTestFilter = "الكل";
    private TabLayout tabLayout;
    private EditText etSearchName;

    private LinearLayout layoutTestFilters;
    private View layoutBloodTypeFilters;

    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodbank_donors);

        dbRef = FirebaseDatabase.getInstance().getReference();
        initViews();
        setupRecyclerView();
        getStaffCityAndLoadDonors();
        setupListeners();
    }

    private void initViews() {
        rvDonorsList = findViewById(R.id.rvDonorsList);
        tabLayout = findViewById(R.id.tabLayout);
        etSearchName = findViewById(R.id.etSearchName);

        layoutTestFilters = findViewById(R.id.layoutTestFilters);
        layoutBloodTypeFilters = findViewById(R.id.layoutBloodTypeFilters);

        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("المتبرعون"));
        tabLayout.addTab(tabLayout.newTab().setText("الواصلون"));
        tabLayout.addTab(tabLayout.newTab().setText("الفحوصات"));
        tabLayout.addTab(tabLayout.newTab().setText("السجل"));
    }

    private void setupRecyclerView() {
        rvDonorsList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BloodBankDonorsAdapter(filteredList, new BloodBankDonorsAdapter.OnDonorActionListener() {
            @Override public void onRegisterDonation(BloodBankDonorsModel d) { registerDonation(d); }
            @Override public void onAddNote(BloodBankDonorsModel d) { showNoteDialog(d); }
            @Override public void onUpdateTestStatus(BloodBankDonorsModel d, String status) { updateTestStatusInDB(d, status); }

            // إضافة تنفيذ دالة الحذف هنا لإزالة الخطأ
            @Override
            public void onDeleteTest(BloodBankDonorsModel d) {
                confirmDeletion(d);
            }
        });
        rvDonorsList.setAdapter(adapter);
    }

    private void confirmDeletion(BloodBankDonorsModel d) {
        new AlertDialog.Builder(this)
                .setTitle("حذف الفحص")
                .setMessage("هل أنتِ متأكدة من حذف صورة وبيانات هذا الفحص نهائياً؟")
                .setPositiveButton("حذف", (dialog, which) -> {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("bloodTestProofUrl", ""); // مسح الصورة
                    updates.put("bloodTestStatus", "");    // تصفير الحالة

                    dbRef.child("Donors").child(d.getUid()).updateChildren(updates)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "تم حذف الفحص بنجاح", Toast.LENGTH_SHORT).show();
                                // لا داعي لاستدعاء loadDonors لأن المستمع (ValueEventListener) سيحدث البيانات تلقائياً
                            });
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void getStaffCityAndLoadDonors() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        dbRef.child("BloodBankStaff").child(uid)
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
        dbRef.child("Donors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allDonors.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    BloodBankDonorsModel d = ds.getValue(BloodBankDonorsModel.class);
                    if (d != null) {
                        d.setUid(ds.getKey());
                        if (staffCity != null && d.getCity() != null && d.getCity().equals(staffCity)) {
                            allDonors.add(d);
                        }
                    }
                }
                applyCurrentTabFilter();
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void setupListeners() {
        etSearchName.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyCurrentTabFilter(); }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                applyCurrentTabFilter();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        findViewById(R.id.btnAll).setOnClickListener(v -> { selectedBloodType = "الكل"; filterDonors(); });
        findViewById(R.id.btnAPlus).setOnClickListener(v -> { selectedBloodType = "A+"; filterDonors(); });
        findViewById(R.id.btnAMinus).setOnClickListener(v -> { selectedBloodType = "A-"; filterDonors(); });
        findViewById(R.id.btnBPlus).setOnClickListener(v -> { selectedBloodType = "B+"; filterDonors(); });
        findViewById(R.id.btnBMinus).setOnClickListener(v -> { selectedBloodType = "B-"; filterDonors(); });
        findViewById(R.id.btnOPlus).setOnClickListener(v -> { selectedBloodType = "O+"; filterDonors(); });
        findViewById(R.id.btnOMinus).setOnClickListener(v -> { selectedBloodType = "O-"; filterDonors(); });
        findViewById(R.id.btnABPlus).setOnClickListener(v -> { selectedBloodType = "AB+"; filterDonors(); });
        findViewById(R.id.btnABMinus).setOnClickListener(v -> { selectedBloodType = "AB-"; filterDonors(); });
        findViewById(R.id.btnToday).setOnClickListener(v -> { selectedBloodType = "اليوم"; filterDonors(); });

        findViewById(R.id.filterAllTests).setOnClickListener(v -> { currentTestFilter = "الكل"; filterTests(); });
        findViewById(R.id.filterPending).setOnClickListener(v -> { currentTestFilter = "معلق"; filterTests(); });
        findViewById(R.id.filterAccepted).setOnClickListener(v -> { currentTestFilter = "مقبول"; filterTests(); });
        findViewById(R.id.filterRejected).setOnClickListener(v -> { currentTestFilter = "مرفوض"; filterTests(); });
    }

    private void applyCurrentTabFilter() {
        int position = tabLayout.getSelectedTabPosition();
        if (position == 0) {
            rvDonorsList.setVisibility(View.VISIBLE);
            layoutBloodTypeFilters.setVisibility(View.VISIBLE);
            layoutTestFilters.setVisibility(View.GONE);
            filterDonors();
        } else if (position == 2) {
            rvDonorsList.setVisibility(View.VISIBLE);
            layoutBloodTypeFilters.setVisibility(View.GONE);
            layoutTestFilters.setVisibility(View.VISIBLE);
            filterTests();
        } else {
            rvDonorsList.setVisibility(View.GONE);
            layoutBloodTypeFilters.setVisibility(View.GONE);
            layoutTestFilters.setVisibility(View.GONE);
        }
    }

    private void filterDonors() {
        String query = etSearchName.getText().toString().toLowerCase().trim();
        String todayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date());
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

            if (matchesName && matchesFilter) filteredList.add(d);
        }
        adapter.notifyDataSetChanged();
    }

    private void filterTests() {
        String query = etSearchName.getText().toString().toLowerCase().trim();
        filteredList.clear();
        for (BloodBankDonorsModel d : allDonors) {
            if (d.getBloodTestProofUrl() != null && !d.getBloodTestProofUrl().isEmpty()) {
                boolean matchesName = d.getDisplayName().toLowerCase().contains(query);
                boolean matchesStatus = currentTestFilter.equals("الكل") || currentTestFilter.equals(d.getBloodTestStatus());
                if (matchesName && matchesStatus) filteredList.add(d);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void updateTestStatusInDB(BloodBankDonorsModel d, String status) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("bloodTestStatus", status);
        if ("مقبول".equals(status)) {
            updates.put("lastBloodTest", new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date()));
        }

        dbRef.child("Donors").child(d.getUid()).updateChildren(updates).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "تم التحديث بنجاح ✅", Toast.LENGTH_SHORT).show();
        });
    }

    private void registerDonation(BloodBankDonorsModel d) {
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date());
        int count = 0;
        try { count = Integer.parseInt(d.getDonationCount()); } catch (Exception e) {}

        Map<String, Object> updates = new HashMap<>();
        updates.put("lastDonation", today);
        updates.put("donationCount", String.valueOf(count + 1));

        dbRef.child("Donors").child(d.getUid()).updateChildren(updates).addOnSuccessListener(aVoid ->
                Toast.makeText(this, "تم تسجيل التبرع", Toast.LENGTH_SHORT).show());
    }

    private void showNoteDialog(BloodBankDonorsModel d) {
        EditText etNote = new EditText(this);
        etNote.setText(d.getNote());
        new AlertDialog.Builder(this).setTitle("ملاحظة").setView(etNote)
                .setPositiveButton("حفظ", (dialog, which) -> {
                    dbRef.child("Donors").child(d.getUid()).child("note").setValue(etNote.getText().toString());
                }).show();
    }
}