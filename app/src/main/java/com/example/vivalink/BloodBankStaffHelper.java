package com.example.vivalink;

import com.example.vivalink.BloodBankStaff;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class BloodBankStaffHelper {

    private DatabaseReference dbRef;

    public BloodBankStaffHelper() {

        dbRef = FirebaseDatabase.getInstance().getReference("Staff");
    }


    public Task<Void> addStaff(BloodBankStaff staff) {
        return dbRef.child(staff.getStaffId()).setValue(staff);
    }


    public Query getStaffById(String staffId) {
        return dbRef.child(staffId);
    }


    public Task<Void> updateStaff(String staffId, BloodBankStaff staff) {
        return dbRef.child(staffId).setValue(staff);
    }


    public Task<Void> deleteStaff(String staffId) {
        return dbRef.child(staffId).removeValue();
    }


    public Query getAllStaff() {
        return dbRef;
    }
}

