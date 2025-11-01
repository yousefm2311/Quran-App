// package com.example.quran_app_android.adhan

// import android.content.BroadcastReceiver
// import android.content.Context
// import android.content.Intent
// import android.util.Log
// import android.widget.Toast

// class AlarmReceiver : BroadcastReceiver() {
//     override fun onReceive(context: Context, intent: Intent?) {
//         val prayerName = intent?.getStringExtra("prayer_name") ?: "الصلاة"

//         Log.i("AlarmReceiver", "🚨 استقبل المنبّه لصلاة $prayerName")
//         Toast.makeText(context, "🕌 حان الآن وقت $prayerName", Toast.LENGTH_LONG).show()

//         // 🟩 1. تشغيل خدمة الأذان (تشغيل الصوت)
//         val serviceIntent = Intent(context, AdhanService::class.java).apply {
//             putExtra("prayer_name", prayerName)
//         }
//         context.startForegroundService(serviceIntent)

//         // 🟦 2. فتح شاشة الأذان فوق القفل
//         val activityIntent = Intent(context, AdhanAlertActivity::class.java).apply {
//             addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
//                      Intent.FLAG_ACTIVITY_CLEAR_TOP or
//                      Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
//             putExtra("prayer_name", prayerName)
//         }
//         context.startActivity(activityIntent)
//     }
// }
// package com.example.quran_app_android.adhan

// import android.content.BroadcastReceiver
// import android.content.Context
// import android.content.Intent
// import android.util.Log
// import android.widget.Toast

// class AlarmReceiver : BroadcastReceiver() {
//     override fun onReceive(context: Context, intent: Intent?) {
//         val prayerName = intent?.getStringExtra("prayer_name") ?: "الصلاة"

//         Log.i("AlarmReceiver", "🚨 استقبل المنبّه لصلاة $prayerName")
//         Toast.makeText(context, "🕌 حان الآن وقت $prayerName", Toast.LENGTH_LONG).show()

//         // تشغيل الخدمة المسؤولة عن الأذان
//         val serviceIntent = Intent(context, AdhanService::class.java).apply {
//             putExtra("prayer_name", prayerName)
//         }
//         context.startForegroundService(serviceIntent)
//     }
// }
package com.example.quran_app_android.adhan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prayerName = intent?.getStringExtra("prayer_name") ?: "الصلاة"
        Log.i("AlarmReceiver", "🚨 استقبل المنبّه لصلاة $prayerName")

        try {
            Toast.makeText(context, "🕌 حان الآن وقت $prayerName", Toast.LENGTH_LONG).show()

            // ✅ تشغيل خدمة الأذان (Foreground Service)
            val serviceIntent = Intent(context, AdhanService::class.java).apply {
                action = "START_ADHAN"
                putExtra("prayer_name", prayerName)
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }

            // ✅ فتح شاشة الأذان فوق أي تطبيق
            val alertIntent = Intent(context, AdhanAlertActivity::class.java).apply {
                putExtra("prayer_name", prayerName)
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
                            Intent.FLAG_ACTIVITY_NO_USER_ACTION
                )
            }

            // محاولة فتح الشاشة بأمان
            try {
                context.startActivity(alertIntent)
                Log.i("AlarmReceiver", "✅ تم فتح شاشة الأذان فوق التطبيقات")
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "⚠️ فشل فتح الشاشة: ${e.message}")
            }

        } catch (e: Exception) {
            Log.e("AlarmReceiver", "❌ خطأ أثناء معالجة المنبّه: ${e.message}")
        }
    }
}

