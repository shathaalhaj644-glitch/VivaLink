package com.example.vivalink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BloodBankNotificationAdapter extends RecyclerView.Adapter<BloodBankNotificationAdapter.ViewHolder> {

    private List<BloodBankNotificationModel> list;

    public BloodBankNotificationAdapter(List<BloodBankNotificationModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bloodbank_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodBankNotificationModel model = list.get(position);
        if (model != null) {
            holder.tvTitle.setText(model.getTitle());
            holder.tvMessage.setText(model.getMessage());
            holder.imgIcon.setImageResource(android.R.drawable.ic_dialog_info);
        }
    }

    @Override
    public int getItemCount() { return list != null ? list.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage;
        ImageView imgIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvMessage = itemView.findViewById(R.id.tvNotifMessage);
            imgIcon = itemView.findViewById(R.id.imgNotifIcon);
        }
    }
}