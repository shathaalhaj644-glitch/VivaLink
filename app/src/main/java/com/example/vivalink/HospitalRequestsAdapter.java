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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        // 1. عرض البيانات الأساسية
        holder.tvBloodType.setText("🩸 " + m.bloodType);
        holder.tvUnits.setText("🧪 عدد الوحدات: " + m.units);
        holder.tvDept.setText("🏢 القسم: " + m.department);
        holder.tvCity.setText("📍 المدينة: " + m.city);
        holder.tvStatusBadge.setText("الحالة: " + m.status);
        holder.tvDateTime.setText("🕒 " + m.getFormattedDate());

        // 2. التحكم في ظهور علامة "تم التبرع"
        if ("مغلق".equals(m.status)) {
            holder.tvDonatedTag.setVisibility(View.VISIBLE);
            holder.tvDonatedTag.setText("تم التبرع ✅ (" + m.donatedCount + ")");
        } else {
            holder.tvDonatedTag.setVisibility(View.GONE);
        }

        // 3. تغيير حالة الطلب والمنطق التلقائي (الباك إند الذكي)
        holder.btnChangeStatus.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.getMenu().add("مفتوح");
            popup.getMenu().add("عاجل");
            popup.getMenu().add("ملغي");
            popup.getMenu().add("مغلق");

            popup.setOnMenuItemClickListener(item -> {
                String newStatus = item.getTitle().toString();

                // تحديث حالة الطلب في Firebase
                FirebaseDatabase.getInstance().getReference("Requests")
                        .child(m.requestId).child("status").setValue(newStatus)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "تم تحديث الحالة لـ " + newStatus, Toast.LENGTH_SHORT).show();

                            // *** المنطق المثالي: إذا أغلق الموظف الطلب، نحدث بيانات المتبرع فوراً ***
                            if ("مغلق".equals(newStatus)) {
                                if (m.donorId != null && !m.donorId.isEmpty() && !m.donorId.equals("null")) {
                                    updateDonorDataAutomatically(m.donorId);
                                } else {
                                    Toast.makeText(context, "تنبيه: لا يوجد متبرع مرتبط بهذا الطلب لتحديث بياناته", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                return true;
            });
            popup.show();
        });

        // 4. حذف الطلب
        holder.btnDelete.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("Requests")
                    .child(m.requestId).removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "تم حذف الطلب بنجاح", Toast.LENGTH_SHORT).show());
        });
    }

    // دالة التحديث التلقائي (Backend Transaction)
    private void updateDonorDataAutomatically(String donorId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
        DatabaseReference donorRef = FirebaseDatabase.getInstance().getReference("Donors").child(donorId);

        donorRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // جلب العداد الحالي من قاعدة البيانات
                String countStr = String.valueOf(mutableData.child("donationCount").getValue());
                int currentCount = 0;
                if (!countStr.equals("null") && !countStr.isEmpty()) {
                    currentCount = Integer.parseInt(countStr);
                }

                // تحديث البيانات تلقائياً في حساب المتبرع
                mutableData.child("lastDonation").setValue(today); // تاريخ اليوم
                mutableData.child("lastBloodTest").setValue(today); // تاريخ فحص اليوم
                mutableData.child("donationCount").setValue(String.valueOf(currentCount + 1)); // زيادة العداد
                mutableData.child("isEligible").setValue(false); // منعه من التبرع حالياً (لأنه تبرع للتو)

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (committed) {
                    Toast.makeText(context, "تم تحديث سجل المتبرع بنجاح ✅", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "فشل في تحديث بيانات المتبرع", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
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