// package com.example.quran_app_android.adhan

// import android.app.AlarmManager
// import android.app.PendingIntent
// import android.content.Context
// import android.content.Intent
// import android.os.Build
// import android.os.PowerManager
// import android.util.Log
// import java.util.Calendar

// object PrayerScheduler {

//     fun scheduleAll(context: Context, prayerTimes: Map<String, Long> = emptyMap()) {
//         if (prayerTimes.isEmpty()) {
//             Log.w("PrayerScheduler", "⚠️ لم يتم تمرير أي مواقيت صلاة — لا توجد صلوات للجدولة")
//             return
//         }

//         val sdk = Build.VERSION.SDK_INT
//         val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
//         val ignoring = try { pm.isIgnoringBatteryOptimizations(context.packageName) } catch (_: Exception) { false }
//         Log.i("PrayerScheduler", "📱 نظام التشغيل SDK=$sdk، تجاهل تحسينات البطارية=$ignoring")

//         val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

//         // Cancel any existing alarms for these names
//         prayerTimes.keys.forEach { name ->
//             val cancelIntent = Intent(context, AlarmReceiver::class.java)
//             val cancelPending = PendingIntent.getBroadcast(
//                 context,
//                 name.hashCode(),
//                 cancelIntent,
//                 PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//             )
//             alarmManager.cancel(cancelPending)
//         }

//         // Schedule the future prayers only
//         prayerTimes.forEach { (name, millis) ->
//             if (millis <= System.currentTimeMillis()) {
//                 Log.i("PrayerScheduler", "⏭ تم تخطي صلاة $name عند $millis لأنها وقتها فات (<= الآن)")
//                 return@forEach
//             }

//             val intent = Intent(context, AlarmReceiver::class.java).apply {
//                 putExtra("prayer_name", name)
//             }

//             val pendingIntent = PendingIntent.getBroadcast(
//                 context,
//                 name.hashCode(),
//                 intent,
//                 PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//             )

//             try {
//                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                     alarmManager.setExactAndAllowWhileIdle(
//                         AlarmManager.RTC_WAKEUP,
//                         millis,
//                         pendingIntent
//                     )
//                 } else {
//                     alarmManager.setExact(
//                         AlarmManager.RTC_WAKEUP,
//                         millis,
//                         pendingIntent
//                     )
//                 }
//                 Log.i("PrayerScheduler", "🕌 تم جدولة صلاة $name عند $millis (باستخدام setExact${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) "+AllowWhileIdle" else ""})")
//             } catch (e: Exception) {
//                 Log.e("PrayerScheduler", "❌ فشل في جدولة صلاة $name: ${e.message}")
//             }
//         }
//     }

//     fun scheduleDailyReset(context: Context) {
//         try {
//             val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//             val intent = Intent(context, AdhanResetReceiver::class.java)
//             val pendingIntent = PendingIntent.getBroadcast(
//                 context,
//                 12345,
//                 intent,
//                 PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//             )

//             val next = Calendar.getInstance().apply {
//                 timeInMillis = System.currentTimeMillis()
//                 set(Calendar.HOUR_OF_DAY, 0)
//                 set(Calendar.MINUTE, 1)
//                 set(Calendar.SECOND, 0)
//                 set(Calendar.MILLISECOND, 0)
//                 // Always schedule for the next 12:01 AM in the future
//                 if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
//             }

//             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                 alarmManager.setExactAndAllowWhileIdle(
//                     AlarmManager.RTC_WAKEUP,
//                     next.timeInMillis,
//                     pendingIntent
//                 )
//             } else {
//                 alarmManager.setExact(
//                     AlarmManager.RTC_WAKEUP,
//                     next.timeInMillis,
//                     pendingIntent
//                 )
//             }

//             Log.i("PrayerScheduler", "🕛 تم جدولة إعادة الأذان اليومية الساعة 12:01 بعد منتصف الليل (millis=${next.timeInMillis})")
//         } catch (e: Exception) {
//             Log.e("PrayerScheduler", "💥 فشل في جدولة إعادة الأذان اليومية: ${e.message}")
//         }
//     }

//     // Debug helper: schedule AdhanResetReceiver after N seconds (default 60)
//     fun scheduleTestReset(context: Context, delaySeconds: Int = 60) {
//         val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//         val intent = Intent(context, AdhanResetReceiver::class.java)
//         val pendingIntent = PendingIntent.getBroadcast(
//             context,
//             12346,
//             intent,
//             PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//         )
//         val whenMillis = System.currentTimeMillis() + delaySeconds * 1000L
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//             alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, whenMillis, pendingIntent)
//         } else {
//             alarmManager.setExact(AlarmManager.RTC_WAKEUP, whenMillis, pendingIntent)
//         }
//         Log.i("PrayerScheduler", "🧪 [تصحيح] تم جدولة اختبار إعادة الأذان بعد ${delaySeconds} ثانية (millis=$whenMillis)")
//     }
// }





package com.example.quran_app_android.adhan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object PrayerScheduler {

    private val arLocale = Locale("ar", "EG")
    private val timeFmt = SimpleDateFormat("hh:mm a, dd/MM/yyyy", arLocale).apply {
        timeZone = TimeZone.getDefault()
    }

    fun scheduleAll(context: Context, prayerTimes: Map<String, Long> = emptyMap()) {
        if (prayerTimes.isEmpty()) {
            Log.w("PrayerScheduler", "⚠️ لم يتم تمرير أي مواقيت صلاة — لا توجد صلوات للجدولة")
            return
        }

        val now = System.currentTimeMillis()
        val sdk = Build.VERSION.SDK_INT
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val ignoring = try { pm.isIgnoringBatteryOptimizations(context.packageName) } catch (_: Exception) { false }
        Log.i("PrayerScheduler", "📱 SDK=$sdk، تجاهل تحسينات البطارية=$ignoring، الآن=${timeFmt.format(now)}")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // إلغاء أي منبهات سابقة لنفس أسماء الصلوات
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

        // جدولة الصلوات القادمة فقط + لوج واضح لكل صلاة
        var scheduledCount = 0
        prayerTimes.forEach { (name, millis) ->
            if (millis <= now) {
                Log.i("PrayerScheduler", "⏭ تم تخطي صلاة $name (${timeFmt.format(millis)}) لأن وقتها فات")
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        millis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent)
                }
                scheduledCount++
                Log.i(
                    "PrayerScheduler",
                    "🕌 تم جدولة صلاة $name عند ${timeFmt.format(millis)} (setExact${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) "+AllowWhileIdle" else ""})"
                )
            } catch (e: Exception) {
                Log.e("PrayerScheduler", "❌ فشل في جدولة صلاة $name: ${e.message}")
            }
        }

        // تلخيص وعدّ الصلوات المجدولة
        Log.i("PrayerScheduler", "✅ تم جدولة $scheduledCount صلاة قادمة اليوم")

        // إظهار أقرب صلاة قادمة + الوقت المتبقي
        logNextPrayer(prayerTimes, now)

        // نحافظ على إعادة الضبط اليومية كما هي (12:01 بعد منتصف الليل)
        try {
            scheduleDailyReset(context)
        } catch (e: Exception) {
            Log.e("PrayerScheduler", "⚠️ فشل جدولة إعادة الضبط اليومية: ${e.message}")
        }
    }

    private fun logNextPrayer(prayerTimes: Map<String, Long>, now: Long) {
        val upcoming = prayerTimes
            .filter { it.value > now }
            .minByOrNull { it.value }

        if (upcoming == null) {
            Log.w("PrayerScheduler", "ℹ️ لا توجد صلوات قادمة في قائمة اليوم (كل الأوقات فاتت).")
            return
        }

        val (name, millis) = upcoming
        val remainingMs = millis - now
        Log.i(
            "PrayerScheduler",
            "⏰ أقرب صلاة: $name — الساعة ${timeFmt.format(millis)} — بعد ${formatRemaining(remainingMs)}"
        )
    }

    private fun formatRemaining(ms: Long): String {
        var sec = ms / 1000
        val days = sec / 86400
        sec %= 86400
        val hours = sec / 3600
        sec %= 3600
        val minutes = sec / 60

        val parts = mutableListOf<String>()
        if (days > 0) parts += "$days يوم"
        if (hours > 0) parts += "$hours ساعة"
        if (minutes > 0) parts += "$minutes دقيقة"
        if (parts.isEmpty()) return "أقل من دقيقة"
        return parts.joinToString(" و ")
    }

    fun scheduleDailyReset(context: Context) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AdhanResetReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                12345,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val next = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 1)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    next.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    next.timeInMillis,
                    pendingIntent
                )
            }

            Log.i(
                "PrayerScheduler",
                "🕛 تم جدولة إعادة الأذان اليومية الساعة 12:01 بعد منتصف الليل (millis=${next.timeInMillis})"
            )
        } catch (e: Exception) {
            Log.e("PrayerScheduler", "💥 فشل في جدولة إعادة الأذان اليومية: ${e.message}")
        }
    }

    // Debug helper: schedule AdhanResetReceiver after N seconds (default 60)
    fun scheduleTestReset(context: Context, delaySeconds: Int = 60) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AdhanResetReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            12346,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val whenMillis = System.currentTimeMillis() + delaySeconds * 1000L
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, whenMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, whenMillis, pendingIntent)
        }
        Log.i("PrayerScheduler", "🧪 [تصحيح] تم جدولة اختبار إعادة الأذان بعد ${delaySeconds} ثانية (millis=$whenMillis)")
    }
}
