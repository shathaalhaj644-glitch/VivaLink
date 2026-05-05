package com.example.vivalink;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    private List<BloodBankDonorsModel> incomingDonors = new ArrayList<>();
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
        tabLayout.addTab(tabLayout.newTab().setText("قيد الوصول"));
        tabLayout.addTab(tabLayout.newTab().setText("الفحوصات"));
        tabLayout.addTab(tabLayout.newTab().setText("السجل"));
    }

    private void setupRecyclerView() {
        rvDonorsList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BloodBankDonorsAdapter(filteredList, new BloodBankDonorsAdapter.OnDonorActionListener() {
            @Override public void onRegisterDonation(BloodBankDonorsModel d) { registerDonation(d); }
            @Override public void onAddNote(BloodBankDonorsModel d) { showNoteDialog(d); }
            @Override public void onUpdateTestStatus(BloodBankDonorsModel d, String status) { updateTestStatusInDB(d, status); }
            @Override public void onDeleteTest(BloodBankDonorsModel d) { confirmDeletion(d); }
            @Override public void onConfirmArrival(BloodBankDonorsModel d) { confirmArrivalAndRegister(d); }
        });
        rvDonorsList.setAdapter(adapter);
    }

    private void getStaffCityAndLoadDonors() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;
        dbRef.child("BloodBankStaff").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                staffCity = s.child("city").getValue(String.class);
                loadDonors();
                loadIncomingDonors();
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

    private void loadIncomingDonors() {
        dbRef.child("IncomingDonations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                incomingDonors.clear();
                if (!snapshot.exists()) {
                    adapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot ds : snapshot.getChildren()) {
                    BloodBankDonorsModel incomingData = ds.getValue(BloodBankDonorsModel.class);
                    if (incomingData != null) {
                        String dId = ds.getKey();
                        incomingData.setDonorId(dId);
                        incomingData.setUid(dId);

                        dbRef.child("Donors").child(dId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot donorSnapshot) {
                                if (donorSnapshot.exists()) {
                                    BloodBankDonorsModel donorDetails = donorSnapshot.getValue(BloodBankDonorsModel.class);
                                    if (donorDetails != null) {
                                        incomingData.setFullName(donorDetails.getFullName());
                                        incomingData.setName(donorDetails.getName());
                                        incomingData.setBloodType(donorDetails.getBloodType());
                                        incomingData.setPhone(donorDetails.getPhone());
                                        incomingData.setCity(donorDetails.getCity());
                                        incomingData.setDonationCount(donorDetails.getDonationCount());
                                        incomingData.setLastDonation(donorDetails.getLastDonation());
                                    }
                                }

                                if ("قادم".equals(incomingData.getStatus())) {
                                    boolean exists = false;
                                    for(BloodBankDonorsModel m : incomingDonors) {
                                        if(m.getUid().equals(incomingData.getUid())) { exists = true; break; }
                                    }
                                    if(!exists) incomingDonors.add(incomingData);
                                }

                                if (tabLayout.getSelectedTabPosition() == 1) {
                                    applyCurrentTabFilter();
                                }
                            }
                            @Override public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    public void confirmArrivalAndRegister(BloodBankDonorsModel donor) {
        if (donor == null || donor.getDonorId() == null) {
            Toast.makeText(this, "بيانات المتبرع غير مكتملة", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String today = sdf.format(cal.getTime());
        cal.add(Calendar.MONTH, 4);
        String nextDate = sdf.format(cal.getTime());

        db.child("Donors").child(donor.getDonorId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;
                long count = 0;
                if (snapshot.hasChild("donationCount")) {
                    try { count = Long.parseLong(String.valueOf(snapshot.child("donationCount").getValue()));
                    } catch (Exception e) { count = 0; }
                }
                Map<String, Object> updates = new HashMap<>();
                updates.put("lastDonation", today);
                updates.put("nextDonationDate", nextDate);
                updates.put("donationCount", count + 1);
                updates.put("canDonate", false);

                db.child("Donors").child(donor.getDonorId()).updateChildren(updates).addOnSuccessListener(aVoid -> {
                    if (donor.getRequestId() != null) db.child("Requests").child(donor.getRequestId()).child("status").setValue("مغلق");
                    db.child("IncomingDonations").child(donor.getUid()).removeValue();
                    Toast.makeText(BloodBankDonorsActivity.this, "تم تسجيل التبرع بنجاح ✅", Toast.LENGTH_SHORT).show();
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void applyCurrentTabFilter() {
        int position = tabLayout.getSelectedTabPosition();
        String query = etSearchName.getText().toString().toLowerCase().trim();
        filteredList.clear();

        if (position == 0) {
            layoutBloodTypeFilters.setVisibility(View.VISIBLE);
            layoutTestFilters.setVisibility(View.GONE);
            filterDonors(query);
        } else if (position == 1) {
            layoutBloodTypeFilters.setVisibility(View.GONE);
            layoutTestFilters.setVisibility(View.GONE);
            filterIncoming(query);
        } else if (position == 2) {
            layoutBloodTypeFilters.setVisibility(View.GONE);
            layoutTestFilters.setVisibility(View.VISIBLE);
            filterTests(query);
        }

    else if (position == 3) { // تاب السجل
            layoutBloodTypeFilters.setVisibility(View.GONE);
            layoutTestFilters.setVisibility(View.GONE);
            filterHistory(query); // دالة رح ننشئها الآن
        }
        adapter.notifyDataSetChanged();
    }

    private void filterIncoming(String query) {
        for (BloodBankDonorsModel d : incomingDonors) {
            if (d.getDisplayName().toLowerCase().contains(query)) {
                filteredList.add(d);
            }
        }
    }

    private void filterDonors(String query) {
        String todayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date());
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
    }

    private void filterTests(String query) {
        for (BloodBankDonorsModel d : allDonors) {
            if (d.getBloodTestProofUrl() != null && !d.getBloodTestProofUrl().isEmpty()) {
                boolean matchesName = d.getDisplayName().toLowerCase().contains(query);
                boolean matchesStatus = currentTestFilter.equals("الكل") || currentTestFilter.equals(d.getBloodTestStatus());
                if (matchesName && matchesStatus) filteredList.add(d);
            }
        }
    }

    private void setupListeners() {
        etSearchName.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyCurrentTabFilter(); }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        // تم إصلاح استدعاء الدوال هنا بتمرير الـ query
        findViewById(R.id.btnAll).setOnClickListener(v -> { selectedBloodType = "الكل"; applyCurrentTabFilter(); });
        findViewById(R.id.btnAPlus).setOnClickListener(v -> { selectedBloodType = "A+"; applyCurrentTabFilter(); });
        findViewById(R.id.btnAMinus).setOnClickListener(v -> { selectedBloodType = "A-"; applyCurrentTabFilter(); });
        findViewById(R.id.btnBPlus).setOnClickListener(v -> { selectedBloodType = "B+"; applyCurrentTabFilter(); });
        findViewById(R.id.btnBMinus).setOnClickListener(v -> { selectedBloodType = "B-"; applyCurrentTabFilter(); });
        findViewById(R.id.btnOPlus).setOnClickListener(v -> { selectedBloodType = "O+"; applyCurrentTabFilter(); });
        findViewById(R.id.btnOMinus).setOnClickListener(v -> { selectedBloodType = "O-"; applyCurrentTabFilter(); });
        findViewById(R.id.btnABPlus).setOnClickListener(v -> { selectedBloodType = "AB+"; applyCurrentTabFilter(); });
        findViewById(R.id.btnABMinus).setOnClickListener(v -> { selectedBloodType = "AB-"; applyCurrentTabFilter(); });

        View btnToday = findViewById(R.id.btnToday);
        if (btnToday != null) btnToday.setOnClickListener(v -> { selectedBloodType = "اليوم"; applyCurrentTabFilter(); });

        findViewById(R.id.filterAllTests).setOnClickListener(v -> { currentTestFilter = "الكل"; applyCurrentTabFilter(); });
        findViewById(R.id.filterPending).setOnClickListener(v -> { currentTestFilter = "معلق"; applyCurrentTabFilter(); });
        findViewById(R.id.filterAccepted).setOnClickListener(v -> { currentTestFilter = "مقبول"; applyCurrentTabFilter(); });
        findViewById(R.id.filterRejected).setOnClickListener(v -> { currentTestFilter = "مرفوض"; applyCurrentTabFilter(); });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { applyCurrentTabFilter(); }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void registerDonation(BloodBankDonorsModel d) {}
    private void showNoteDialog(BloodBankDonorsModel d) {}

    private void updateTestStatusInDB(BloodBankDonorsModel d, String status) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("bloodTestStatus", status);

        // إذا تم القبول، نحدث تاريخ الفحص لليوم ليتمكن من التبرع
        if ("مقبول".equals(status)) {
            String today = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date());
            updates.put("lastBloodTest", today);
        }

        dbRef.child("Donors").child(d.getUid()).updateChildren(updates).addOnSuccessListener(aVoid -> {

            // --- إرسال الإشعار للمتبرع ---
            DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("Notifications").push();
            String notifId = notifRef.getKey();

            if (notifId != null) {
                HashMap<String, Object> notifData = new HashMap<>();
                notifData.put("notificationId", notifId);

                if ("مقبول".equals(status)) {
                    notifData.put("title", "✅ تم قبول فحص الدم");
                    notifData.put("message", "تم التحقق من فحصك الدوري بنجاح. يمكنك الآن التوجه للتبرع بالدم.");
                } else if ("مرفوض".equals(status)) {
                    notifData.put("title", "❌ تعذر قبول فحص الدم");
                    notifData.put("message", "نعتذر منك، تم رفض صورة الفحص المرفوعة. يرجى التأكد من وضوح الصورة وإعادة رفعها.");
                }

                notifData.put("type", "test_result");
                notifData.put("targetType", "DONOR");

                // ملاحظة: تأكدي أن كود المتبرع يقرأ الحقل باسم "userId" أو "targetUserId"
                // حسب ما هو مبرمج في DonorNotificationActivity
                notifData.put("userId", d.getUid());

                notifData.put("createdAt", System.currentTimeMillis());
                notifData.put("isRead", false);

                notifRef.setValue(notifData);
            }

            Toast.makeText(this, "تم تحديث الحالة وإرسال إشعار للمتبرع", Toast.LENGTH_SHORT).show();
        });
    }
    private void filterHistory(String query) {
        // جلب تاريخ اليوم بنفس الصيغة المخزنة بالداتابيز
        String todayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date());

        for (BloodBankDonorsModel d : allDonors) {
            // التحقق من الاسم
            boolean matchesName = d.getDisplayName().toLowerCase().contains(query);
            // التحقق إذا كان تاريخ آخر تبرع هو "اليوم"
            boolean donatedToday = d.getLastDonation() != null && d.getLastDonation().equals(todayDate);

            if (matchesName && donatedToday) {
                filteredList.add(d);
            }
        }
    }

    private void confirmDeletion(BloodBankDonorsModel d) {
        new AlertDialog.Builder(this).setTitle("حذف الفحص").setMessage("هل أنتِ متأكدة؟")
                .setPositiveButton("حذف", (dialog, which) -> {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("bloodTestProofUrl", "");
                    updates.put("bloodTestStatus", "");
                    dbRef.child("Donors").child(d.getUid()).updateChildren(updates);
                }).setNegativeButton("إلغاء", null).show();
    }
}