package com.example.vivalink;

import com.example.vivalink.Hospitals;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class HospitalsHelper {

    private DatabaseReference dbRef;

    public HospitalsHelper() {
        // إنشاء مرجع لجدول المستشفيات في Firebase تحت اسم "Hospitals"
        dbRef = FirebaseDatabase.getInstance().getReference("Hospitals");
    }

    // --- 1. عمليات قاعدة البيانات الأساسية (CRUD) ---

    // إضافة مستشفى جديد ✅
    public Task<Void> addHospital(Hospitals hospital) {
        return dbRef.child(hospital.getHospitalId()).setValue(hospital);
    }

    // جلب كل المستشفيات ✅
    public Query getAllHospitals() {
        return dbRef;
    }

    // جلب مستشفى محدد باستخدام الـ ID ✅
    public Query getHospitalById(String hospitalId) {
        return dbRef.child(hospitalId);
    }

    // تحديث بيانات المستشفى بالكامل ✅
    public Task<Void> updateHospital(String hospitalId, Hospitals updatedHospital) {
        return dbRef.child(hospitalId).setValue(updatedHospital);
    }

    // حذف مستشفى ✅
    public Task<Void> deleteHospital(String hospitalId) {
        return dbRef.child(hospitalId).removeValue();
    }

    // --- 2. عمليات البحث المتقدم (Advanced Search) ---

    // البحث عن مستشفيات حسب المدينة 📍
    public Query getHospitalsByCity(String city) {
        return dbRef.orderByChild("address").equalTo(city);
    }

    // البحث عن المستشفيات الموثقة فقط ✅
    public Query getVerifiedHospitals() {
        return dbRef.orderByChild("verified").equalTo(true);
    }

    // --- 3. منطق الفحص والتنسيق (Logic & Validation) ---

    // التحقق من صحة بيانات المستشفى قبل رفعها ✅
    public static String validateHospital(String hospitalName, String address, String contactPhone) {
        if (hospitalName == null || hospitalName.trim().isEmpty()) return "اسم المستشفى مطلوب ✅";
        if (address == null || address.trim().isEmpty()) return "العنوان مطلوب ✅";
        if (contactPhone == null || contactPhone.trim().isEmpty()) return "رقم التواصل مطلوب ✅";

        return null; // البيانات سليمة
    }

    // دالة لتنسيق حالة التوثيق بالعربي ✅
    public static String getVerificationLabel(boolean verified) {
        return verified ? "موثق ✅" : "غير موثق ❌";
    }
}
