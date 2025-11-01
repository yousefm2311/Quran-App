package com.example.quran_app_android.adhan

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.quran_app_android.R

class AdhanBackgroundService : Service() {

    private val timeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context == null || intent == null) return
            val action = intent.action ?: return

            if (action in listOf(
                    Intent.ACTION_TIME_CHANGED,
                    Intent.ACTION_TIMEZONE_CHANGED,
                    Intent.ACTION_DATE_CHANGED
                )
            ) {
                Log.i("AdhanBackgroundService", "⏱️ Detected system time/date change → Rescheduling Adhan")

                val prefs = context.getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
                val lat = prefs.getFloat("lat", 0f).toDouble()
                val lng = prefs.getFloat("lng", 0f).toDouble()

                if (lat != 0.0 && lng != 0.0) {
                    NativeAdhanBridge.reschedule(context, lat, lng)
                    Log.i("AdhanBackgroundService", "✅ Adhan re-scheduled successfully after time change")
                } else {
                    Log.w("AdhanBackgroundService", "⚠️ No saved coordinates found, skipping reschedule")
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("AdhanBackgroundService", "✅ Background Service created")

        // تسجيل الـ BroadcastReceiver لتغييرات الوقت
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        registerReceiver(timeChangeReceiver, filter)

        // إنشاء إشعار صامت علشان يشتغل كـ Foreground service
        val notification = NotificationCompat.Builder(this, "adhan_channel_foreground")
            .setContentTitle("⏰ مراقبة الوقت للأذان")
            .setContentText("الخدمة تعمل في الخلفية لضمان دقة الأذان.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
             // .setPriority(NotificationCompat.PRIORITY_MIN)

        startForeground(2, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(timeChangeReceiver)
        Log.w("AdhanBackgroundService", "🛑 Background Service stopped")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
