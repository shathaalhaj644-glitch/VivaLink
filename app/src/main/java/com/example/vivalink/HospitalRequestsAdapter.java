package com.example.vivalink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class HospitalRequestsAdapter extends RecyclerView.Adapter<HospitalRequestsAdapter.VH> {

    private Context context;
    private List<HospitalRequestModel> list;

    public HospitalRequestsAdapter(Context context, List<HospitalRequestModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // تأكدي أن اسم ملف الـ XML هو item_hospital_request
        View v = LayoutInflater.from(context).inflate(R.layout.item_hospital_request, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        HospitalRequestModel m = list.get(position);


        holder.tvBloodType.setText("🩸 " + m.bloodType);
        holder.tvUnits.setText(m.units + " وحدات");
        holder.tvDept.setText(m.department);
        holder.tvCity.setText(m.city);


        holder.tvStatusBadge.setText(m.status);


        holder.tvDateTime.setText("🕒 " + m.date);



        if ("مغلق".equals(m.status)) {
            holder.tvDonatedTag.setVisibility(View.VISIBLE);
        } else {
            holder.tvDonatedTag.setVisibility(View.GONE);
        }


        holder.btnChangeStatus.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.getMenu().add("مفتوح");
            popup.getMenu().add("عاجل");
            popup.getMenu().add("ملغي");
            popup.getMenu().add("مغلق");

            popup.setOnMenuItemClickListener(item -> {
                String newStatus = item.getTitle().toString();
                FirebaseDatabase.getInstance().getReference("Requests")
                        .child(m.requestId).child("status").setValue(newStatus)
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "تم تحديث الحالة لـ " + newStatus, Toast.LENGTH_SHORT).show());
                return true;
            });
            popup.show();
        });


        holder.btnDelete.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("Requests")
                    .child(m.requestId).removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "تم حذف الطلب بنجاح", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class VH extends RecyclerView.ViewHolder {
        TextView tvBloodType, tvStatusBadge, tvUnits, tvDept, tvCity, tvDateTime, tvDonatedTag;
        Button btnDelete, btnChangeStatus;

        public VH(@NonNull View v) {
            super(v);
            tvBloodType = v.findViewById(R.id.tvBloodType);
            tvStatusBadge = v.findViewById(R.id.tvStatusBadge);
            tvUnits = v.findViewById(R.id.tvUnits);
            tvDept = v.findViewById(R.id.tvDepartment);
            tvCity = v.findViewById(R.id.tvCity);
            tvDateTime = v.findViewById(R.id.tvDateTime);
            tvDonatedTag = v.findViewById(R.id.tvDonatedTag);
            btnDelete = v.findViewById(R.id.btnDelete);
            btnChangeStatus = v.findViewById(R.id.btnChangeStatus);
        }
    }
}