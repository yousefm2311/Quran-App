package com.example.quran_app_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import com.example.quran_app_android.permissions.PermissionsBridge
import com.example.quran_app_android.adhan.NativeAdhanBridge
import com.example.quran_app_android.adhan.AdhanBackgroundService
import com.example.quran_app_android.azkar.NativeAzkarBridge

class MainActivity : FlutterActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createAdhanNotificationChannel()

        // ✅ تشغيل الخدمة الخلفية لمراقبة الوقت
        val serviceIntent = Intent(this, AdhanBackgroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(serviceIntent)
        else
            startService(serviceIntent)
    }

    private fun createAdhanNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "adhan_channel_foreground"
            val channel = NotificationChannel(
                channelId,
                "Adhan Background Service",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel to keep Adhan background service running"
                setShowBadge(false)
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        NativeAdhanBridge.register(flutterEngine, this)
        PermissionsBridge.register(flutterEngine, this)
        NativeAzkarBridge.register(flutterEngine, this)
        super.configureFlutterEngine(flutterEngine)
    }
}
