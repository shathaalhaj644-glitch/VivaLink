package com.example.vivalink;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class BloodBankStaffHelper {

    private DatabaseReference dbRef;

    public BloodBankStaffHelper() {
        // الربط مع الجدول الرئيسي للموظفين
        dbRef = FirebaseDatabase.getInstance().getReference("BloodBankStaff");
    }

    // إضافة أو تحديث بيانات الموظف
    public Task<Void> addStaff(String userId, BloodBankStaff staff) {
        return dbRef.child(userId).setValue(staff);
    }

    // جلب مرجع الموظف (أفضل من Query للتعامل المباشر)
    public DatabaseReference getStaffReference(String userId) {
        return dbRef.child(userId);
    }

    // تحديث حقل واحد فقط (مثلاً تحديث رقم الهاتف دون مسح باقي البيانات)
    public Task<Void> updateStaffField(String userId, String fieldName, Object value) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(fieldName, value);
        return dbRef.child(userId).updateChildren(map);
    }

    // حذف حساب موظف
    public Task<Void> deleteStaff(String userId) {
        return dbRef.child(userId).removeValue();
    }

    // جلب كل الموظفين
    public Query getAllStaff() {
        return dbRef;
    }

    // --- فلاتر البحث ---

    // البحث حسب المدينة
    public Query getStaffByCity(String city) {
        return dbRef.orderByChild("city").equalTo(city);
    }

    // البحث حسب المشفى (مهم جداً لمشروعك)
    public Query getStaffByHospital(String hospitalId) {
        return dbRef.orderByChild("hospitalId").equalTo(hospitalId);
    }
}