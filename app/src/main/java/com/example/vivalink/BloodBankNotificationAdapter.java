package com.example.vivalink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BloodBankNotificationAdapter extends RecyclerView.Adapter<BloodBankNotificationAdapter.VH> {

    List<BloodBankNotificationModel> list;

    public BloodBankNotificationAdapter(List<BloodBankNotificationModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
        // تم تعديل السطر ليتطابق مع اسم الملف اللي اخترتيه
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_bloodbank_notification, p, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        BloodBankNotificationModel m = list.get(i);
        h.title.setText(m.title);
        h.msg.setText(m.message);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class VH extends RecyclerView.ViewHolder {
        TextView title, msg;
        VH(View v) {
            super(v);
            // تم تعديل الـ IDs لتطابق الأسماء اللي في ملف الـ XML
            title = v.findViewById(R.id.notifTitle);
            msg = v.findViewById(R.id.notifMessage);
        }
    }
}