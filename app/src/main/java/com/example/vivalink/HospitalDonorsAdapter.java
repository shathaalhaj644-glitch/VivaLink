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
    private List<RequestModel> requestList;
    private OnDonorClickListener listener;

    public interface OnDonorClickListener {
        void onDonorClick(RequestModel request);
    }

    public HospitalDonorsAdapter(Context context, List<RequestModel> requestList, OnDonorClickListener listener) {
        this.context = context;
        this.requestList = requestList;
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

        RequestModel request = requestList.get(position);


        holder.tvHospitalName.setText(request.getHospitalName());
        holder.tvCity.setText("📍 المدينة: " + request.getCity());
        holder.tvBloodType.setText("🩸 فصيلة الدم: " + request.getBloodType());
        holder.tvUnits.setText("🧪 عدد الوحدات: " + request.getUnits());
        holder.tvDepartment.setText("🏢 القسم: " + request.getDepartment());


        if (request.getDate() != null) {
            holder.tvDate.setText("📅 تاريخ ووقت الطلب: " + request.getDate());
        } else {
            holder.tvDate.setText("📅 تاريخ ووقت الطلب: --");
        }


        holder.btnDonate.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDonorClick(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class DonorVH extends RecyclerView.ViewHolder {


        TextView tvHospitalName, tvCity, tvBloodType, tvUnits, tvDepartment, tvDate;
        Button btnDonate;

        public DonorVH(@NonNull View v) {
            super(v);
            tvHospitalName = v.findViewById(R.id.tvHospitalName);
            tvCity = v.findViewById(R.id.tvCity);
            tvBloodType = v.findViewById(R.id.tvBloodType);
            tvUnits = v.findViewById(R.id.tvUnits);
            tvDepartment = v.findViewById(R.id.tvDepartment);
            tvDate = v.findViewById(R.id.tvDate);
            btnDonate = v.findViewById(R.id.btnDonateItem);
        }
    }
}