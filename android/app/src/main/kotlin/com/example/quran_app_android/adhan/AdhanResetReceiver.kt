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
                json.keys().forEach { key -> map[key] = json.getLong(key) }
                PrayerScheduler.scheduleAll(context, map)
                Log.i("AdhanReset", "✅ تمت إعادة جدولة ${map.size} صلاة من الأوقات المحفوظة")
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

