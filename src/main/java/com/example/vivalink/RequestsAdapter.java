package com.example.vivalink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    private List<RequestModel> requestList;
    private Context context;

    // 1. Constructor لاستقبال البيانات والسياق
    public RequestsAdapter(List<RequestModel> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // تأكدي أن اسم الملف في مجلد layout هو item_blood_request.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_blood_requests, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestModel model = requestList.get(position);

        // 2. ربط البيانات بالعناصر (استخدام Getters من الموديل الخاص بكِ)
        holder.tvHospital.setText(model.getHospitalName());
        holder.tvBloodType.setText("الفصيلة المطلوبة: " + model.getBloodType());
        holder.tvLocation.setText("المدينة: " + model.getCity());
        holder.tvDept.setText("القسم: " + model.getDepartment());
        holder.tvUnits.setText("عدد الوحدات: " + model.getUnits());

        // 3. برمجة زر "تبرع الآن" لإظهار رسالة تأكيد
        holder.btnDonate.setOnClickListener(v -> {
            showConfirmationDialog();
        });
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(context)
                .setTitle("تأكيد التبرع")
                .setMessage("هل أنت متأكد من رغبتك في التبرع لهذا الطلب؟")
                .setPositiveButton("تأكيد", (dialog, which) -> {
                    // هنا تظهر رسالة النجاح
                    Toast.makeText(context, "تم إرسال عرض تبرعك بنجاح! شكراً لك.", Toast.LENGTH_LONG).show();

                    /* ملاحظة: هنا يمكنك إضافة كود للانتقال لصفحة المواعيد DonateActivity إذا أردتِ
                       Intent intent = new Intent(context, DonateActivity.class);
                       context.startActivity(intent);
                    */
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    // 4. كلاس ViewHolder لتعريف العناصر الموجودة في الـ XML
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHospital, tvBloodType, tvLocation, tvDept, tvUnits;
        Button btnDonate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // هذه الـ IDs يجب أن تطابق تماماً ما هو موجود في ملف item_blood_request.xml
            tvHospital = itemView.findViewById(R.id.tvHospitalItem);
            tvBloodType = itemView.findViewById(R.id.tvBloodTypeItem);
            tvLocation = itemView.findViewById(R.id.tvLocationItem);
            tvDept = itemView.findViewById(R.id.tvDepartmentItem);
            tvUnits = itemView.findViewById(R.id.tvUnitsItem);
            btnDonate = itemView.findViewById(R.id.btnDonateNow);
        }
    }
}