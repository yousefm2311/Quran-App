package com.example.quran_app_android.adhan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prayerName = intent?.getStringExtra("prayer_name") ?: "الصلاة"
        Log.i("AlarmReceiver", "🚨 تم تشغيل منبّه صلاة $prayerName؛ جاري بدء خدمة الأذان وواجهة التنبيه")

        val serviceIntent = Intent(context, AdhanService::class.java).apply {
            action = "START_ADHAN"
            putExtra("prayer_name", prayerName)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

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
        } catch (_: Exception) {
        }
    }
}

