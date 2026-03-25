package com.example.vivalink;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DonationsHelper {

    private DatabaseReference dbRef;

    public DonationsHelper() {
        // إنشاء مرجع لجدول التبرعات في Firebase تحت اسم "Donations"
        dbRef = FirebaseDatabase.getInstance().getReference("Donations");
    }

    // --- 1. عمليات قاعدة البيانات (Database Operations) ---

    // إضافة تبرع جديد ✅
    public Task<Void> addDonation(Donations donation) {
        return dbRef.child(donation.getDonationId()).setValue(donation);
    }

    // جلب كل التبرعات ✅
    public Query getAllDonations() {
        return dbRef;
    }

    // جلب تبرع محدد باستخدام الـ ID ✅
    public Query getDonationById(String donationId) {
        return dbRef.child(donationId);
    }

    // تحديث حالة التبرع ✅
    public Task<Void> updateDonationStatus(String donationId, String newStatus) {
        return dbRef.child(donationId).child("status").setValue(newStatus);
    }

    // حذف تبرع ✅
    public Task<Void> deleteDonation(String donationId) {
        return dbRef.child(donationId).removeValue();
    }

    // --- 2. منطق الفحص والتنسيق (Logic & Validation) ---

    // التحقق من صحة بيانات التبرع قبل رفعها ✅
    public static String validateDonation(String donorId, String hospitalId, String quantity, String donationDate) {
        if (donorId == null || donorId.isEmpty()) return "رقم المتبرع مطلوب ✅";
        if (hospitalId == null || hospitalId.isEmpty()) return "رقم المستشفى مطلوب ✅";
        if (quantity == null || quantity.trim().isEmpty()) return "يرجى تحديد الكمية ✅";
        if (donationDate == null || donationDate.trim().isEmpty()) return "يرجى تحديد تاريخ التبرع ✅";

        try {
            int q = Integer.parseInt(quantity);
            if (q <= 0) return "الكمية يجب أن تكون أكبر من صفر ✅";
        } catch (NumberFormatException e) {
            return "يرجى إدخال رقم صحيح للكمية ✅";
        }

        return null; // البيانات سليمة
    }

    // دالة لتنسيق عرض حالة التبرع بالعربي ✅
    public static String getStatusLabel(String status) {
        if (status == null) return "غير معروف";
        switch (status) {
            case "accepted": return "مقبول";
            case "rejected": return "مرفوض";
            case "pending": return "قيد المراجعة";
            default: return "غير معروف";
        }
    }
}
