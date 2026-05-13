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
        void onDeleteTest(BloodBankDonorsModel d);

        void onConfirmArrival(BloodBankDonorsModel d);
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


        h.tvName.setText(d.getDisplayName());
        h.tvPhone.setText(d.getPhone());
        h.tvBloodType.setText(d.getBloodType());
        h.tvLastDonation.setText("آخر تبرع: " + d.getLastDonation());
        h.tvDonationCount.setText(d.getDonationCount());
        h.tvCity.setText(d.getCity());


        TabLayout tabLayout = ((BloodBankDonorsActivity) h.itemView.getContext()).findViewById(R.id.tabLayout);
        int selectedTab = tabLayout.getSelectedTabPosition();


        h.layoutTestSection.setVisibility(View.GONE);
        h.layoutIncomingSection.setVisibility(View.GONE);
        h.expandLayout.setVisibility(View.GONE);
        h.btnExpand.setVisibility(View.VISIBLE);


        if (selectedTab == 2) {

            h.layoutTestSection.setVisibility(View.VISIBLE);
            h.btnExpand.setVisibility(View.GONE);

            if (d.getBloodTestProofUrl() != null && !d.getBloodTestProofUrl().isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(d.getBloodTestProofUrl(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    h.imgTestProof.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    h.imgTestProof.setImageResource(android.R.drawable.ic_menu_report_image);
                }
            }

            h.btnAcceptTest.setOnClickListener(v -> listener.onUpdateTestStatus(d, "مقبول"));
            h.btnRejectTest.setOnClickListener(v -> listener.onUpdateTestStatus(d, "مرفوض"));

            if ("مرفوض".equals(d.getBloodTestStatus())) {
                h.btnDeleteTest.setVisibility(View.VISIBLE);
            } else {
                h.btnDeleteTest.setVisibility(View.GONE);
            }
            h.btnDeleteTest.setOnClickListener(v -> listener.onDeleteTest(d));

        } else if (selectedTab == 1) {

            h.layoutIncomingSection.setVisibility(View.VISIBLE);
            h.btnExpand.setVisibility(View.GONE);
            h.btnConfirmArrival.setOnClickListener(v -> listener.onConfirmArrival(d));

        } else if (selectedTab == 3) {

            h.btnExpand.setVisibility(View.GONE);
            h.layoutTestSection.setVisibility(View.GONE);
            h.layoutIncomingSection.setVisibility(View.GONE);
            h.expandLayout.setVisibility(View.GONE);

        } else {

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
        LinearLayout expandLayout, layoutTestSection, layoutIncomingSection;
        Button btnRegister, btnNote, btnAcceptTest, btnRejectTest, btnDeleteTest, btnConfirmArrival;
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

            layoutTestSection = v.findViewById(R.id.layoutTestSection);
            layoutIncomingSection = v.findViewById(R.id.layoutIncomingSection);
            imgTestProof = v.findViewById(R.id.imgTestProof);
            btnAcceptTest = v.findViewById(R.id.btnAcceptTest);
            btnRejectTest = v.findViewById(R.id.btnRejectTest);
            btnDeleteTest = v.findViewById(R.id.btnDeleteTest);
            btnConfirmArrival = v.findViewById(R.id.btnConfirmArrival);

            btnRegister = v.findViewById(R.id.btnRegister);
            btnNote = v.findViewById(R.id.btnNote);
        }
    }
}