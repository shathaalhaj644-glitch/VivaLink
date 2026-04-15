package com.example.vivalink;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private List<HospitalDonorsModel> donorList;
    private String hospitalCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_donors);

        rvDonors = findViewById(R.id.rvDonors);
        rvDonors.setLayoutManager(new LinearLayoutManager(this));
        donorList = new ArrayList<>();

        adapter = new HospitalDonorsAdapter(this, donorList, this::showDonorDetailsDialog);
        rvDonors.setAdapter(adapter);

        getHospitalInfo();
    }

    private void getHospitalInfo() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance().getReference("Hospitals").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            hospitalCity = snapshot.child("city").getValue(String.class);

                            // ✨ تحديث الهيدر ليظهر اسم المدينة
                            TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
                            tvHeaderTitle.setText("المتبرعون - " + hospitalCity);

                            loadDonorsFromSameCity();
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadDonorsFromSameCity() {
        FirebaseDatabase.getInstance().getReference("Donors")
                .orderByChild("city").equalTo(hospitalCity)
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        donorList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            HospitalDonorsModel donor = ds.getValue(HospitalDonorsModel.class);
                            if (donor != null) {
                                donor.setUid(ds.getKey());
                                donorList.add(donor);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void showDonorDetailsDialog(HospitalDonorsModel donor) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_donor_details);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvName = dialog.findViewById(R.id.tvDonorName);
        EditText etPhone = dialog.findViewById(R.id.etPhone);
        EditText etDiseases = dialog.findViewById(R.id.etDiseases);
        EditText etLastDonation = dialog.findViewById(R.id.etLastDonation);
        EditText etLastBloodTest = dialog.findViewById(R.id.etLastBloodTest);
        EditText etNote = dialog.findViewById(R.id.etNote);
        EditText etHospital = dialog.findViewById(R.id.etHospital);
        TextView tvEligibility = dialog.findViewById(R.id.tvEligibility);
        RadioGroup rgEligibility = dialog.findViewById(R.id.rgEligibility);
        RadioButton rbEligible = dialog.findViewById(R.id.rbEligible);
        RadioButton rbNotEligible = dialog.findViewById(R.id.rbNotEligible);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);

        // تعبئة البيانات
        tvName.setText(donor.getFullName());
        etPhone.setText(donor.getPhone());
        etDiseases.setText(donor.isHasDisease() ? donor.getDiseaseName() : "");
        etLastDonation.setText(donor.getLastDonation());
        etLastBloodTest.setText(donor.getLastBloodTest());
        etNote.setText("");
        etHospital.setText(donor.getHospitalName());

        // فحص الأهلية
        checkEligibility(donor.getLastBloodTest(), donor.getLastDonation(), tvEligibility, rbEligible, rbNotEligible);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnUpdate.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Donors").child(donor.getUid());
            ref.child("phone").setValue(etPhone.getText().toString());
            ref.child("diseaseName").setValue(etDiseases.getText().toString());
            ref.child("lastDonation").setValue(etLastDonation.getText().toString());
            ref.child("lastBloodTest").setValue(etLastBloodTest.getText().toString());
            ref.child("hospitalName").setValue(etHospital.getText().toString());
            ref.child("hasDisease").setValue(!etDiseases.getText().toString().isEmpty());

            if (rbEligible.isChecked()) {
                tvEligibility.setText("✅ مؤهل للتبرع");
                tvEligibility.setTextColor(Color.parseColor("#2E7D32"));
            } else {
                tvEligibility.setText("❌ غير مؤهل للتبرع");
                tvEligibility.setTextColor(Color.RED);
            }

            Toast.makeText(this, "تم حفظ التعديلات بنجاح", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void checkEligibility(String testDateStr, String donationDateStr, TextView tv, RadioButton rbEligible, RadioButton rbNotEligible) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date testDate = sdf.parse(testDateStr);
            Date donationDate = sdf.parse(donationDateStr);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -4);
            Date fourMonthsAgo = cal.getTime();

            if (testDate.before(fourMonthsAgo) || donationDate.before(fourMonthsAgo)) {
                tv.setText("✅ مؤهل للتبرع");
                tv.setTextColor(Color.parseColor("#2E7D32"));
                rbEligible.setChecked(true);
            } else {
                tv.setText("❌ غير مؤهل للتبرع");
                tv.setTextColor(Color.RED);
                rbNotEligible.setChecked(true);
            }
        } catch (Exception e) {
            tv.setText("⚠️ حالة الأهلية: تأكد من التواريخ");
        }
    }
}
