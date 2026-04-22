package com.example.vivalink;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DonorsHelper {

    private DatabaseReference dbRef;

    public DonorsHelper() {
        dbRef = FirebaseDatabase.getInstance().getReference("Donors");
    }

    // إضافة متبرع جديد باستخدام uid كـ key
    public Task<Void> addDonor(Donors donor) {
        return dbRef.child(donor.getUid()).setValue(donor);
    }

    // جلب كل المتبرعين
    public Query getAllDonors() {
        return dbRef;
    }

    // جلب متبرع حسب الـ uid
    public Query getDonorById(String donorId) {
        return dbRef.child(donorId);
    }

    // تحديث بيانات متبرع كامل
    public Task<Void> updateDonor(String donorId, Donors updatedDonor) {
        return dbRef.child(donorId).setValue(updatedDonor);
    }

    // حذف متبرع
    public Task<Void> deleteDonor(String donorId) {
        return dbRef.child(donorId).removeValue();
    }

    // جلب المتبرعين حسب المدينة
    public Query getDonorsByCity(String city) {
        return dbRef.orderByChild("city").equalTo(city);
    }

    // جلب المتبرعين حسب فصيلة الدم
    public Query getDonorsByBloodType(String bloodType) {
        return dbRef.orderByChild("bloodType").equalTo(bloodType);
    }

    // جلب المتبرعين حسب حالة فحص الدم
    public Query getDonorsByBloodTestStatus(String status) {
        return dbRef.orderByChild("bloodTestStatus").equalTo(status);
    }

    // تحديث حالة فحص الدم فقط
    public Task<Void> updateBloodTestStatus(String donorId, String status) {
        return dbRef.child(donorId).child("bloodTestStatus").setValue(status);
    }

    // تحديث رابط إثبات الفحص فقط
    public Task<Void> updateBloodTestProofUrl(String donorId, String proofUrl) {
        return dbRef.child(donorId).child("bloodTestProofUrl").setValue(proofUrl);
    }

    // تحديث وقت إرسال الفحص فقط
    public Task<Void> updateBloodTestSubmittedAt(String donorId, String submittedAt) {
        return dbRef.child(donorId).child("bloodTestSubmittedAt").setValue(submittedAt);
    }

    // ✅ تحديث حالة الأهلية فقط
    public Task<Void> updateEligibility(String donorId, boolean isEligible) {
        return dbRef.child(donorId).child("isEligible").setValue(isEligible);
    }

    // ✅ جلب المتبرعين حسب الأهلية
    public Query getDonorsByEligibility(boolean isEligible) {
        return dbRef.orderByChild("isEligible").equalTo(isEligible);
    }

    // التحقق من صحة بيانات المتبرع
    public static String validateDonor(String fullName, String phone, String bloodType, String city) {
        if (fullName == null || fullName.trim().isEmpty()) return "اسم المتبرع مطلوب ✅";
        if (phone == null || phone.trim().isEmpty()) return "رقم الهاتف مطلوب ✅";
        if (bloodType == null || bloodType.trim().isEmpty() || bloodType.equals("اختر فصيلة الدم"))
            return "يرجى تحديد فصيلة الدم ✅";
        if (city == null || city.trim().isEmpty()) return "يرجى تحديد المدينة ✅";

        return null;
    }

    // ترجمة فصيلة الدم للعرض
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
