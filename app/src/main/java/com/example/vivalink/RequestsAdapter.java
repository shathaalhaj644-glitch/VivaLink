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
    private OnRequestClickListener listener; // تعريف الـ Listener للضغط

    // الواجهة (Interface) ليتوافق مع الـ showDonateDialog في الـ Activity
    public interface OnRequestClickListener {
        void onRequestClick(BloodRequests request);
    }

    // ✅ التعديل: الـ Constructor صار يستقبل وسيطين (القائمة + الـ Listener)
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

        // عرض البيانات في الـ Item
        holder.tvBloodType.setText(request.getBloodType());
        holder.tvHospital.setText(request.getHospitalName());
        holder.tvLocation.setText("📍 " + request.getCity());
        holder.tvDepartment.setText("🏥 " + request.getDepartment());
        holder.tvUnits.setText("🩸 عدد الوحدات: " + request.getUnits());
        holder.tvTime.setText("⏰ " + request.getTime());

        // ✅ تفعيل زر "تبرع الآن" باستخدام الـ Listener الممرر من الـ Activity
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
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvUnits = itemView.findViewById(R.id.tvUnits);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnDonate = itemView.findViewById(R.id.btnDonateItem);
        }
    }
}