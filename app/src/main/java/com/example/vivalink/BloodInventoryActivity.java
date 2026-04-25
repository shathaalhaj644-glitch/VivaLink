package com.example.vivalink;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;

public class BloodInventoryActivity extends AppCompatActivity {
    private TabLayout tabs;
    private RecyclerView rv;
    private DatabaseReference db;
    private String uid, hName, hCity;
    private List<BloodInventoryModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_blood_inventory);

        uid = FirebaseAuth.getInstance().getUid();
        db = FirebaseDatabase.getInstance().getReference();
        tabs = findViewById(R.id.tabLayout);
        rv = findViewById(R.id.recyclerViewInventory);

        tabs.addTab(tabs.newTab().setText("مخزوني"));
        tabs.addTab(tabs.newTab().setText("المستشفيات"));
        tabs.addTab(tabs.newTab().setText("طلبات"));

        // جلب معلومات المستشفى الحالية أولاً للهيدر
        db.child("Hospitals").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                hName = s.child("hospitalName").getValue(String.class);
                hCity = s.child("city").getValue(String.class);
                load(0); // التحميل الابتدائي بعد جلب البيانات
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab t) { load(t.getPosition()); }
            @Override public void onTabUnselected(TabLayout.Tab t) {}
            @Override public void onTabReselected(TabLayout.Tab t) {}
        });
    }

    private void load(int tab) {
        list.clear();
        if (tab == 0) {
            rv.setLayoutManager(new GridLayoutManager(this, 2));
            String[] types = {"A+","A-","B+","B-","O+","O-","AB+","AB-"};
            for (String t : types) {
                db.child("BloodInventory").child(uid).child(t).addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot s) {
                        BloodInventoryModel m = new BloodInventoryModel();
                        m.bloodType = t;
                        m.units = s.child("units").getValue(Integer.class) == null ? 0 : s.child("units").getValue(Integer.class);
                        db.child("BloodInventoryThresholds").child(uid).child(t).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override public void onDataChange(@NonNull DataSnapshot s2) {
                                m.threshold = s2.getValue(Integer.class) == null ? 0 : s2.getValue(Integer.class);
                                updateList(m);
                                refresh(tab);
                            }
                            @Override public void onCancelled(@NonNull DatabaseError e) {}
                        });
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });
            }
        } else if (tab == 1) {
            rv.setLayoutManager(new LinearLayoutManager(this));
            db.child("BloodInventory").addValueEventListener(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot s) {
                    list.clear();
                    for (DataSnapshot h : s.getChildren()) {
                        if (h.getKey().equals(uid)) continue;
                        for (DataSnapshot t : h.getChildren()) {
                            BloodInventoryModel m = new BloodInventoryModel();
                            m.hospitalId = h.getKey();
                            m.bloodType = t.getKey();
                            m.units = t.child("units").getValue(Integer.class) == null ? 0 : t.child("units").getValue(Integer.class);
                            db.child("Hospitals").child(h.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override public void onDataChange(@NonNull DataSnapshot s2) {
                                    m.hospitalName = s2.child("hospitalName").getValue(String.class);
                                    m.city = s2.child("city").getValue(String.class);
                                    list.add(m);
                                    refresh(tab);
                                }
                                @Override public void onCancelled(@NonNull DatabaseError e) {}
                            });
                        }
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError e) {}
            });
        } else {
            rv.setLayoutManager(new LinearLayoutManager(this));
            db.child("BloodTransferRequests").orderByChild("toHospitalId").equalTo(uid).addValueEventListener(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot s) {
                    list.clear();
                    for (DataSnapshot d : s.getChildren()) {
                        if ("pending".equals(d.child("status").getValue())) {
                            BloodInventoryModel m = d.getValue(BloodInventoryModel.class);
                            m.requestId = d.getKey();
                            // جلب مدينة المستشفى الطالب للتاب الثالث
                            db.child("Hospitals").child(m.fromHospitalId != null ? m.fromHospitalId : "").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot s3) {
                                    m.city = s3.child("city").getValue(String.class);
                                    list.add(m);
                                    refresh(tab);
                                }
                                @Override public void onCancelled(@NonNull DatabaseError e) {}
                            });
                        }
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError e) {}
            });
        }
    }

    private void updateList(BloodInventoryModel m) {
        for (int i=0; i<list.size(); i++) {
            if (list.get(i).bloodType.equals(m.bloodType)) {
                list.set(i, m);
                return;
            }
        }
        list.add(m);
    }

    private void refresh(int tab) {
        rv.setAdapter(new BloodInventoryAdapter(list, tab, hName, hCity, new BloodInventoryAdapter.Listener() {
            @Override public void onMyClick(BloodInventoryModel m) { openDialog(m, true); }
            @Override public void onRequest(BloodInventoryModel m) { openDialog(m, false); }
            @Override public void onAccept(BloodInventoryModel m) { db.child("BloodTransferRequests").child(m.requestId).child("status").setValue("مقبول"); }
            @Override public void onReject(BloodInventoryModel m) { db.child("BloodTransferRequests").child(m.requestId).child("status").setValue("مرفوض"); }
        }));
    }

    private void openDialog(BloodInventoryModel m, boolean isMine) {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_blood_action);

        TextView title = d.findViewById(R.id.tv_title);
        TextView tvDetails = d.findViewById(R.id.tv_dialog_details);
        EditText etU = d.findViewById(R.id.et_units);
        EditText etR = d.findViewById(R.id.et_reserve);
        Button btnOk = d.findViewById(R.id.btn_ok);
        Button btnCancel = d.findViewById(R.id.btn_cancel);

        if (isMine) {
            title.setText("تعديل " + m.bloodType);
            tvDetails.setVisibility(View.GONE);
            etU.setText(String.valueOf(m.units));
            etR.setText(String.valueOf(m.threshold));
            etR.setVisibility(View.VISIBLE);
            btnOk.setText("تحديث");
        } else {
            title.setText("طلب نقل دم من " + m.hospitalName);
            tvDetails.setVisibility(View.VISIBLE);
            tvDetails.setText("الفصيلة: " + m.bloodType + "\nالمتوفر حالياً: " + m.units + " وحدة");
            etR.setVisibility(View.GONE);
            etU.setHint("أدخل عدد الوحدات المطلوبة");
            btnOk.setText("إرسال الطلب");
        }

        btnCancel.setOnClickListener(v -> d.dismiss());

        btnOk.setOnClickListener(v -> {
            String val = etU.getText().toString();
            if (val.isEmpty()) return;

            if (isMine) {
                db.child("BloodInventory").child(uid).child(m.bloodType).child("units").setValue(Integer.parseInt(val));
                db.child("BloodInventoryThresholds").child(uid).child(m.bloodType).setValue(Integer.parseInt(etR.getText().toString()));
            } else {
                HashMap<String,Object> map = new HashMap<>();
                map.put("fromHospitalId", uid);
                map.put("fromHospitalName", hName);
                map.put("toHospitalId", m.hospitalId);
                map.put("bloodType", m.bloodType);
                map.put("requestedUnits", Integer.parseInt(val));
                map.put("status", "pending");
                db.child("BloodTransferRequests").push().setValue(map);
            }
            d.dismiss();
        });
        d.show();
    }
}