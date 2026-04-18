package com.example.vivalink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HospitalDonorsAdapter extends RecyclerView.Adapter<HospitalDonorsAdapter.DonorVH> {

    private Context context;
    private List<Donors> list; // التأكد من استخدام كلاس Donors
    private OnDonorClickListener listener;

    // Interface للتعامل مع نقرة العنصر وفتح الدايلوج
    public interface OnDonorClickListener {
        void onDonorClick(Donors donor);
    }

    public HospitalDonorsAdapter(Context context, List<Donors> list, OnDonorClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DonorVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // تأكدي أن اسم ملف الـ item هو item_hospital_donors
        View v = LayoutInflater.from(context).inflate(R.layout.item_hospital_donors, parent, false);
        return new DonorVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorVH holder, int position) {
        Donors donor = list.get(position);

        // عرض اسم المتبرع (يفضل fullName وإذا فارغ نستخدم name)
        String displayName = (donor.getFullName() != null && !donor.getFullName().isEmpty())
                ? donor.getFullName() : donor.getName();
        holder.tvName.setText(displayName);

        // عرض المدينة وفصيلة الدم
        holder.tvCity.setText("📍 " + donor.getCity());
        holder.tvBloodType.setText("🩸 " + donor.getBloodType());

        // عند النقر على العنصر لفتح الدايلوج
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDonorClick(donor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    // الدالة السحرية لتحديث القائمة وتصفير الإيرور
    public void updateList(List<Donors> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    public static class DonorVH extends RecyclerView.ViewHolder {
        TextView tvName, tvCity, tvBloodType;

        public DonorVH(@NonNull View v) {
            super(v);
            // تأكدي أن هذه الـ IDs موجودة في ملف item_hospital_donors.xml
            tvName = v.findViewById(R.id.tvDonorName);
            tvCity = v.findViewById(R.id.tvCity);
            tvBloodType = v.findViewById(R.id.tvBloodType);
        }
    }
}