package com.example.vivalink;

import com.google.firebase.database.ServerValue;
import java.util.HashMap;
import java.util.Map;

public class BloodBankNotificationModel {
    // المتغيرات الأساسية لكل أنواع الإشعارات
    public String title;        // عنوان الإشعار
    public String message;      // نص الرسالة
    public String type;         // نوع الحركة (NEW_TEST, DONOR_COMING, NEW_REQUEST)
    public String targetType;   // لمين رايح الإشعار (ADMIN, DONOR, HOSPITAL)

    // متغيرات الفلترة (عشان يوصل للشخص الصح)
    public String hospitalId;   // معرف المستشفى
    public String donorId;     // معرف المتبرع
    public String targetCity;   // المدينة المستهدفة
    public String targetBlood;  // الفصيلة المستهدفة

    // متغيرات إضافية للتفاصيل
    public String donorName;    // اسم المتبرع
    public String refNumber;    // رقم مرجع الفحص
    public Object timestamp;    // وقت الإشعار

    // 1. كونسرتكتور فارغ (إجباري عشان Firebase)
    public BloodBankNotificationModel() {
    }

    // 2. دالة جاهزة لإرسال إشعار للموظف (لما المتبرع يرفع فحص)
    public static BloodBankNotificationModel createTestNotification(String dName, String dId, String ref) {
        BloodBankNotificationModel n = new BloodBankNotificationModel();
        n.title = "💉 فحص مخبري جديد";
        n.message = "المتبرع " + dName + " قام برفع صورة فحص بمرجع: " + ref;
        n.type = "NEW_TEST";
        n.targetType = "ADMIN"; // الموظف هو المستهدف
        n.donorId = dId;
        n.refNumber = ref;
        n.donorName = dName;
        n.timestamp = ServerValue.TIMESTAMP;
        return n;
    }

    // 3. دالة جاهزة لإرسال إشعار للموظف/المستشفى (لما المتبرع يضغط "تبرع الآن")
    public static BloodBankNotificationModel createComingNotification(String dName, String blood, String hName, String hId) {
        BloodBankNotificationModel n = new BloodBankNotificationModel();
        n.title = "🏃 متبرع في الطريق";
        n.message = "المتبرع " + dName + " (فصيلة " + blood + ") في طريقه إلى مستشفى " + hName;
        n.type = "DONOR_COMING";
        n.targetType = "ADMIN"; // أو HOSPITAL حسب مين بدك يراقب الحركة
        n.hospitalId = hId;
        n.donorName = dName;
        n.targetBlood = blood;
        n.timestamp = ServerValue.TIMESTAMP;
        return n;
    }

    // 4. دالة جاهزة لإرسال نداء للمتبرعين (لما المستشفى يطلب دم)
    public static BloodBankNotificationModel createUrgentRequestNotification(String hName, String city, String blood) {
        BloodBankNotificationModel n = new BloodBankNotificationModel();
        n.title = "🆘 طلب دم عاجل";
        n.message = "مستشفى " + hName + " بحاجة ماسة لفصيلة " + blood + " في مدينة " + city;
        n.type = "NEW_REQUEST";
        n.targetType = "DONOR"; // المتبرعين هم المستهدفين
        n.targetCity = city;
        n.targetBlood = blood;
        n.timestamp = ServerValue.TIMESTAMP;
        return n;
    }
}