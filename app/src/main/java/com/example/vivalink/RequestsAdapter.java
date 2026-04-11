package com.example.vivalink;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {
    private List<RequestModel> requestList;
    private OnRequestClickListener listener;

    public interface OnRequestClickListener {
        void onRequestClick(RequestModel request, int position);
    }

    public RequestsAdapter(List<RequestModel> requestList, OnRequestClickListener listener) {
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
        RequestModel request = requestList.get(position);

        // 1. اللون الوردي للمربع (مطابق لطلبك)
        holder.cardView.setCardBackgroundColor(Color.parseColor("#FFF5F5"));

        holder.tvHospital.setText(request.getHospitalName());
        holder.tvLocation.setText("📍 المدينة: " + request.getCity());
        holder.tvBloodType.setText("🩸 فصيلة الدم: " + request.getBloodType());
        holder.tvUnits.setText("💉 عدد الوحدات: " + request.getUnits());
        holder.tvDepartment.setText("🏢 القسم: " + request.getDepartment());

        // 2. التاريخ بالإنجليزي (استخدام الدالة المحدثة بالأسفل)
        holder.tvDate.setText("📅 تاريخ الطلب: " + formatBloodDateToEnglish(request.getDate()));

        // 3. ثبات حالة "تم التبرع"
        if (request.isDonated()) {
            holder.btnDonate.setVisibility(View.GONE);
            holder.layoutDonated.setVisibility(View.VISIBLE);
        } else {
            holder.btnDonate.setVisibility(View.VISIBLE);
            holder.layoutDonated.setVisibility(View.GONE);
        }

        holder.btnDonate.setOnClickListener(v -> listener.onRequestClick(request, position));
    }

    // الدالة السحرية لعرض الأرقام بالإنجليزية
    private String formatBloodDateToEnglish(String rawDate) {
        if (rawDate == null) return "";
        try {
            // المحلل لصيغة الفايربيس (Thu Apr 09...)
            SimpleDateFormat parser = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH);
            Date date = parser.parse(rawDate);

            // المنسق مع Locale.ENGLISH لإجبار الأرقام الإنجليزية
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.ENGLISH);
            return formatter.format(date);
        } catch (Exception e) {
            return rawDate;
        }
    }

    @Override
    public int getItemCount() { return requestList.size(); }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvBloodType, tvHospital, tvLocation, tvDepartment, tvUnits, tvDate;
        Button btnDonate;
        LinearLayout layoutDonated;
        CardView cardView;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvBloodType = itemView.findViewById(R.id.tvBloodType);
            tvHospital = itemView.findViewById(R.id.tvHospitalName);
            tvLocation = itemView.findViewById(R.id.tvCity);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvUnits = itemView.findViewById(R.id.tvUnits);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDonate = itemView.findViewById(R.id.btnDonateItem);
            layoutDonated = itemView.findViewById(R.id.layoutDonatedStatus);
        }
    }
}