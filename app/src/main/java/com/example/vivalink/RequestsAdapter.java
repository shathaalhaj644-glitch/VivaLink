package com.example.vivalink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {
    private List<BloodRequests> list;
    private OnDonateClickListener listener;

    public interface OnDonateClickListener {
        void onDonateClick(BloodRequests request);
    }

    public RequestsAdapter(List<BloodRequests> list, OnDonateClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blood_requests, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodRequests req = list.get(position);

        holder.tvBlood.setText(req.getBloodType());
        holder.tvHospital.setText(req.getHospitalName());
        holder.tvCity.setText("📍 " + req.getCity());
        holder.tvDept.setText("🏥 " + req.getDepartment());
        holder.tvUnits.setText("🩸 وحدات: " + req.getUnits());
        holder.tvTime.setText("⏰ " + req.getTime());

        holder.btnDonate.setOnClickListener(v -> {
            if (listener != null) listener.onDonateClick(req);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBlood, tvHospital, tvCity, tvDept, tvUnits, tvTime;
        Button btnDonate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBlood = itemView.findViewById(R.id.tvBloodTypeItem);
            tvHospital = itemView.findViewById(R.id.tvHospitalItem);
            tvCity = itemView.findViewById(R.id.tvCityItem);
            tvDept = itemView.findViewById(R.id.tvDepartmentItem);
            tvUnits = itemView.findViewById(R.id.tvUnitsItem);
            tvTime = itemView.findViewById(R.id.tvTimeItem);
            btnDonate = itemView.findViewById(R.id.btnDonateItem);
        }
    }
}