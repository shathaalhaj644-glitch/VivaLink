package com.example.vivalink;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class BloodRequestsHelper {

    private DatabaseReference dbRef;

    public BloodRequestsHelper() {
        // إنشاء مرجع لجدول طلبات الدم في Firebase تحت اسم "BloodRequests"
        dbRef = FirebaseDatabase.getInstance().getReference("BloodRequests");
    }

    // --- 1. عمليات قاعدة البيانات (Database Operations) ---

    // إضافة طلب دم جديد ✅
    public Task<Void> addRequest(BloodRequests request) {
        return dbRef.child(request.getRequestId()).setValue(request);
    }

    // جلب كل الطلبات (لعرضها في القائمة العامة) ✅
    public Query getAllRequests() {
        return dbRef;
    }

    // جلب طلب محدد باستخدام الـ ID (لمشاهدة التفاصيل) ✅
    public Query getRequestById(String requestId) {
        return dbRef.child(requestId);
    }

    // تحديث حالة الطلب (مثلاً من open إلى completed) ✅
    public Task<Void> updateRequestStatus(String requestId, String newStatus) {
        return dbRef.child(requestId).child("status").setValue(newStatus);
    }

    // حذف طلب دم ✅
    public Task<Void> deleteRequest(String requestId) {
        return dbRef.child(requestId).removeValue();
    }

    // --- 2. منطق الفحص والتنسيق (Logic & Validation) ---

    // التحقق من صحة البيانات قبل رفعها للسيرفر ✅
    public static String validateRequest(String bloodType, String quantity, String location, String hospital) {
        if (hospital == null || hospital.isEmpty()) return "اسم المستشفى مطلوب ✅";
        if (bloodType == null || bloodType.isEmpty() || bloodType.equals("اختر فصيلة الدم")) return "يرجى تحديد فصيلة الدم ✅";
        if (quantity == null || quantity.trim().isEmpty()) return "يرجى تحديد الكمية المطلوبة ✅";
        if (location == null || location.trim().isEmpty()) return "يرجى تحديد الموقع ✅";

        try {
            int q = Integer.parseInt(quantity);
            if (q <= 0) return "الكمية يجب أن تكون أكبر من صفر ✅";
        } catch (NumberFormatException e) {
            return "يرجى إدخال رقم صحيح للكمية ✅";
        }

        return null; // البيانات سليمة تماماً
    }

    // دالة لتنسيق عرض حالة الطلب باللغة العربية ✅
    public static String getStatusLabel(String status) {
        if (status == null) return "غير معروف";
        switch (status) {
            case "open": return "مفتوح - بانتظار متبرعين";
            case "completed": return "مكتمل - تم توفير الكمية";
            case "cancelled": return "ملغي";
            default: return "غير معروف";
        }
    }
}
