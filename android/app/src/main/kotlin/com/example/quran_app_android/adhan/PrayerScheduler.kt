package com.example.quran_app_android.adhan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import java.util.Calendar

object PrayerScheduler {

    fun scheduleAll(context: Context, prayerTimes: Map<String, Long> = emptyMap()) {
        if (prayerTimes.isEmpty()) {
            Log.w("PrayerScheduler", "⚠️ لم يتم تمرير أي مواقيت صلاة — لا توجد صلوات للجدولة")
            return
        }

        val sdk = Build.VERSION.SDK_INT
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val ignoring = try { pm.isIgnoringBatteryOptimizations(context.packageName) } catch (_: Exception) { false }
        Log.i("PrayerScheduler", "📱 نظام التشغيل SDK=$sdk، تجاهل تحسينات البطارية=$ignoring")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Cancel any existing alarms for these names
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

        // Schedule the future prayers only
        prayerTimes.forEach { (name, millis) ->
            if (millis <= System.currentTimeMillis()) {
                Log.i("PrayerScheduler", "⏭ تم تخطي صلاة $name عند $millis لأنها وقتها فات (<= الآن)")
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
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        millis,
                        pendingIntent
                    )
                }
                Log.i("PrayerScheduler", "🕌 تم جدولة صلاة $name عند $millis (باستخدام setExact${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) "+AllowWhileIdle" else ""})")
            } catch (e: Exception) {
                Log.e("PrayerScheduler", "❌ فشل في جدولة صلاة $name: ${e.message}")
            }
        }
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
                // Always schedule for the next 12:01 AM in the future
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

            Log.i("PrayerScheduler", "🕛 تم جدولة إعادة الأذان اليومية الساعة 12:01 بعد منتصف الليل (millis=${next.timeInMillis})")
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

