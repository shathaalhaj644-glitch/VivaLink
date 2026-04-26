package com.example.vivalink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// تم تعديل النوع المستهدف في الأدابتر إلى Notifications
public class BloodBankNotificationAdapter extends RecyclerView.Adapter<BloodBankNotificationAdapter.ViewHolder> {

    private List<Notifications> list;

    // تم تعديل الكونستراكتور ليتوافق مع الموديل الجديد
    public BloodBankNotificationAdapter(List<Notifications> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // تأكدي أن اسم الملف item_bloodbank_notification هو نفسه اسم ملف الـ XML عندك
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bloodbank_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // استخدام كلاس Notifications الموحد
        Notifications model = list.get(position);

        // ربط البيانات باستخدام الـ Getters (لأن المتغيرات private)
        holder.tvTitle.setText(model.getTitle());
        holder.tvMessage.setText(model.getMessage());

        // المنطق الخاص بالأيقونات بناءً على النوع (Type)
        String type = (model.getNotificationId() != null) ? "new_request" : "default";
        // ملاحظة: بما أن كلاس Notifications الأساسي قد لا يحتوي على حقل type صريح،
        // سنستخدم منطقاً افتراضياً أو يمكنك إضافة getType() للكلاس إذا كان موجوداً.

        // إذا أضفتِ حقل type للموديل، استدعيه هنا: model.getType()
        holder.imgIcon.setImageResource(android.R.drawable.ic_dialog_info);
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage;
        ImageView imgIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // مطابقة الـ IDs مع الـ XML (تأكدي أن هذه الأسماء مطابقة تماماً لملف الـ Layout)
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvMessage = itemView.findViewById(R.id.tvNotifMessage);
            imgIcon = itemView.findViewById(R.id.imgNotifIcon);
        }
    }
}