package com.example.quran_app_android.azkar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AzkarResetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            Log.i("AzkarResetReceiver", "🌙 Midnight reached — rescheduling azkar for new day")

            // إعادة الجدولة من الساعة 10 صباحًا لحد 10 مساءً
            AzkarScheduler.scheduleDailyAzkar(context, intervalHours = 2)

        } catch (e: Exception) {
            Log.e("AzkarResetReceiver", "❌ Error resetting daily azkar: ${e.message}")
            e.printStackTrace()
        }
    }
}
