package com.example.vivalink;

import android.content.Context;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HospitalDonorsAdapter extends RecyclerView.Adapter<HospitalDonorsAdapter.VH> {
    Context context;
    List<HospitalDonorsModel> list;

    public HospitalDonorsAdapter(Context context, List<HospitalDonorsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_hospital_donors, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        HospitalDonorsModel donor = list.get(position);
        holder.name.setText(donor.getFullName());
        holder.blood.setText(donor.getBloodType());
        holder.phone.setText(donor.getPhone());
        holder.city.setText(donor.getCity());
    }

    @Override
    public int getItemCount() { return list.size(); }

    class VH extends RecyclerView.ViewHolder {
        TextView name, blood, phone, city;
        public VH(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.tv_donor_name);
            blood = v.findViewById(R.id.tv_donor_blood);
            phone = v.findViewById(R.id.tv_donor_phone);
            city = v.findViewById(R.id.tv_donor_city);
        }
    }
}