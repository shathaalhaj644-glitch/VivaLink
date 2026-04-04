package com.example.vivalink;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class UsersHelper {

    private DatabaseReference dbRef;

    public UsersHelper() {

        dbRef = FirebaseDatabase.getInstance().getReference("Users");
    }


    public Task<Void> addUser(Users user) {
        return dbRef.child(user.getId()).setValue(user);
    }

    public Query getAllUsers() {
        return dbRef;
    }


    public Query getUserById(String userId) {
        return dbRef.child(userId);
    }

    public Task<Void> updateUser(String userId, Users updatedUser) {
        return dbRef.child(userId).setValue(updatedUser);
    }


    public Task<Void> deleteUser(String userId) {
        return dbRef.child(userId).removeValue();
    }


    public Query getUserByEmail(String email) {
        return dbRef.orderByChild("email").equalTo(email);
    }


    public Query getUserByPhone(String phoneNumber) {
        return dbRef.orderByChild("phoneNumber").equalTo(phoneNumber);
    }


    public Query getUsersByBloodGroup(String bloodGroup) {
        return dbRef.orderByChild("bloodGroup").equalTo(bloodGroup);
    }


    public static String validateUser(String fullName, String email, String phoneNumber, String bloodGroup) {
        if (fullName == null || fullName.trim().isEmpty()) return "اسم المستخدم مطلوب ✅";
        if (email == null || email.trim().isEmpty()) return "البريد الإلكتروني مطلوب ✅";
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) return "رقم الهاتف مطلوب ✅";
        if (bloodGroup == null || bloodGroup.trim().isEmpty() || bloodGroup.equals("اختر فصيلة الدم"))
            return "يرجى تحديد فصيلة الدم ✅";

return null;
    }


    public static String getBloodGroupLabel(String bloodGroup) {
        if (bloodGroup == null) return "غير معروف";
        switch (bloodGroup) {
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