package com.example.vivalink;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class BloodBankStaffHelper {

    private DatabaseReference dbRef;

    public BloodBankStaffHelper() {

        dbRef = FirebaseDatabase.getInstance().getReference("BloodBankStaff");
    }


    public Task<Void> addStaff(String userId, BloodBankStaff staff) {
        return dbRef.child(userId).setValue(staff);
    }


    public DatabaseReference getStaffReference(String userId) {
        return dbRef.child(userId);
    }


    public Task<Void> updateStaffField(String userId, String fieldName, Object value) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(fieldName, value);
        return dbRef.child(userId).updateChildren(map);
    }


    public Task<Void> deleteStaff(String userId) {
        return dbRef.child(userId).removeValue();
    }


    public Query getAllStaff() {
        return dbRef;
    }


    public Query getStaffByCity(String city) {
        return dbRef.orderByChild("city").equalTo(city);
    }


    public Query getStaffByHospital(String hospitalId) {
        return dbRef.orderByChild("hospitalId").equalTo(hospitalId);
    }
}