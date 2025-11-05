// package com.example.quran_app_android.adhan

// import android.content.BroadcastReceiver
// import android.content.Context
// import android.content.Intent
// import android.util.Log

// class AlarmReceiver : BroadcastReceiver() {
//     override fun onReceive(context: Context, intent: Intent?) {
//         val prayerName = intent?.getStringExtra("prayer_name") ?: "الصلاة"
//         Log.i("AlarmReceiver", "🚨 تم تشغيل منبّه صلاة $prayerName؛ جاري بدء خدمة الأذان وواجهة التنبيه")

//         val serviceIntent = Intent(context, AdhanService::class.java).apply {
//             action = "START_ADHAN"
//             putExtra("prayer_name", prayerName)
//         }
//         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//             context.startForegroundService(serviceIntent)
//         } else {
//             context.startService(serviceIntent)
//         }

//         val alertIntent = Intent(context, AdhanAlertActivity::class.java).apply {
//             putExtra("prayer_name", prayerName)
//             addFlags(
//                 Intent.FLAG_ACTIVITY_NEW_TASK or
//                     Intent.FLAG_ACTIVITY_CLEAR_TOP or
//                     Intent.FLAG_ACTIVITY_SINGLE_TOP or
//                     Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
//             )
//         }
//         try {
//             context.startActivity(alertIntent)
//         } catch (_: Exception) {
//         }
//     }
// }

package com.example.quran_app_android.adhan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val prayerName = intent?.getStringExtra("prayer_name") ?: "الصلاة"
        Log.i("AlarmReceiver", "🚨 تم استقبال منبّه صلاة $prayerName — سيتم إفاقة الجهاز وتشغيل الأذان")

        // ✅ **إفاقة المعالج لحظيًا حتى لو الجهاز في Doze**
        try {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "quran_app:adhan_wakelock"
            )
            wakeLock.acquire(10 * 1000L) // يمسك الـ CPU لمدة 10 ثواني بس
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "⚠️ فشل في أخذ WakeLock: ${e.message}")
        }

        // ✅ تشغيل خدمة الأذان (Foreground Service)
        val serviceIntent = Intent(context, AdhanService::class.java).apply {
            action = "START_ADHAN"
            putExtra("prayer_name", prayerName)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            context.startForegroundService(serviceIntent)
        else
            context.startService(serviceIntent)

        // ✅ فتح واجهة التنبيه فوق أي تطبيق
        val alertIntent = Intent(context, AdhanAlertActivity::class.java).apply {
            putExtra("prayer_name", prayerName)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            )
        }

        try {
            context.startActivity(alertIntent)
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "⚠️ فشل فتح واجهة التنبيه: ${e.message}")
        }
    }
}
