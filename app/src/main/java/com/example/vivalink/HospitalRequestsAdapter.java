package com.example.vivalink;

import android.content.Context;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class HospitalRequestsAdapter extends RecyclerView.Adapter<HospitalRequestsAdapter.VH> {
    Context context;
    List<HospitalRequestModel> list;

    public HospitalRequestsAdapter(Context context, List<HospitalRequestModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // تأكد أن اسم الملف هنا هو نفس اسم ملف الـ XML اللي صممناه
        View v = LayoutInflater.from(context).inflate(R.layout.item_hospital_request, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        HospitalRequestModel m = list.get(position);

        // ربط البيانات بالعناصر الجديدة
        holder.tvBloodType.setText(m.bloodType);
        holder.tvStatusBadge.setText(m.status);
        holder.tvUnits.setText("عدد الوحدات: " + m.units);
        holder.tvDept.setText("القسم: " + m.department);
        holder.tvCity.setText("المدينة: " + m.city);

        // برمجة زر الحذف كمثال (عشان تحس بفرق الباك إند)
        holder.btnDelete.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("Requests")
                    .child(m.requestId).removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "تم الحذف", Toast.LENGTH_SHORT).show());
        });

        // برمجة زر التعديل أو الحالة (ممكن تضيفهم لاحقاً)
    }

    @Override
    public int getItemCount() { return list.size(); }

    class VH extends RecyclerView.ViewHolder {
        // تعريف العناصر بالأسماء الموجودة في الـ XML الجديد
        TextView tvBloodType, tvStatusBadge, tvUnits, tvDept, tvCity;
        Button btnDelete, btnEdit, btnChangeStatus;

        public VH(@NonNull View v) {
            super(v);

            // الربط بالـ IDs اللي حطيناها في الـ XML
            tvBloodType = v.findViewById(R.id.tvBloodType);
            tvStatusBadge = v.findViewById(R.id.tvStatusBadge);
            tvUnits = v.findViewById(R.id.tvUnits);
            tvDept = v.findViewById(R.id.tvDept);
            tvCity = v.findViewById(R.id.tvCity);

            btnDelete = v.findViewById(R.id.btnDelete);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnChangeStatus = v.findViewById(R.id.btnChangeStatus);
        }
    }
}