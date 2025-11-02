// package com.example.quran_app_android.azkar

// import android.app.AlarmManager
// import android.app.PendingIntent
// import android.content.Context
// import android.content.Intent
// import android.util.Log
// import java.util.*

// object AzkarScheduler {
//     fun scheduleAzkar(context: Context, intervalMinutes: Int) {
//         val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//         val intent = Intent(context, AzkarReceiver::class.java)
//         val pendingIntent = PendingIntent.getBroadcast(
//             context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//         )

//         val intervalMillis = intervalMinutes * 60 * 1000L
//         val startTime = System.currentTimeMillis() + 10_000L // بعد 10 ثواني للتجربة

//         Log.i("AzkarScheduler", "⏰ Scheduling Azkar every $intervalMinutes minutes")

//         alarmManager.setRepeating(
//             AlarmManager.RTC_WAKEUP,
//             startTime,
//             intervalMillis,
//             pendingIntent
//         )
//     }
// }




// package com.example.quran_app_android.azkar

// import android.app.AlarmManager
// import android.app.PendingIntent
// import android.content.Context
// import android.content.Intent
// import android.util.Log
// import java.util.*

// object AzkarScheduler {

//     fun scheduleDailyAzkar(context: Context, intervalHours: Int) {
//         val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//         cancelAll(context, alarmManager)

//         val now = Calendar.getInstance()
//         val start = Calendar.getInstance().apply {
//             set(Calendar.HOUR_OF_DAY, 10)
//             set(Calendar.MINUTE, 0)
//             set(Calendar.SECOND, 0)
//         }

//         // لو الوقت الحالي بعد 10 مساءً، نبدأ من اليوم اللي بعده
//         if (now.get(Calendar.HOUR_OF_DAY) >= 22) {
//             start.add(Calendar.DAY_OF_YEAR, 1)
//         }

//         val end = Calendar.getInstance().apply {
//             set(Calendar.HOUR_OF_DAY, 22)
//             set(Calendar.MINUTE, 0)
//             set(Calendar.SECOND, 0)
//         }

//         var triggerTime = start.timeInMillis
//         var index = 0

//         while (triggerTime <= end.timeInMillis) {
//             val intent = Intent(context, AzkarReceiver::class.java)
//             val pendingIntent = PendingIntent.getBroadcast(
//                 context,
//                 index++,
//                 intent,
//                 PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//             )

//             alarmManager.setExactAndAllowWhileIdle(
//                 AlarmManager.RTC_WAKEUP,
//                 triggerTime,
//                 pendingIntent
//             )

//             Log.i("AzkarScheduler", "⏰ Scheduled azkar at ${Date(triggerTime)}")

//             triggerTime += intervalHours * 60 * 60 * 1000 // بالساعات
//         }

//         // ✅ إعادة الجدولة كل يوم عند منتصف الليل
//         scheduleNextDayReset(context)
//     }

//     private fun scheduleNextDayReset(context: Context) {
//         val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//         val intent = Intent(context, AzkarResetReceiver::class.java)
//         val pendingIntent = PendingIntent.getBroadcast(
//             context,
//             9999,
//             intent,
//             PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//         )

//         val resetTime = Calendar.getInstance().apply {
//             add(Calendar.DAY_OF_YEAR, 1)
//             set(Calendar.HOUR_OF_DAY, 0)
//             set(Calendar.MINUTE, 0)
//             set(Calendar.SECOND, 5)
//         }

//         alarmManager.setExactAndAllowWhileIdle(
//             AlarmManager.RTC_WAKEUP,
//             resetTime.timeInMillis,
//             pendingIntent
//         )
//     }

//     private fun cancelAll(context: Context, alarmManager: AlarmManager) {
//         for (i in 0..100) {
//             val intent = Intent(context, AzkarReceiver::class.java)
//             val pendingIntent = PendingIntent.getBroadcast(
//                 context,
//                 i,
//                 intent,
//                 PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
//             )
//             if (pendingIntent != null) {
//                 alarmManager.cancel(pendingIntent)
//             }
//         }
//     }
// }
package com.example.quran_app_android.azkar

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

object AzkarScheduler {

    private const val TAG = "AzkarScheduler"
    private const val RESET_REQ_CODE = 9999
    private const val MAX_SLOTS = 200 // احتياطي كبير لو intervalHours صغير

    /**
     * جدول الأذكار يوميًا بين 10:00 صباحًا و 10:00 مساءً بفواصل كل intervalHours ساعة.
     * - يحترم حالة الآن: قبل 10ص → يبدأ 10ص. بعد 10م → يبدأ غدًا 10ص.
     * - داخل النافذة: يلتقط أقرب فتحة متوافقة مع intervalHours.
     */
    fun scheduleDailyAzkar(context: Context, intervalHours: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // امسح أي جدول سابق (بما فيهم منبّه إعادة الضبط الليلي)
        cancelAll(context, alarmManager)

        val now = Calendar.getInstance()

        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val end = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 22)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // لو الوقت بعد 10 مساءً: رحّل للغد 10 صباحًا
        if (now.timeInMillis > end.timeInMillis) {
            start.add(Calendar.DAY_OF_YEAR, 1)
            end.add(Calendar.DAY_OF_YEAR, 1)
        }

        // لو قبل 10 صباحًا: ابدأ 10 صباحًا. لو جوّا النافذة: التقط أقرب فتحة متوافقة مع intervalHours
        var firstTrigger = start.clone() as Calendar
        if (now.timeInMillis in start.timeInMillis..end.timeInMillis) {
            // احسب أول فتحة متوافقة ≥ الآن
            val minutesFromStart = ((now.timeInMillis - start.timeInMillis) / (60 * 1000)).toInt()
            val slotMinutes = intervalHours * 60
            val nextSlotIndex = Math.ceil(minutesFromStart / slotMinutes.toDouble()).toInt()
            firstTrigger.timeInMillis = start.timeInMillis + nextSlotIndex * slotMinutes * 60L * 1000L

            // لو عدّت الفتحة المحسوبة نهاية اليوم، رحّل لليوم التالي 10 صباحًا
            if (firstTrigger.timeInMillis > end.timeInMillis) {
                firstTrigger = start.clone() as Calendar
                firstTrigger.add(Calendar.DAY_OF_YEAR, 1) // بكرة 10 صباحًا
                end.add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // جدولة الفتحات حتى نهاية النافذة
        val intervalMillis = intervalHours * 60L * 60L * 1000L
        var triggerTime = firstTrigger.timeInMillis
        var index = 0

        while (triggerTime <= end.timeInMillis && index < MAX_SLOTS) {
            val intent = Intent(context, AzkarReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                index, // requestCode فريد لكل فتحة
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // أفضل دقة ممكنة مع الحفاظ على وصوله أثناء Doze
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )

            Log.i(TAG, "⏰ Scheduled azkar at ${Date(triggerTime)} (slot #$index, every $intervalHours h)")
            index++
            triggerTime += intervalMillis
        }

        // إعادة الجدولة يوميًا بعد منتصف الليل بثوانٍ قليلة
        scheduleNextDayReset(context)
    }

    /**
     * منبّه لضبط جدول اليوم التالي عند منتصف الليل (00:00:05)
     */
    private fun scheduleNextDayReset(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AzkarResetReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            RESET_REQ_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val resetTime = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 5)
            set(Calendar.MILLISECOND, 0)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            resetTime.timeInMillis,
            pendingIntent
        )
        Log.i(TAG, "🔁 Scheduled midnight reset at ${Date(resetTime.timeInMillis)}")
    }

    /**
     * إلغاء كل منبّهات الأذكار + منبّه إعادة الضبط الليلي
     */
    fun cancelAzkar(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        cancelAll(context, alarmManager)
        Log.i(TAG, "🛑 All azkar alarms cancelled")
    }

    private fun cancelAll(context: Context, alarmManager: AlarmManager) {
        // ألغِ فتحات اليوم (نفس الـ requestCode النطاقي المستخدم في الجدولة)
        for (i in 0 until MAX_SLOTS) {
            val intent = Intent(context, AzkarReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                i,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
            }
        }

        // ألغِ منبّه إعادة الضبط الليلي
        val resetIntent = Intent(context, AzkarResetReceiver::class.java)
        val resetPI = PendingIntent.getBroadcast(
            context,
            RESET_REQ_CODE,
            resetIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (resetPI != null) {
            alarmManager.cancel(resetPI)
        }
    }
}
