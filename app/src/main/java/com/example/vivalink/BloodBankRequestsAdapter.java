package com.example.vivalink;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BloodBankRequestsAdapter extends RecyclerView.Adapter<BloodBankRequestsAdapter.VH> {

    private List<BloodBankRequestsModel> list;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onCloseRequest(BloodBankRequestsModel request);
    }

    public BloodBankRequestsAdapter(List<BloodBankRequestsModel> list, OnRequestActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        // تأكدي أن اسم الـ XML هو item_bloodbank_request
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_bloodbank_request, p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int p) {
        BloodBankRequestsModel req = list.get(p);

        h.tvHospitalName.setText(req.getHospitalName());
        h.tvBloodType.setText("الفصيلة: " + req.getBloodType());
        h.tvUnits.setText("الوحدات: " + req.getUnits());
        h.tvDepartment.setText("القسم: " + req.getDepartment());

        // 🔥 التعديل هنا: استخدام getConfirmedAt() بدلاً من getDate()
        h.tvDate.setText(req.getConfirmedAt());

        if ("مفتوح".equals(req.getStatus())) {
            h.tvStatus.setText("● مفتوح");
            h.tvStatus.setBackgroundColor(0xFFFFF3E0); // برتقالي فاتح
            h.tvStatus.setTextColor(0xFFEF6C00);
            h.btnCloseRequest.setVisibility(View.VISIBLE);
        } else {
            h.tvStatus.setText("✓ مغلق");
            h.tvStatus.setBackgroundColor(0xFFE8F5E9); // أخضر فاتح
            h.tvStatus.setTextColor(0xFF2E7D32);
            h.btnCloseRequest.setVisibility(View.GONE);
        }

        h.btnCloseRequest.setOnClickListener(v -> listener.onCloseRequest(req));
    }

    @Override public int getItemCount() { return list.size(); }

    class VH extends RecyclerView.ViewHolder {
        TextView tvHospitalName, tvBloodType, tvUnits, tvDepartment, tvDate, tvStatus;
        Button btnCloseRequest;

        VH(View v) {
            super(v);
            tvHospitalName = v.findViewById(R.id.tvHospitalName);
            tvBloodType = v.findViewById(R.id.tvBloodType);
            tvUnits = v.findViewById(R.id.tvUnits);
            tvDepartment = v.findViewById(R.id.tvDepartment);
            tvDate = v.findViewById(R.id.tvDate);
            tvStatus = v.findViewById(R.id.tvStatus);
            btnCloseRequest = v.findViewById(R.id.btnCloseRequest);
        }
    }
}