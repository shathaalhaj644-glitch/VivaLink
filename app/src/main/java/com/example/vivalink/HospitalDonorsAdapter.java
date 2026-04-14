package com.example.vivalink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HospitalDonorsAdapter extends RecyclerView.Adapter<HospitalDonorsAdapter.DonorVH> {

    private Context context;
    private List<HospitalDonorsModel> list;
    private OnDonorClickListener listener;

    public interface OnDonorClickListener { void onDonorClick(HospitalDonorsModel donor); }

    public HospitalDonorsAdapter(Context context, List<HospitalDonorsModel> list, OnDonorClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DonorVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_blood_requests, parent, false);
        return new DonorVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorVH holder, int position) {
        HospitalDonorsModel donor = list.get(position);

        holder.tvName.setText(donor.getFullName());
        holder.tvCity.setText("📍 المدينة: " + donor.getCity());
        holder.tvBloodType.setText("🩸 فصيلة الدم: " + donor.getBloodType());

        // تعديل النصوص لتناسب بيانات المتبرع
        holder.tvUnits.setText("عدد التبرعات: " + donor.getDonationCount());
        holder.tvDepartment.setText("آخر فحص: " + donor.getLastBloodTest());
        holder.tvDate.setText("تاريخ آخر تبرع: " + donor.getLastDonation());

        holder.btnDonate.setText("فتح الملف");
        holder.btnDonate.setOnClickListener(v -> {
            if (listener != null) listener.onDonorClick(donor);
        });
    }

    @Override public int getItemCount() { return list.size(); }

    public static class DonorVH extends RecyclerView.ViewHolder {
        TextView tvName, tvCity, tvBloodType, tvUnits, tvDepartment, tvDate;
        Button btnDonate;

        public DonorVH(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tvHospitalName);
            tvCity = v.findViewById(R.id.tvCity);
            tvBloodType = v.findViewById(R.id.tvBloodType);
            tvUnits = v.findViewById(R.id.tvUnits);
            tvDepartment = v.findViewById(R.id.tvDepartment);
            tvDate = v.findViewById(R.id.tvDate);
            btnDonate = v.findViewById(R.id.btnDonateItem);
        }
    }
}