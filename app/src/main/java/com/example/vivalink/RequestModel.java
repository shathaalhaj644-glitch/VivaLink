package com.example.vivalink;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RequestModel {
    // 1. تغيير date إلى confirmedAt ليطابق الصورة في الفايربيس
    private String hospitalName, bloodType, city, department, units, requestId, confirmedAt, city_bloodType;
    private boolean isDonated;

    public RequestModel() {}

    // 2. دالة ذكية لتنسيق الوقت فوراً عند عرضه في الـ Adapter
    public String getFormattedDate() {
        if (confirmedAt == null || confirmedAt.isEmpty() || confirmedAt.equals("--")) {
            return "--";
        }
        try {
            // الصيغة المخزنة في قاعدة بياناتك (ISO)
            SimpleDateFormat parser = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
            Date dateObj = parser.parse(confirmedAt);

            // الصيغة اللي بدك إياها تظهر في الـ XML
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.ENGLISH);
            return formatter.format(dateObj);
        } catch (Exception e) {
            return confirmedAt; // في حال حدث خطأ يرجع النص الأصلي
        }
    }

    // Getters
    public String getHospitalName() { return hospitalName; }
    public String getBloodType() { return bloodType; }
    public String getCity() { return city; }
    public String getDepartment() { return department; }
    public String getUnits() { return units; }
    public String getRequestId() { return requestId; }
    public String getConfirmedAt() { return confirmedAt; } // Getter الجديد
    public boolean isDonated() { return isDonated; }

    // Setters
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setDonated(boolean donated) { this.isDonated = donated; }
    public void setConfirmedAt(String confirmedAt) { this.confirmedAt = confirmedAt; }
}