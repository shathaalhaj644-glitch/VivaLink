package com.example.vivalink;

public class HospitalRequestModel {
    // الحقول الأساسية التي يتم تخزينها في Firebase
    public String requestId, bloodType, city, hospitalName, status, department, confirmedAt, hospitalId, units, phone, city_bloodType;

    // الحقل السحري الذي يربط الطلب بالمتبرع (للتعديل التلقائي)
    public String donorId;

    public int donatedCount; // عدد الأشخاص الذين تبرعوا لهذا الطلب

    // Constructor فارغ مطلوب من أجل Firebase (ضروري جداً للباك إند)
    public HospitalRequestModel() {}

    // Constructor كامل لإنشاء الكائن في الكود (تمت إضافة donorId هنا)
    public HospitalRequestModel(String requestId, String bloodType, String city, String hospitalName,
                                String units, String status, String department, String confirmedAt,
                                String hospitalId, String phone, String city_bloodType, int donatedCount, String donorId) {
        this.requestId = requestId;
        this.bloodType = bloodType;
        this.city = city;
        this.hospitalName = hospitalName;
        this.units = units;
        this.status = status;
        this.department = department;
        this.confirmedAt = confirmedAt;
        this.hospitalId = hospitalId;
        this.phone = phone;
        this.city_bloodType = city_bloodType;
        this.donatedCount = donatedCount;
        this.donorId = donorId; // تعيين المعرف
    }

    /**
     * دالة ذكية لتحويل الوقت من صيغة ISO (قاعدة البيانات)
     * إلى صيغة مقروءة وجميلة تظهر في قائمة المستشفى.
     */
    public String getFormattedDate() {
        if (confirmedAt == null || confirmedAt.isEmpty() || confirmedAt.equals("--")) {
            return "--";
        }
        try {
            // الصيغة القادمة من الفايربيس (yyyy/MM/dd'T'HH:mm:ss.SSS)
            java.text.SimpleDateFormat parser = new java.text.SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss.SSS", java.util.Locale.ENGLISH);
            java.util.Date dateObj = parser.parse(confirmedAt);

            // الصيغة التي ستظهر للمستخدم (يوم/شهر/سنة - ساعة:دقيقة)
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm", java.util.Locale.ENGLISH);
            return formatter.format(dateObj);
        } catch (Exception e) {
            // في حال وجود خطأ في التنسيق، يرجع النص الأصلي المخزن
            return confirmedAt;
        }
    }
}