package com.example.quran_app_android.adhan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import org.json.JSONObject

class AdhanResetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.i("AdhanReset", "🔄 تم استقبال إعادة الجدولة اليومية، جاري جدولة مواقيت الصلاة لليوم الجديد")
        try {
            val prefs = context.getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE)
            val jsonString = prefs.getString("last_prayer_times", null)
            if (!jsonString.isNullOrEmpty()) {
                val json = JSONObject(jsonString)
                val map = mutableMapOf<String, Long>()
                json.keys().forEach { key ->
                    val oldTime = json.getLong(key)
                    // ✅ لو الوقت فات من اليوم السابق، زوده 24 ساعة بالضبط (86400000 ملي ثانية)
                    val adjustedTime = if (oldTime <= System.currentTimeMillis())
                        oldTime + 86_400_000L else oldTime
                    map[key] = adjustedTime
                }
                PrayerScheduler.scheduleAll(context, map)
                Log.i("AdhanReset", "✅ تمت إعادة جدولة ${map.size} صلاة بعد تعديل اليوم الجديد")
            } else {
                Log.w("AdhanReset", "⚠️ لم يتم العثور على مواقيت صلاة محفوظة — تم تخطي الجدولة")
            }
            PrayerScheduler.scheduleDailyReset(context)
            Log.i("AdhanReset", "🕛 تم جدولة إعادة الأذان اليومية القادمة الساعة 12:01 بعد منتصف الليل")
        } catch (e: Exception) {
            Log.e("AdhanReset", "💥 حدث خطأ أثناء جدولة إعادة الأذان اليومية: ${e.message}")
        }
    }
}


