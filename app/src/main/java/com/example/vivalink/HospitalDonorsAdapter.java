package com.example.vivalink;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HospitalDonorsAdapter extends RecyclerView.Adapter<HospitalDonorsAdapter.DonorVH> {

    private Context context;
    private List<HospitalDonorsModel> donorList;
    private OnDonorClickListener listener;

    public interface OnDonorClickListener {
        void onDonorClick(HospitalDonorsModel donor);
    }

    public HospitalDonorsAdapter(Context context, List<HospitalDonorsModel> donorList, OnDonorClickListener listener) {
        this.context = context;
        this.donorList = donorList;
        this.listener = listener;
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

        String lastDonation = donor.getLastDonation();
        String lastBloodTest = donor.getLastBloodTest();

        holder.tvLastDonation.setText("📅 آخر تبرع: " + (lastDonation != null && !lastDonation.isEmpty() ? lastDonation : "لا يوجد"));

        if (lastBloodTest == null || lastBloodTest.equals("none") || lastBloodTest.isEmpty()) {
            holder.tvStatus.setText("⏳ لم يتم إجراء فحص دم بعد");
            holder.tvStatus.setTextColor(Color.GRAY);
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date testDate = sdf.parse(lastBloodTest);

                Calendar cal = Calendar.getInstance();
                cal.setTime(testDate);
                cal.add(Calendar.MONTH, 4);

                Date expiryDate = cal.getTime();
                Date today = new Date();

                if (today.after(expiryDate)) {
                    holder.tvStatus.setText("⚠️ استحق الفحص (منتهي الصلاحية)");
                    holder.tvStatus.setTextColor(Color.RED);
                } else {
                    long diff = expiryDate.getTime() - today.getTime();
                    long daysLeft = diff / (24 * 60 * 60 * 1000);
                    holder.tvStatus.setText("✔️ فحص ساري (باقي " + daysLeft + " يوم)");
                    holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
                }
            } catch (Exception e) {
                holder.tvStatus.setText("⚠️ خطأ في تاريخ الفحص");
                holder.tvStatus.setTextColor(Color.RED);
            }
        }

        holder.tvNote.setText("📝 " + (donor.getHospitalNote() != null ? donor.getHospitalNote() : "لا توجد ملاحظات"));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDonorClick(donor);
            }
        });

        holder.btnCall.setOnClickListener(v -> {
            if (donor.getPhone() != null && !donor.getPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + donor.getPhone()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    public static class DonorVH extends RecyclerView.ViewHolder {
        TextView tvName, tvCity, tvBloodType, tvDonationCount, tvLastDonation;
        TextView tvStatus, tvNote;
        ImageButton btnCall;

        public DonorVH(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tvDonorName);
            tvCity = v.findViewById(R.id.tvDonorCity);
            tvBloodType = v.findViewById(R.id.tvBloodType);
            tvDonationCount = v.findViewById(R.id.tvDonationCount);
            tvLastDonation = v.findViewById(R.id.tvLastDonation);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvNote = v.findViewById(R.id.tvNote);
            btnCall = v.findViewById(R.id.btnCall);
        }
    }
}
