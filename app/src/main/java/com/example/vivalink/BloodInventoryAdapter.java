package com.example.vivalink;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BloodInventoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BloodInventoryModel> list;
    private int tab;
    private String hName, hCity;
    private Listener listener;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public interface Listener {
        void onMyClick(BloodInventoryModel m);
        void onRequest(BloodInventoryModel m);
        void onAccept(BloodInventoryModel m);
        void onReject(BloodInventoryModel m);
    }

    public BloodInventoryAdapter(List<BloodInventoryModel> list, int tab, String hName, String hCity, Listener l) {
        this.list = list;
        this.tab = tab;
        this.hName = hName;
        this.hCity = hCity;
        this.listener = l;
    }

    @Override
    public int getItemViewType(int position) {
        if (tab == 0 && position == 0) return TYPE_HEADER;
        return TYPE_ITEM;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int viewType) {
        if (viewType == TYPE_HEADER) {
            LinearLayout header = new LinearLayout(p.getContext());
            header.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            header.setOrientation(LinearLayout.VERTICAL);
            header.setBackgroundColor(Color.parseColor("#D32F2F"));
            header.setPadding(60, 60, 60, 60);

            TextView title = new TextView(p.getContext());
            title.setText("مخزون الدم الفائض");
            title.setTextColor(Color.WHITE);
            title.setTextSize(20);
            title.setTypeface(null, Typeface.BOLD);

            TextView info = new TextView(p.getContext());
            info.setTextColor(Color.WHITE);
            info.setTextSize(14);
            header.addView(title);
            header.addView(info);

            return new HeaderVH(header, info);
        }
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_blood_inventory, p, false);
        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof HeaderVH) {
            ((HeaderVH) holder).tvInfo.setText(hName + " - " + hCity);
        } else {
            int pos = (tab == 0) ? i - 1 : i;
            BloodInventoryModel m = list.get(pos);
            ItemVH v = (ItemVH) holder;
            v.reset();

            if (tab == 0) {
                v.type.setText(m.bloodType);
                v.units.setVisibility(View.VISIBLE);
                v.units.setText("الوحدات: " + (m.units + m.threshold));
                v.layoutInfo.setGravity(Gravity.CENTER);
                v.layoutInfo.setVisibility(View.VISIBLE);
                v.itemView.setOnClickListener(x -> {
                    if (listener != null) listener.onMyClick(m);
                });

            } else if (tab == 1) {
                v.layoutInfo.setVisibility(View.VISIBLE);
                v.btn.setVisibility(View.VISIBLE);
                v.name.setText(m.hospitalName);
                v.city.setText(m.city);
                v.units.setText(m.units + " وحدة متاحة");
                v.type.setText(m.bloodType);
                v.btn.setOnClickListener(x -> {
                    if (listener != null) listener.onRequest(m);
                });

            } else {
                v.layoutInfo.setVisibility(View.VISIBLE);
                v.name.setText("طلب من: " + m.fromHospitalName);
                v.city.setText("المدينة: " + (m.city != null ? m.city : "جاري التحميل.."));
                v.type.setText(m.bloodType);
                v.units.setText("الكمية المطلوبة: " + m.requestedUnits);


                if ("مقبول".equals(m.status)) {

                    v.layoutActions.setVisibility(View.VISIBLE);
                    v.accept.setVisibility(View.VISIBLE);
                    v.reject.setVisibility(View.GONE);


                    v.accept.setText("تم قبول هذا الطلب");
                    v.accept.setBackgroundColor(Color.parseColor("#4CAF50"));
                    v.accept.setEnabled(false);
                    v.accept.setTextColor(Color.WHITE);

                } else if ("مرفوض".equals(m.status)) {

                    v.layoutActions.setVisibility(View.VISIBLE);
                    v.accept.setVisibility(View.GONE);
                    v.reject.setVisibility(View.VISIBLE);


                    v.reject.setText("تم رفض هذا الطلب");
                    v.reject.setBackgroundColor(Color.parseColor("#F44336"));
                    v.reject.setEnabled(false);
                    v.reject.setTextColor(Color.WHITE);

                } else {

                    v.layoutActions.setVisibility(View.VISIBLE);
                    v.accept.setVisibility(View.VISIBLE);
                    v.reject.setVisibility(View.VISIBLE);


                    v.accept.setText("قبول");
                    v.accept.setBackgroundColor(Color.parseColor("#4CAF50"));
                    v.accept.setEnabled(true);

                    v.reject.setText("رفض");
                    v.reject.setBackgroundColor(Color.parseColor("#D32F2F"));
                    v.reject.setEnabled(true);


                    v.accept.setOnClickListener(x -> {
                        if (listener != null) listener.onAccept(m);
                    });

                    v.reject.setOnClickListener(x -> {
                        if (listener != null) listener.onReject(m);
                    });
                }
            }
        }
    }

    @Override public int getItemCount() { return (tab == 0) ? list.size() + 1 : list.size(); }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView rv) {
        super.onAttachedToRecyclerView(rv);
        if (rv.getLayoutManager() instanceof GridLayoutManager) {
            ((GridLayoutManager) rv.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override public int getSpanSize(int p) { return (tab == 0 && p == 0) ? 2 : 1; }
            });
        }
    }

    class HeaderVH extends RecyclerView.ViewHolder {
        TextView tvInfo;
        HeaderVH(View v, TextView info) { super(v); tvInfo = info; }
    }

    class ItemVH extends RecyclerView.ViewHolder {
        TextView type, units, name, city; Button btn, accept, reject;
        LinearLayout layoutActions, layoutInfo;
        ItemVH(View v) { super(v);
            type = v.findViewById(R.id.tv_type); units = v.findViewById(R.id.tv_units);
            name = v.findViewById(R.id.tv_name); city = v.findViewById(R.id.tv_city);
            btn = v.findViewById(R.id.btn); accept = v.findViewById(R.id.btn_accept);
            reject = v.findViewById(R.id.btn_reject); layoutActions = v.findViewById(R.id.layout_actions);
            layoutInfo = v.findViewById(R.id.layout_text_info); }
        void reset() {
            layoutInfo.setVisibility(View.GONE);
            btn.setVisibility(View.GONE);
            layoutActions.setVisibility(View.GONE);
            units.setTextColor(Color.BLACK);
        }
    }
}