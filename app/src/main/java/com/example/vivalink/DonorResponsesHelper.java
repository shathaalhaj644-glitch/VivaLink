package com.example.vivalink;

import com.example.vivalink.DonorResponses;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DonorResponsesHelper {

    private DatabaseReference dbRef;

    public DonorResponsesHelper() {
        // إنشاء مرجع لجدول الاستجابات في Firebase تحت اسم "DonorResponses"
        dbRef = FirebaseDatabase.getInstance().getReference("DonorResponses");
    }

    // --- 1. عمليات قاعدة البيانات (Database Operations) ---

    // إضافة استجابة جديدة ✅
    public Task<Void> addResponse(DonorResponses response) {
        return dbRef.child(response.getResponseId()).setValue(response);
    }

    // جلب كل الاستجابات ✅
    public Query getAllResponses() {
        return dbRef;
    }

    // جلب استجابة محددة باستخدام الـ ID ✅
    public Query getResponseById(String responseId) {
        return dbRef.child(responseId);
    }

    // تحديث حالة الاستجابة ✅
    public Task<Void> updateResponseStatus(String responseId, String newStatus) {
        return dbRef.child(responseId).child("responseStatus").setValue(newStatus);
    }

    // حذف استجابة ✅
    public Task<Void> deleteResponse(String responseId) {
        return dbRef.child(responseId).removeValue();
    }

    // --- 2. منطق الفحص والتنسيق (Logic & Validation) ---

    // التحقق من صحة بيانات الاستجابة قبل رفعها ✅
    public static String validateResponse(String donorId, String requestId, String responseStatus) {
        if (donorId == null || donorId.isEmpty()) return "رقم المتبرع مطلوب ✅";
        if (requestId == null || requestId.isEmpty()) return "رقم الطلب مطلوب ✅";
        if (responseStatus == null || responseStatus.isEmpty()) return "حالة الاستجابة مطلوبة ✅";

        return null; // البيانات سليمة
    }

    // دالة لتنسيق عرض حالة الاستجابة بالعربي ✅
    public static String getStatusLabel(String status) {
        if (status == null) return "غير معروف";
        switch (status) {
            case "pending": return "قيد المراجعة";
            case "accepted": return "مقبول";
            case "rejected": return "مرفوض";
            default: return "غير معروف";
        }
    }
}
