package com.example.quran_app_android

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

        // ✅ تشغيل الخدمة الخلفية لمراقبة الوقت
        val serviceIntent = Intent(this, AdhanBackgroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(serviceIntent)
        else
            startService(serviceIntent)
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        // ✅ تسجيل القنوات كلها في مكان واحد
        NativeAdhanBridge.register(flutterEngine, this)
        PermissionsBridge.register(flutterEngine, this)
        NativeAzkarBridge.register(flutterEngine, this)
        super.configureFlutterEngine(flutterEngine)
    }
}
