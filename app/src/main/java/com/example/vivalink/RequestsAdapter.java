package com.example.vivalink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


        holder.tvBloodType.setText(request.getBloodType());
        holder.tvHospital.setText(request.getHospitalName());
        holder.tvLocation.setText("📍 " + request.getCity());
        holder.tvDepartment.setText("🏥 " + request.getDepartment());
        holder.tvUnits.setText("🩸 عدد الوحدات: " + request.getUnits());


        holder.btnDonate.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRequestClick(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvBloodType, tvHospital, tvLocation, tvDepartment, tvUnits, tvTime;
        Button btnDonate;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBloodType = itemView.findViewById(R.id.tvBloodType);
            tvHospital = itemView.findViewById(R.id.tvHospitalName);
            tvLocation = itemView.findViewById(R.id.tvCity);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvUnits = itemView.findViewById(R.id.tvUnits);
            btnDonate = itemView.findViewById(R.id.btnDonateItem);
        }
    }
}