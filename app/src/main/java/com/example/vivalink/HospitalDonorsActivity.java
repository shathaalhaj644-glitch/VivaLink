package com.example.vivalink;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class HospitalDonorsActivity extends AppCompatActivity {

    private RecyclerView rvDonors;
    private HospitalDonorsAdapter adapter;
    private List<Donors> allDonors = new ArrayList<>();
    private List<Donors> filteredList = new ArrayList<>();
    private String hospitalCity = "";
    private EditText etSearch;
    private TextView tvHeaderTitle;
    private String selectedBloodType = "الكل";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_donors);

        initViews();
        getHospitalInfo();
    }

    private void initViews() {
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        rvDonors = findViewById(R.id.rvDonors);
        etSearch = findViewById(R.id.etSearch);

        rvDonors.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HospitalDonorsAdapter(this, filteredList, this::showDonorDetailsDialog);
        rvDonors.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        setupFilterButtons();
    }

    private void getHospitalInfo() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance().getReference("Hospitals").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String city = snapshot.child("city").getValue(String.class);
                            hospitalCity = (city != null) ? city.replace("📍", "").trim() : "";
                            tvHeaderTitle.setText("المتبرعون - " + hospitalCity);
                            loadDonorsFromServer();
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadDonorsFromServer() {
        FirebaseDatabase.getInstance().getReference("Donors")
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allDonors.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                Donors d = new Donors();
                                d.setUid(ds.getKey());
                                d.setFullName(String.valueOf(ds.child("fullName").getValue()));
                                d.setName(String.valueOf(ds.child("name").getValue()));
                                d.setBloodType(String.valueOf(ds.child("bloodType").getValue()));
                                d.setCity(String.valueOf(ds.child("city").getValue()));
                                d.setPhone(String.valueOf(ds.child("phone").getValue()));
                                d.setLastDonation(String.valueOf(ds.child("lastDonation").getValue()));
                                d.setLastBloodTest(String.valueOf(ds.child("lastBloodTest").getValue()));
                                d.setBloodTestStatus(String.valueOf(ds.child("bloodTestStatus").getValue()));
                                d.setHasDisease(String.valueOf(ds.child("hasDisease").getValue()));
                                d.setDonationCount(String.valueOf(ds.child("donationCount").getValue()));

                                if (ds.hasChild("isEligible")) {
                                    d.setEligible(Boolean.TRUE.equals(ds.child("isEligible").getValue(Boolean.class)));
                                }

                                String dCityClean = d.getCity().replace("📍", "").trim();
                                if (dCityClean.equalsIgnoreCase(hospitalCity)) {
                                    allDonors.add(d);
                                }
                            } catch (Exception e) { e.printStackTrace(); }
                        }
                        applyFilters();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void applyFilters() {
        String query = etSearch.getText().toString().toLowerCase().trim();
        filteredList.clear();

        for (Donors d : allDonors) {
            String name = (d.getFullName() != null && !d.getFullName().equals("null")) ? d.getFullName() : d.getName();
            boolean matchesName = (name != null && name.toLowerCase().contains(query));
            boolean matchesBlood = selectedBloodType.equals("الكل") || d.getBloodType().trim().equalsIgnoreCase(selectedBloodType);

            if (matchesName && matchesBlood) {
                filteredList.add(d);
            }
        }
        adapter.updateList(filteredList);
    }

    private void showDonorDetailsDialog(Donors donor) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_donor_details);

        if (dialog.getWindow() != null) {
            int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.95);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvName = dialog.findViewById(R.id.tvName);
        TextView tvBloodInfo = dialog.findViewById(R.id.tvBloodTypeInfo);
        TextView tvCityInfo = dialog.findViewById(R.id.tvCityInfo);
        TextView tvCountInfo = dialog.findViewById(R.id.tvDonationCountInfo);
        TextView tvHospitalsHistory = dialog.findViewById(R.id.tvHospitalsHistory);

        EditText etPhone = dialog.findViewById(R.id.etPhone);
        etPhone.setTextColor(Color.BLACK);
        EditText etLastDonation = dialog.findViewById(R.id.etLastDonation);
        EditText etLastTest = dialog.findViewById(R.id.etLastTest);
        EditText etAddInfo = dialog.findViewById(R.id.etAddInfo);
        CheckBox cbEligible = dialog.findViewById(R.id.cbEligible);
        TextView tvStatus = dialog.findViewById(R.id.tvEligibleStatus);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        String displayName = (donor.getFullName() == null || donor.getFullName().equals("null")) ? donor.getName() : donor.getFullName();
        tvName.setText(clean(displayName));

        tvBloodInfo.setText("🩸 فصيلة الدم: " + clean(donor.getBloodType()));
        tvCityInfo.setText("📍 المدينة: " + clean(donor.getCity()));
        tvCountInfo.setText("🔢 عدد التبرعات: " + clean(donor.getDonationCount()));

        etPhone.setText(clean(donor.getPhone()));
        etLastDonation.setText(clean(donor.getLastDonation()));
        etLastTest.setText(clean(donor.getLastBloodTest()));
        etAddInfo.setText(clean(donor.getBloodTestStatus()));

        fetchHospitalsHistory(donor.getUid(), tvHospitalsHistory);

        boolean isEligible = checkEligibility(donor.getLastDonation(), donor.getLastBloodTest(), donor.getHasDisease());
        cbEligible.setChecked(isEligible);
        updateUI(isEligible, tvStatus);

        cbEligible.setOnClickListener(v -> updateUI(cbEligible.isChecked(), tvStatus));

        btnSave.setOnClickListener(v -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("lastDonation", etLastDonation.getText().toString());
            map.put("lastBloodTest", etLastTest.getText().toString());
            map.put("bloodTestStatus", etAddInfo.getText().toString());
            map.put("isEligible", cbEligible.isChecked());

            FirebaseDatabase.getInstance().getReference("Donors").child(donor.getUid())
                    .updateChildren(map).addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "تم الحفظ بنجاح ✅", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
        });

        dialog.findViewById(R.id.tvClose).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void fetchHospitalsHistory(String donorUid, TextView tvHistory) {
        DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference("Donors").child(donorUid).child("myDonations");
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("Requests");

        donationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    tvHistory.setText("لا يوجد سجل سابق");
                    return;
                }

                tvHistory.setText("");
                Set<String> hospitalNames = new HashSet<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    requestsRef.child(ds.getKey()).child("hospitalName").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot requestSnapshot) {
                            if (requestSnapshot.exists()) {
                                String name = requestSnapshot.getValue(String.class);
                                if (name != null && !hospitalNames.contains(name)) {
                                    hospitalNames.add(name);
                                    if (tvHistory.getText().length() > 0) tvHistory.append("\n");
                                    tvHistory.append("• " + name);
                                }
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private boolean checkEligibility(String lastDonation, String lastTest, String hasDisease) {
        if (hasDisease != null && (hasDisease.equalsIgnoreCase("Yes") || hasDisease.equals("نعم"))) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            long now = System.currentTimeMillis();
            long fourMonthsInMs = 4L * 30 * 24 * 60 * 60 * 1000;

            if (lastDonation != null && lastDonation.length() > 5 && !lastDonation.equals("null")) {
                Date dDate = sdf.parse(lastDonation);
                if (dDate != null && (now - dDate.getTime() < fourMonthsInMs)) return false;
            }
        } catch (Exception e) { return true; }
        return true;
    }

    private void setupFilterButtons() {
        findViewById(R.id.btnAll).setOnClickListener(v -> { selectedBloodType = "الكل"; applyFilters(); });
        findViewById(R.id.btnAPlus).setOnClickListener(v -> { selectedBloodType = "A+"; applyFilters(); });
        findViewById(R.id.btnAMinus).setOnClickListener(v -> { selectedBloodType = "A-"; applyFilters(); });
        findViewById(R.id.btnBPlus).setOnClickListener(v -> { selectedBloodType = "B+"; applyFilters(); });
        findViewById(R.id.btnBMinus).setOnClickListener(v -> { selectedBloodType = "B-"; applyFilters(); });
        findViewById(R.id.btnOPlus).setOnClickListener(v -> { selectedBloodType = "O+"; applyFilters(); });
        findViewById(R.id.btnOMinus).setOnClickListener(v -> { selectedBloodType = "O-"; applyFilters(); });
        findViewById(R.id.btnABPlus).setOnClickListener(v -> { selectedBloodType = "AB+"; applyFilters(); });
        findViewById(R.id.btnABMinus).setOnClickListener(v -> { selectedBloodType = "AB-"; applyFilters(); });
    }

    private String clean(String s) { return (s == null || s.equals("null")) ? "" : s; }

    private void updateUI(boolean isChecked, TextView tv) {
        if (isChecked) {
            tv.setText("");
            tv.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            tv.setText("❌ غير مؤهل حالياً");
            tv.setTextColor(Color.RED);
        }
    }
}