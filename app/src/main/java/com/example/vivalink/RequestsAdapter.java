package com.example.vivalink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blood_requests, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BloodRequests req = list.get(position);

        holder.tvBlood.setText(req.getBloodType() != null ? req.getBloodType() : "--");
        holder.tvHospital.setText(req.getHospitalName() != null ? req.getHospitalName() : "غير محدد");
        holder.tvCity.setText("📍 " + (req.getCity() != null ? req.getCity() : "غير محدد"));
        holder.tvUnits.setText("🩸الوحدات المطلوبة: " + (req.getUnits() != null ? req.getUnits() : "0"));

        holder.btnDonate.setOnClickListener(v -> {
            if (listener != null) listener.onDonateClick(req);
        });
    }

    @Override
    public int getItemCount() { return list != null ? list.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBlood, tvHospital, tvCity, tvUnits;
        Button btnDonate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvBlood = itemView.findViewById(R.id.tvBloodTypeItem);
            tvHospital = itemView.findViewById(R.id.tvHospitalItem);
            tvCity = itemView.findViewById(R.id.tvCityItem);
            tvUnits = itemView.findViewById(R.id.tvUnitsItem);
            btnDonate = itemView.findViewById(R.id.btnDonateItem);
        }
    }
}