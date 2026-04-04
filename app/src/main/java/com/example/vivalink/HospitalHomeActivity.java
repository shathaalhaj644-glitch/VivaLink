package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class HospitalHomeActivity extends AppCompatActivity {

    private TextView tvHospitalName, tvHospitalLocation, valTotalRequests, valPending, valDonors;
    private CardView btnCreateRequestCard, btnViewDonorsCard, btnSettingsCard;

    private DatabaseReference dbRef;
    private String currentHospitalUid;
    private String hospitalCity = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_home);


        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvHospitalLocation = findViewById(R.id.tvHospitalLocation);
        valTotalRequests = findViewById(R.id.valTotalRequests);
        valPending = findViewById(R.id.valPending);
        valDonors = findViewById(R.id.valDonors);

        btnCreateRequestCard = findViewById(R.id.btnCreateRequestCard);
        btnViewDonorsCard = findViewById(R.id.btnViewDonorsCard);
        btnSettingsCard = findViewById(R.id.btnSettingsCard);

        currentHospitalUid = FirebaseAuth.getInstance().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference();

        if (currentHospitalUid != null) {
            loadHospitalData();
        }


        btnCreateRequestCard.setOnClickListener(v ->
                startActivity(new Intent(this, HospitalRequestsActivity.class)));

        btnViewDonorsCard.setOnClickListener(v ->
                startActivity(new Intent(this, HospitalDonorsActivity.class)));

        btnSettingsCard.setOnClickListener(v ->
                startActivity(new Intent(this, HospitalSettingsActivity.class)));
    }


    private void loadHospitalData() {

        dbRef.child("Hospitals").child(currentHospitalUid)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            String name = snapshot.child("hospitalName").getValue(String.class);
                            hospitalCity = snapshot.child("city").getValue(String.class);

                            tvHospitalName.setText(name != null ? name : "اسم المستشفى");
                            tvHospitalLocation.setText(hospitalCity != null ? hospitalCity + " 📍" : "المدينة");

                            fetchRequests();
                            fetchDonors();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("HOME", error.getMessage());
                    }
                });
    }


    private void fetchRequests() {

        dbRef.child("Requests")
                .orderByChild("hospitalId")
                .equalTo(currentHospitalUid)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int total = (int) snapshot.getChildrenCount();
                        int pending = 0;

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String status = ds.child("status").getValue(String.class);

                            if (status != null && status.equalsIgnoreCase("مفتوح")) {
                                pending++;
                            }
                        }

                        valTotalRequests.setText(String.valueOf(total));
                        valPending.setText(String.valueOf(pending));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }


    private void fetchDonors() {

        if (hospitalCity == null || hospitalCity.isEmpty()) return;

        dbRef.child("Donors")
                .orderByChild("city")
                .equalTo(hospitalCity)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int count = (int) snapshot.getChildrenCount();

                        valDonors.setText(String.valueOf(count));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}