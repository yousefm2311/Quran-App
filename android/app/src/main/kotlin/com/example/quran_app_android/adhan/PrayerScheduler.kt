// package com.example.quran_app_android.adhan

// import android.app.AlarmManager
// import android.app.PendingIntent
// import android.content.Context
// import android.content.Intent
// import android.util.Log

// object PrayerScheduler {

//     /**
//      * جدولة كل الصلوات القادمة بناءً على أوقات بيتم إرسالها من فلاتر
//      * @param context  Context التطبيق
//      * @param prayerTimes خريطة فيها أوقات الصلوات (millis)
//      * مثلاً:
//      * {
//      *   "fajr": 1730443200000,
//      *   "dhuhr": 1730486400000,
//      *   "asr": 1730508000000,
//      *   "maghrib": 1730529600000,
//      *   "isha": 1730540400000
//      * }
//      */
//     fun scheduleAll(context: Context, prayerTimes: Map<String, Long> = emptyMap()) {
//         if (prayerTimes.isEmpty()) {
//             Log.w("PrayerScheduler", "No prayer times received to schedule.")
//             return
//         }

//         val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

//         prayerTimes.forEach { (name, millis) ->
//             val intent = Intent(context, AlarmReceiver::class.java).apply {
//                 putExtra("prayer_name", name)
//             }

//             val requestCode = name.hashCode()
//             val pendingIntent = PendingIntent.getBroadcast(
//                 context,
//                 requestCode,
//                 intent,
//                 PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//             )

//             // لو الوقت فات، متجدولش
//             if (millis <= System.currentTimeMillis()) return@forEach

//             alarmManager.setExactAndAllowWhileIdle(
//                 AlarmManager.RTC_WAKEUP,
//                 millis,
//                 pendingIntent
//             )

//             Log.i("PrayerScheduler", "⏰ Scheduled $name at ${millis}")
//         }
//     }

//     /**
//      * إلغاء كل الصلوات المجدولة (اختياري)
//      */
//     fun cancelAll(context: Context, prayerTimes: Map<String, Long>) {
//         val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//         prayerTimes.keys.forEach { name ->
//             val intent = Intent(context, AlarmReceiver::class.java)
//             val pendingIntent = PendingIntent.getBroadcast(
//                 context,
//                 name.hashCode(),
//                 intent,
//                 PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//             )
//             alarmManager.cancel(pendingIntent)
//         }
//     }
// }
// package com.example.quran_app_android.adhan

// import android.app.AlarmManager
// import android.app.PendingIntent
// import android.content.Context
// import android.content.Intent
// import android.util.Log

// object PrayerScheduler {

//     fun scheduleAll(context: Context, prayerTimes: Map<String, Long> = emptyMap()) {
//         Log.i("PrayerScheduler", "Testing: scheduling test adhan after 60 sec")

//         val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//         val intent = Intent(context, AlarmReceiver::class.java)
//         intent.putExtra("prayer_name", "اختبار الأذان")

//         val pendingIntent = PendingIntent.getBroadcast(
//             context,
//             9999,
//             intent,
//             PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//         )

//         val triggerTime = System.currentTimeMillis() + 60 * 1000 // بعد دقيقة واحدة
//         alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)

//         Log.i("PrayerScheduler", "✅ تم جدولة اختبار الأذان بعد دقيقة واحدة.")
//     }
// }


package com.example.quran_app_android.adhan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

object PrayerScheduler {

    fun scheduleAll(context: Context, prayerTimes: Map<String, Long> = emptyMap()) {
        if (prayerTimes.isEmpty()) {
            Log.w("PrayerScheduler", "❌ لم تصل مواقيت صلاة من Flutter")
            Toast.makeText(context, "❌ لم تصل مواقيت صلاة من Flutter", Toast.LENGTH_SHORT).show()
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 🧹 إلغاء أي منبهات قديمة لتجنب التكرار
        prayerTimes.keys.forEach { name ->
            val cancelIntent = Intent(context, AlarmReceiver::class.java)
            val cancelPending = PendingIntent.getBroadcast(
                context,
                name.hashCode(),
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(cancelPending)
        }

        // 🕌 جدولة كل صلاة بدقة
        prayerTimes.forEach { (name, millis) ->
            if (millis <= System.currentTimeMillis()) {
                Log.i("PrayerScheduler", "⏭ تخطينا $name لأنها في الماضي (${millis})")
                return@forEach
            }

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("prayer_name", name)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                name.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    millis,
                    pendingIntent
                )
                Log.i("PrayerScheduler", "✅ تم جدولة $name عند ${millis}")
            } catch (e: Exception) {
                Log.e("PrayerScheduler", "❌ فشل في جدولة $name: ${e.message}")
            }
        }

        Toast.makeText(
            context,
            "✅ تم جدولة ${prayerTimes.size} صلاة بنجاح",
            Toast.LENGTH_SHORT
        ).show()
    }
}
