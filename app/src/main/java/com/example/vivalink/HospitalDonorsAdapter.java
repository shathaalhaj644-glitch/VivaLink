package com.example.vivalink;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HospitalDonorsAdapter extends RecyclerView.Adapter<HospitalDonorsAdapter.DonorVH> {
    private Context context;
    private List<HospitalDonorsModel> donorList;

    public HospitalDonorsAdapter(Context context, List<HospitalDonorsModel> donorList) {
        this.context = context;
        this.donorList = donorList;
    }

    @NonNull
    @Override
    public DonorVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_hospital_donors, parent, false);
        return new DonorVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorVH holder, int position) {
        HospitalDonorsModel donor = donorList.get(position);


        holder.tvName.setText(donor.getFullName());
        holder.tvCity.setText("📍 " + donor.getCity());
        holder.tvBloodType.setText(donor.getBloodType());


        holder.tvDonationCount.setText("🩸 عدد التبرعات: " + donor.getDonationCount());

        String lastDate = donor.getLastDonation();
        holder.tvLastDonation.setText("📅 آخر تبرع: " + (lastDate != null && !lastDate.isEmpty() ? lastDate : "لا يوجد"));


        holder.btnCall.setOnClickListener(v -> {
            if (donor.getPhone() != null && !donor.getPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + donor.getPhone()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return donorList.size(); }

    public static class DonorVH extends RecyclerView.ViewHolder {
        TextView tvName, tvCity, tvBloodType, tvDonationCount, tvLastDonation;
        ImageButton btnCall;

        public DonorVH(@NonNull View v) {
            super(v);

            tvName = v.findViewById(R.id.tvDonorName);
            tvCity = v.findViewById(R.id.tvDonorCity);
            tvBloodType = v.findViewById(R.id.tvBloodType);
            tvDonationCount = v.findViewById(R.id.tvDonationCount);
            tvLastDonation = v.findViewById(R.id.tvLastDonation);
            btnCall = v.findViewById(R.id.btnCall);
        }
    }
}