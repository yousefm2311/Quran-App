package com.example.quran_app_android.adhan

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
                Log.i("AdhanBgService", "⏱️ تم اكتشاف تغيير في الوقت/التاريخ → إعادة جدولة الأذان")

                val prefs = context.getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
                val lat = prefs.getFloat("lat", 0f).toDouble()
                val lng = prefs.getFloat("lng", 0f).toDouble()

                if (lat != 0.0 && lng != 0.0) {
                    NativeAdhanBridge.reschedule(context, lat, lng)
                    Log.i("AdhanBgService", "✅ تم إعادة جدولة الأذان بعد تغيير الوقت")
                } else {
                    Log.w("AdhanBgService", "⚠️ لا توجد إحداثيات محفوظة — تجاهل إعادة الجدولة")
                }

                PrayerScheduler.scheduleDailyReset(context)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        Log.i("AdhanBgService", "✅ Background Service بدأت العمل")
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        registerReceiver(timeChangeReceiver, filter)

        val notification = NotificationCompat.Builder(this, "adhan_channel_foreground")
            .setContentTitle("🕌 الأذان يعمل في الخلفية")
            .setContentText("يتم مراقبة الوقت لضمان دقة الأذان حتى عند غلق التطبيق.")
            .setSmallIcon(R.drawable.ic_mosque)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setCategory(androidx.core.app.NotificationCompat.CATEGORY_SERVICE)
            .build()

        startForeground(2, notification)

        // Immediate reschedule and daily reset on service start
        val prefs = getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
        val lat = prefs.getFloat("lat", 0f).toDouble()
        val lng = prefs.getFloat("lng", 0f).toDouble()
        if (lat != 0.0 && lng != 0.0) {
            NativeAdhanBridge.reschedule(this, lat, lng)
            Log.i("AdhanBgService", "✅ تم إعادة جدولة الأذان فور تشغيل الخدمة")
        }
        PrayerScheduler.scheduleDailyReset(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(timeChangeReceiver)
        } catch (e: Exception) {
            // ignore if already unregistered
        }
        Log.w("AdhanBgService", "🛑 Background Service توقفت")
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY
}

