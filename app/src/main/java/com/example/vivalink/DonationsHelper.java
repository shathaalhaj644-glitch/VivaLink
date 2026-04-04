package com.example.vivalink;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DonationsHelper {

    private DatabaseReference dbRef;

    public DonationsHelper() {

        dbRef = FirebaseDatabase.getInstance().getReference("Donations");
    }




    public Task<Void> addDonation(Donations donation) {
        return dbRef.child(donation.getDonationId()).setValue(donation);
    }


    public Query getAllDonations() {
        return dbRef;
    }


    public Query getDonationById(String donationId) {
        return dbRef.child(donationId);
    }


    public Task<Void> updateDonationStatus(String donationId, String newStatus) {
        return dbRef.child(donationId).child("status").setValue(newStatus);
    }


    public Task<Void> deleteDonation(String donationId) {
        return dbRef.child(donationId).removeValue();
    }




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

        return null;
    }


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
