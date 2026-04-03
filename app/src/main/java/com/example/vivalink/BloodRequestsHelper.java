package com.example.vivalink;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class BloodRequestsHelper {

    private DatabaseReference dbRef;

    public BloodRequestsHelper() {

        dbRef = FirebaseDatabase.getInstance().getReference("BloodRequests");
    }



    public Task<Void> addRequest(BloodRequests request) {
        return dbRef.child(request.getRequestId()).setValue(request);
    }


    public Query getAllRequests() {
        return dbRef;
    }


    public Query getRequestById(String requestId) {
        return dbRef.child(requestId);
    }


    public Task<Void> updateRequestStatus(String requestId, String newStatus) {
        return dbRef.child(requestId).child("status").setValue(newStatus);
    }


    public Task<Void> deleteRequest(String requestId) {
        return dbRef.child(requestId).removeValue();
    }

    public static String validateRequest(String bloodType, String quantity, String location, String hospital) {
        if (hospital == null || hospital.isEmpty()) return "اسم المستشفى مطلوب ✅";
        if (bloodType == null || bloodType.isEmpty() || bloodType.equals("اختر فصيلة الدم"))
            return "يرجى تحديد فصيلة الدم ✅";
        if (quantity == null || quantity.trim().isEmpty()) return "يرجى تحديد الكمية المطلوبة ✅";
        if (location == null || location.trim().isEmpty()) return "يرجى تحديد الموقع ✅";

        try {
            int q = Integer.parseInt(quantity);
            if (q <= 0) return "الكمية يجب أن تكون أكبر من صفر ✅";
        } catch (NumberFormatException e) {
            return "يرجى إدخال رقم صحيح للكمية ✅";
        }
        return null;
    }


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
