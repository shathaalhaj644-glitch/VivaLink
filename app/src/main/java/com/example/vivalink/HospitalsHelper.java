package com.example.vivalink;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class HospitalsHelper {

    private DatabaseReference dbRef;

    public HospitalsHelper() {

        dbRef = FirebaseDatabase.getInstance().getReference("Hospitals");
    }


    public Task<Void> addHospital(Hospitals hospital) {
        return dbRef.child(hospital.getHospitalId()).setValue(hospital);
    }


    public Query getAllHospitals() {
        return dbRef;
    }


    public Query getHospitalById(String hospitalId) {
        return dbRef.child(hospitalId);
    }


    public Task<Void> updateHospital(String hospitalId, Hospitals updatedHospital) {
        return dbRef.child(hospitalId).setValue(updatedHospital);
    }


    public Task<Void> deleteHospital(String hospitalId) {
        return dbRef.child(hospitalId).removeValue();
    }

    public Query getHospitalsByCity(String city) {
        return dbRef.orderByChild("city").equalTo(city);
    }

    public static String validateHospital(String hospitalName, String city, String contactPhone, String email) {
        if (hospitalName == null || hospitalName.trim().isEmpty()) return "اسم المستشفى مطلوب ✅";
        if (city == null || city.trim().isEmpty()) return "المدينة مطلوبة ✅";
        if (contactPhone == null || contactPhone.trim().isEmpty()) return "رقم التواصل مطلوب ✅";
        if (email == null || email.trim().isEmpty()) return "البريد الإلكتروني مطلوب ✅";

        return null;
    }
}