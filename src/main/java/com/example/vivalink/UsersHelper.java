package com.example.vivalink;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class UsersHelper {

    private DatabaseReference dbRef;

    public UsersHelper() {
        // إنشاء مرجع لجدول المستخدمين في Firebase تحت اسم "Users"
        dbRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    // --- 1. عمليات قاعدة البيانات الأساسية (CRUD) ---

    // إضافة مستخدم جديد ✅
    public Task<Void> addUser(Users user) {
        return dbRef.child(user.getId()).setValue(user);
    }

    // جلب كل المستخدمين ✅
    public Query getAllUsers() {
        return dbRef;
    }

    // جلب مستخدم محدد باستخدام الـ ID ✅
    public Query getUserById(String userId) {
        return dbRef.child(userId);
    }

    // تحديث بيانات المستخدم بالكامل ✅
    public Task<Void> updateUser(String userId, Users updatedUser) {
        return dbRef.child(userId).setValue(updatedUser);
    }

    // حذف مستخدم ✅
    public Task<Void> deleteUser(String userId) {
        return dbRef.child(userId).removeValue();
    }

    // --- 2. عمليات البحث المتقدم (Advanced Search) ---

    // البحث عن مستخدمين حسب البريد الإلكتروني 📧
    public Query getUserByEmail(String email) {
        return dbRef.orderByChild("email").equalTo(email);
    }

    // البحث عن مستخدمين حسب رقم الهاتف 📱
    public Query getUserByPhone(String phoneNumber) {
        return dbRef.orderByChild("phoneNumber").equalTo(phoneNumber);
    }

    // البحث عن مستخدمين حسب فصيلة الدم 🩸
    public Query getUsersByBloodGroup(String bloodGroup) {
        return dbRef.orderByChild("bloodGroup").equalTo(bloodGroup);
    }

    // --- 3. منطق الفحص والتنسيق (Logic & Validation) ---

    // التحقق من صحة بيانات المستخدم قبل رفعها ✅
    public static String validateUser(String fullName, String email, String phoneNumber, String bloodGroup) {
        if (fullName == null || fullName.trim().isEmpty()) return "اسم المستخدم مطلوب ✅";
        if (email == null || email.trim().isEmpty()) return "البريد الإلكتروني مطلوب ✅";
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) return "رقم الهاتف مطلوب ✅";
        if (bloodGroup == null || bloodGroup.trim().isEmpty() || bloodGroup.equals("اختر فصيلة الدم"))
            return "يرجى تحديد فصيلة الدم ✅";

        return null; // البيانات سليمة
    }

    // دالة لتنسيق عرض فصيلة الدم بالعربي ✅
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
