package com.example.vivalink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {

    private List<BloodRequests> requestList;
    private OnRequestClickListener listener;

    public interface OnRequestClickListener {
        void onRequestClick(BloodRequests request);
    }

    public RequestsAdapter(List<BloodRequests> requestList, OnRequestClickListener listener) {
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blood_requests, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        BloodRequests request = requestList.get(position);

        holder.tvHospital.setText("🏥 " + request.getHospitalName());
        holder.tvBloodType.setText(request.getBloodType());
        holder.tvLocation.setText("📍 " + request.getCity());
        holder.tvDepartment.setText("🚨 " + request.getDepartment());
        holder.tvUnits.setText("🩸 " + request.getUnits());

        // عرض الوقت الذكي
        holder.tvTime.setText(getSmartTimeAgo(request.getTimestamp()));

        holder.itemView.setOnClickListener(v -> listener.onRequestClick(request));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    private String getSmartTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        // استخدام Math.abs لضمان الحساب الصحيح حتى لو توقيت الموبايل مش دقيق 100%
        long absDiff = Math.abs(diff);
        long minutes = absDiff / (1000 * 60);
        long hours = minutes / 60;
        long days = hours / 24;

        // 1. إذا كان الفرق أقل من دقيقة
        if (minutes < 1) {
            return "الآن";
        }
        // 2. إذا كان الفرق أقل من ساعة (مثلاً: منذ 15 دقيقة)
        else if (minutes < 60) {
            return "منذ " + minutes + " دقيقة";
        }
        // 3. إذا كان الفرق أقل من يوم (مثلاً: منذ ساعة و 15 دقيقة)
        else if (hours < 24) {
            long remainingMinutes = minutes % 60;
            String hourText = (hours == 1) ? "ساعة" : (hours == 2) ? "ساعتين" : hours + " ساعات";

            if (remainingMinutes == 0) return "منذ " + hourText;
            return "منذ " + hourText + " و " + remainingMinutes + " دقيقة";
        }
        // 4. الأيام
        else {
            if (days == 1) return "منذ يوم";
            if (days == 2) return "منذ يومين";
            if (days > 7) return "منذ فترة";
            return "منذ " + days + " أيام";
        }
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvHospital, tvBloodType, tvLocation, tvDepartment, tvUnits, tvTime;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHospital = itemView.findViewById(R.id.tvHospitalName);
            tvBloodType = itemView.findViewById(R.id.tvBloodType);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvUnits = itemView.findViewById(R.id.tvUnits);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}