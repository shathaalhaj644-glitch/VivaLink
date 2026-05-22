package com.example.vivalink;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class BloodRequestsHelper {

    private DatabaseReference dbRef;

    public BloodRequestsHelper() {

        dbRef = FirebaseDatabase.getInstance().getReference("Requests");
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


    public Task<Void> updateArrivalConfirmed(String requestId, boolean arrivalConfirmed) {
        return dbRef.child(requestId).child("arrivalConfirmed").setValue(arrivalConfirmed);
    }


    public Task<Void> updateArrivalTimeHours(String requestId, double arrivalTimeHours) {
        return dbRef.child(requestId).child("arrivalTimeHours").setValue(arrivalTimeHours);
    }


    public Task<Void> updateAssignedDonorId(String requestId, String donorId) {
        return dbRef.child(requestId).child("assignedDonorId").setValue(donorId);
    }


    public Task<Void> updateStaffConfirmedAt(String requestId, String staffConfirmedAt) {
        return dbRef.child(requestId).child("staffConfirmedAt").setValue(staffConfirmedAt);
    }


    public Task<Void> updateConfirmedAt(String requestId, String confirmedAt) {
        return dbRef.child(requestId).child("confirmedAt").setValue(confirmedAt);
    }


    public Task<Void> updateConfirmedByStaff(String requestId, boolean confirmedByStaff) {
        return dbRef.child(requestId).child("confirmedByStaff").setValue(confirmedByStaff);
    }


    public Task<Void> updateDonatedCount(String requestId, int donatedCount) {
        return dbRef.child(requestId).child("donatedCount").setValue(donatedCount);
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
            case "معلق": return "معلق - بانتظار التأكيد";
            default: return "غير معروف";
        }
    }
}
