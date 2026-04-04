package com.example.vivalink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
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

        View v = LayoutInflater.from(context).inflate(R.layout.item_hospital_request, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        HospitalRequestModel m = list.get(position);


        holder.tvBloodType.setText(m.bloodType);
        holder.tvStatusBadge.setText(m.status);
        holder.tvUnits.setText("عدد الوحدات: " + m.units);
        holder.tvDept.setText("القسم: " + m.department);
        holder.tvCity.setText("المدينة: " + m.city);


        if ("ملغي".equals(m.status) || "مغلق".equals(m.status)) {
            holder.btnEdit.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
        }


        holder.btnChangeStatus.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.getMenu().add("مفتوح");
            popup.getMenu().add("عاجل");
            popup.getMenu().add("مغلق");
            popup.getMenu().add("ملغي");

            popup.setOnMenuItemClickListener(item -> {
                String selectedStatus = item.getTitle().toString();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Requests").child(m.requestId);

                ref.child("status").setValue(selectedStatus).addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "تم التحديث لـ " + selectedStatus, Toast.LENGTH_SHORT).show();

                });
                return true;
            });
            popup.show();
        });


        holder.btnEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("تعديل الطلب ✏️");

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(60, 40, 60, 20);

            final EditText etUnits = new EditText(context);
            etUnits.setHint("عدد الوحدات");
            etUnits.setText(m.units);
            layout.addView(etUnits);

            final EditText etDept = new EditText(context);
            etDept.setHint("القسم");
            etDept.setText(m.department);
            layout.addView(etDept);

            builder.setView(layout);

            builder.setPositiveButton("حفظ التعديل", (dialog, which) -> {
                String u = etUnits.getText().toString();
                String d = etDept.getText().toString();

                HashMap<String, Object> updates = new HashMap<>();
                updates.put("units", u);
                updates.put("department", d);

                FirebaseDatabase.getInstance().getReference("Requests")
                        .child(m.requestId).updateChildren(updates)
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show());
            });

            builder.setNegativeButton("إلغاء", null);
            builder.show();
        });


        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("حذف")
                    .setMessage("هل أنت متأكد؟")
                    .setPositiveButton("نعم", (d, w) -> {
                        FirebaseDatabase.getInstance().getReference("Requests").child(m.requestId).removeValue();
                    })
                    .setNegativeButton("لا", null).show();
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class VH extends RecyclerView.ViewHolder {
        TextView tvBloodType, tvStatusBadge, tvUnits, tvDept, tvCity;
        Button btnDelete, btnEdit, btnChangeStatus;

        public VH(@NonNull View v) {
            super(v);

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