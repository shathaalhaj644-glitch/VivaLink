package com.example.vivalink;

import com.example.vivalink.DonorResponses;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DonorResponsesHelper {

    private DatabaseReference dbRef;

    public DonorResponsesHelper() {

        dbRef = FirebaseDatabase.getInstance().getReference("DonorResponses");
    }


    public Task<Void> addResponse(DonorResponses response) {
        return dbRef.child(response.getResponseId()).setValue(response);
    }


    public Query getAllResponses() {
        return dbRef;
    }


    public Query getResponseById(String responseId) {
        return dbRef.child(responseId);
    }


    public Task<Void> updateResponseStatus(String responseId, String newStatus) {
        return dbRef.child(responseId).child("responseStatus").setValue(newStatus);
    }


    public Task<Void> deleteResponse(String responseId) {
        return dbRef.child(responseId).removeValue();
    }

    public static String validateResponse(String donorId, String requestId, String responseStatus) {
        if (donorId == null || donorId.isEmpty()) return "رقم المتبرع مطلوب ✅";
        if (requestId == null || requestId.isEmpty()) return "رقم الطلب مطلوب ✅";
        if (responseStatus == null || responseStatus.isEmpty()) return "حالة الاستجابة مطلوبة ✅";

        return null;
    }


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
