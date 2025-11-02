package com.example.quran_app_android.azkar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            val action = intent?.action ?: return

            if (action in listOf(
                    Intent.ACTION_BOOT_COMPLETED,
                    Intent.ACTION_LOCKED_BOOT_COMPLETED,
                    Intent.ACTION_MY_PACKAGE_REPLACED,
                    Intent.ACTION_TIME_CHANGED,
                    Intent.ACTION_TIMEZONE_CHANGED
                )
            ) {
                Log.i("BootReceiver", "🔄 System reboot or time change detected — rescheduling azkar")

                // إعادة جدولة الأذكار تلقائيًا بعد التشغيل أو تغيير الوقت
                AzkarScheduler.scheduleDailyAzkar(context, intervalHours = 2)
            }

        } catch (e: Exception) {
            Log.e("BootReceiver", "❌ Error in BootReceiver: ${e.message}")
            e.printStackTrace()
        }
    }
}
