package com.example.vivalink;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BloodBankDonorsAdapter extends RecyclerView.Adapter<BloodBankDonorsAdapter.VH> {
    private List<BloodBankDonorsModel> list;
    private OnDonorActionListener listener;

    public interface OnDonorActionListener {
        void onRegisterDonation(BloodBankDonorsModel d);
        void onAddNote(BloodBankDonorsModel d);
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

        h.btnExpand.setOnClickListener(v -> {
            h.expandLayout.setVisibility(h.expandLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            h.btnExpand.setRotation(h.expandLayout.getVisibility() == View.VISIBLE ? 180 : 0);
        });

        h.btnRegister.setOnClickListener(v -> listener.onRegisterDonation(d));
        h.btnNote.setOnClickListener(v -> listener.onAddNote(d));
    }

    @Override public int getItemCount() { return list.size(); }

    class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvBloodType, tvLastDonation, tvDonationCount, tvCity;
        ImageButton btnExpand;
        LinearLayout expandLayout;
        Button btnRegister, btnNote;

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
            btnRegister = v.findViewById(R.id.btnRegister);
            btnNote = v.findViewById(R.id.btnNote);
        }
    }
}