package com.example.vivalink;

import com.example.vivalink.Donors;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DonorsHelper {

    private DatabaseReference dbRef;

    public DonorsHelper() {
        // إنشاء مرجع لجدول المتبرعين في Firebase تحت اسم "Donors"
        dbRef = FirebaseDatabase.getInstance().getReference("Donors");
    }

    // --- 1. عمليات قاعدة البيانات الأساسية (CRUD) ---

    // إضافة متبرع جديد ✅
    public Task<Void> addDonor(Donors donor) {
        return dbRef.child(donor.getName()).setValue(donor);
    }

    // جلب كل المتبرعين ✅
    public Query getAllDonors() {
        return dbRef;
    }

    // جلب متبرع محدد باستخدام الـ ID ✅
    public Query getDonorById(String donorId) {
        return dbRef.child(donorId);
    }

    // تحديث بيانات المتبرع بالكامل ✅
    public Task<Void> updateDonor(String donorId, Donors updatedDonor) {
        return dbRef.child(donorId).setValue(updatedDonor);
    }

    // حذف متبرع ✅
    public Task<Void> deleteDonor(String donorId) {
        return dbRef.child(donorId).removeValue();
    }

    // --- 2. عمليات البحث المتقدم (Advanced Search) ---

    // البحث عن متبرعين حسب المدينة (مهم جداً للطلبات المحلية) 📍
    public Query getDonorsByCity(String city) {
        return dbRef.orderByChild("city").equalTo(city);
    }

    // البحث عن متبرعين حسب فصيلة الدم (مهم للحالات الطارئة) 🩸
    public Query getDonorsByBloodType(String bloodType) {
        return dbRef.orderByChild("bloodType").equalTo(bloodType);
    }

    // --- 3. منطق الفحص والتنسيق (Logic & Validation) ---

    // التحقق من صحة بيانات المتبرع قبل رفعها ✅
    public static String validateDonor(String fullName, String phone, String bloodType, String city) {
        if (fullName == null || fullName.trim().isEmpty()) return "اسم المتبرع مطلوب ✅";
        if (phone == null || phone.trim().isEmpty()) return "رقم الهاتف مطلوب ✅";
        if (bloodType == null || bloodType.trim().isEmpty() || bloodType.equals("اختر فصيلة الدم"))
            return "يرجى تحديد فصيلة الدم ✅";
        if (city == null || city.trim().isEmpty()) return "يرجى تحديد المدينة ✅";

        return null; // البيانات سليمة
    }

    // دالة لتنسيق عرض فصيلة الدم بالعربي ✅
    public static String getBloodTypeLabel(String bloodType) {
        if (bloodType == null) return "غير معروف";
        switch (bloodType) {
            case "A+": return "A موجب";
            case "A-": return "A سالب";
            case "B+": return "B موجب";
            case "B-": return "B سالب";
            case "AB+": return "AB موجب";
            case "AB-": return "AB سالب";
            case "O+": return "O موجب";
            case "O-": return "O سالب";
            default: return "غير معروف";
        }
    }
}