package com.example.vivalink;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import java.util.List;

public class BloodBankDonorsAdapter extends RecyclerView.Adapter<BloodBankDonorsAdapter.VH> {
    private List<BloodBankDonorsModel> list;
    private OnDonorActionListener listener;

    public interface OnDonorActionListener {
        void onRegisterDonation(BloodBankDonorsModel d);
        void onAddNote(BloodBankDonorsModel d);
        void onUpdateTestStatus(BloodBankDonorsModel d, String status);
        void onDeleteTest(BloodBankDonorsModel d); // دالة الحذف
    }

    public BloodBankDonorsAdapter(List<BloodBankDonorsModel> list, OnDonorActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_bloodbank_donors, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int p) {
        BloodBankDonorsModel d = list.get(p);

        // البيانات الأساسية
        h.tvName.setText(d.getDisplayName());
        h.tvPhone.setText(d.getPhone());
        h.tvBloodType.setText(d.getBloodType());
        h.tvLastDonation.setText("آخر تبرع: " + d.getLastDonation());
        h.tvDonationCount.setText(d.getDonationCount());
        h.tvCity.setText(d.getCity());

        // الوصول للـ TabLayout للتحقق من التاب الحالي
        TabLayout tabLayout = ((BloodBankDonorsActivity) h.itemView.getContext()).findViewById(R.id.tabLayout);
        int selectedTab = tabLayout.getSelectedTabPosition();

        // منطق تاب "الفحوصات" (رقم 2)
        if (selectedTab == 2) {
            h.layoutTestSection.setVisibility(View.VISIBLE);
            h.expandLayout.setVisibility(View.GONE);
            h.btnExpand.setVisibility(View.GONE);

            // عرض صورة الفحص من Base64
            if (d.getBloodTestProofUrl() != null && !d.getBloodTestProofUrl().isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(d.getBloodTestProofUrl(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    h.imgTestProof.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    h.imgTestProof.setImageResource(android.R.drawable.ic_menu_report_image);
                }
            }

            // أزرار القبول والرفض
            h.btnAcceptTest.setOnClickListener(v -> listener.onUpdateTestStatus(d, "مقبول"));
            h.btnRejectTest.setOnClickListener(v -> listener.onUpdateTestStatus(d, "مرفوض"));

            // منطق إظهار زر الحذف فقط في حالة المرفوض
            if ("مرفوض".equals(d.getBloodTestStatus())) {
                h.btnDeleteTest.setVisibility(View.VISIBLE);
            } else {
                h.btnDeleteTest.setVisibility(View.GONE);
            }

            h.btnDeleteTest.setOnClickListener(v -> listener.onDeleteTest(d));

        } else {
            // منطق تاب "المتبرعون"
            h.layoutTestSection.setVisibility(View.GONE);
            h.btnExpand.setVisibility(View.VISIBLE);

            h.btnExpand.setOnClickListener(v -> {
                h.expandLayout.setVisibility(h.expandLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                h.btnExpand.setRotation(h.expandLayout.getVisibility() == View.VISIBLE ? 180 : 0);
            });

            h.btnRegister.setOnClickListener(v -> listener.onRegisterDonation(d));
            h.btnNote.setOnClickListener(v -> listener.onAddNote(d));
        }
    }

    @Override public int getItemCount() { return list.size(); }

    class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvBloodType, tvLastDonation, tvDonationCount, tvCity;
        ImageButton btnExpand;
        LinearLayout expandLayout, layoutTestSection;
        Button btnRegister, btnNote, btnAcceptTest, btnRejectTest, btnDeleteTest;
        ImageView imgTestProof;

        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvPhone = v.findViewById(R.id.tvPhone);
            tvBloodType = v.findViewById(R.id.tvBloodType);
            tvLastDonation = v.findViewById(R.id.tvLastDonation);
            tvDonationCount = v.findViewById(R.id.tvDonationCount);
            tvCity = v.findViewById(R.id.tvCity);
            btnExpand = v.findViewById(R.id.btnExpand);
            expandLayout = v.findViewById(R.id.expandLayout);

            // العناصر الجديدة
            layoutTestSection = v.findViewById(R.id.layoutTestSection);
            imgTestProof = v.findViewById(R.id.imgTestProof);
            btnAcceptTest = v.findViewById(R.id.btnAcceptTest);
            btnRejectTest = v.findViewById(R.id.btnRejectTest);
            btnDeleteTest = v.findViewById(R.id.btnDeleteTest); // ربط زر الحذف

            btnRegister = v.findViewById(R.id.btnRegister);
            btnNote = v.findViewById(R.id.btnNote);
        }
    }
}