package com.example.vivalink;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        // جلب مدينة المستشفى الحالية
        FirebaseDatabase.getInstance().getReference("Hospitals").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            hospitalCity = snapshot.child("city").getValue(String.class);
                            loadDonorsFromSameCity();
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadDonorsFromSameCity() {
        // التعديل هنا: البحث في عقدة "Donors" وليس "Users"
        FirebaseDatabase.getInstance().getReference("Donors")
                .orderByChild("city").equalTo(hospitalCity)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
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

        // ربط العناصر مع التصميم
        TextView tvTitle = dialog.findViewById(R.id.tvDonorName);
        EditText etPhone = dialog.findViewById(R.id.etPhone);
        EditText etDiseases = dialog.findViewById(R.id.etDiseases);
        EditText etLastDonation = dialog.findViewById(R.id.etLastDonation);
        EditText etNote = dialog.findViewById(R.id.etNote);
        Button btnSave = dialog.findViewById(R.id.btnUpdate);
        TextView tvEligibility = dialog.findViewById(R.id.tvEligibility);

        // التصميم البرمجي (Native) كما طلبت
        GradientDrawable fieldBg = new GradientDrawable();
        fieldBg.setColor(Color.parseColor("#F5F5F5"));
        fieldBg.setCornerRadius(15f);
        fieldBg.setStroke(2, Color.parseColor("#DDDDDD"));

        etPhone.setBackground(fieldBg);
        etDiseases.setBackground(fieldBg);
        etLastDonation.setBackground(fieldBg);
        etNote.setBackground(fieldBg);

        // تعبئة البيانات من Firebase
        tvTitle.setText(donor.getFullName());
        etPhone.setText(donor.getPhone());
        etLastDonation.setText(donor.getLastDonation());
        etDiseases.setText(donor.isHasDisease() ? donor.getDiseaseName() : "لا يوجد أمراض");

        // فحص الأهلية (4 شهور) بناءً على الفحص الدوري
        checkEligibility(donor.getLastBloodTest(), donor.getLastDonation(), tvEligibility);

        btnSave.setOnClickListener(v -> {
            // تحديث البيانات في عقدة Donors
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Donors").child(donor.getUid());
            ref.child("lastDonation").setValue(etLastDonation.getText().toString());
            // زيادة عدد التبرعات عند الحفظ
            ref.child("donationCount").setValue(donor.getDonationCount() + 1);

            Toast.makeText(this, "تم تحديث بيانات المتبرع بنجاح", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void checkEligibility(String testDateStr, String donationDateStr, TextView tv) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date testDate = sdf.parse(testDateStr);
            Date donationDate = sdf.parse(donationDateStr);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -4);
            Date fourMonthsAgo = cal.getTime();

            // إذا كان الفحص أو التبرع صار له أكثر من 4 شهور -> غير مؤهل
            if (testDate.before(fourMonthsAgo) || donationDate.after(fourMonthsAgo)) {
                tv.setText("❌ غير مؤهل (يجب فحص دم كل 4 شهور)");
                tv.setTextColor(Color.RED);
            } else {
                tv.setText("✅ مؤهل للتبرع");
                tv.setTextColor(Color.parseColor("#2E7D32"));
            }
        } catch (Exception e) {
            tv.setText("⚠️ حالة الأهلية: تأكد من التواريخ");
        }
    }
}