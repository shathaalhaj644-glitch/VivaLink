package com.example.vivalink;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class HospitalsHelper {

    private DatabaseReference dbRef;

    public HospitalsHelper() {
        // المرجع في قاعدة البيانات
        dbRef = FirebaseDatabase.getInstance().getReference("Hospitals");
    }

    // --- 1. العمليات الأساسية ---

    // إضافة مستشفى جديد
    public Task<Void> addHospital(Hospitals hospital) {
        return dbRef.child(hospital.getHospitalId()).setValue(hospital);
    }

    // جلب كل المستشفيات
    public Query getAllHospitals() {
        return dbRef;
    }

    // جلب مستشفى محدد
    public Query getHospitalById(String hospitalId) {
        return dbRef.child(hospitalId);
    }

    // تحديث بيانات المستشفى
    public Task<Void> updateHospital(String hospitalId, Hospitals updatedHospital) {
        return dbRef.child(hospitalId).setValue(updatedHospital);
    }

    // حذف مستشفى
    public Task<Void> deleteHospital(String hospitalId) {
        return dbRef.child(hospitalId).removeValue();
    }

    // --- 2. عمليات البحث (Search) ---

    // البحث حسب المدينة (تعديل: نستخدم city بدلاً من address)
    public Query getHospitalsByCity(String city) {
        return dbRef.orderByChild("city").equalTo(city);
    }

    // --- 3. منطق الفحص (Validation) ---

    // التحقق من صحة البيانات
    public static String validateHospital(String hospitalName, String city, String contactPhone, String email) {
        if (hospitalName == null || hospitalName.trim().isEmpty()) return "اسم المستشفى مطلوب ✅";
        if (city == null || city.trim().isEmpty()) return "المدينة مطلوبة ✅";
        if (contactPhone == null || contactPhone.trim().isEmpty()) return "رقم التواصل مطلوب ✅";
        if (email == null || email.trim().isEmpty()) return "البريد الإلكتروني مطلوب ✅";

        return null; // البيانات سليمة
    }
}