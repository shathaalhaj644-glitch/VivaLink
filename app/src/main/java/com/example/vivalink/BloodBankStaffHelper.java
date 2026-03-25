package com.example.vivalink;

import com.example.vivalink.BloodBankStaff;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class BloodBankStaffHelper {

    private DatabaseReference dbRef;

    public BloodBankStaffHelper() {
        // الوصول إلى جدول الموظفين في الفايربيس تحت اسم "Staff"
        dbRef = FirebaseDatabase.getInstance().getReference("Staff");
    }

    // 1. إضافة موظف جديد ✅
    public Task<Void> addStaff(BloodBankStaff staff) {
        return dbRef.child(staff.getStaffId()).setValue(staff);
    }

    // 2. جلب بيانات موظف معين باستخدام الـ ID ✅
    public Query getStaffById(String staffId) {
        return dbRef.child(staffId);
    }

    // 3. تحديث بيانات الموظف ✅
    public Task<Void> updateStaff(String staffId, BloodBankStaff staff) {
        return dbRef.child(staffId).setValue(staff);
    }

    // 4. حذف موظف ✅
    public Task<Void> deleteStaff(String staffId) {
        return dbRef.child(staffId).removeValue();
    }

    // 5. جلب جميع الموظفين ✅
    public Query getAllStaff() {
        return dbRef;
    }
}

