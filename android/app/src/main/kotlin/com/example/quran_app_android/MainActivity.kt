// package com.example.quran_app_android

// import android.content.Intent
// import android.net.Uri
// import android.os.Build
// import android.os.Bundle
// import android.provider.Settings
// import io.flutter.embedding.android.FlutterActivity
// import io.flutter.embedding.engine.FlutterEngine
// import com.example.quran_app_android.adhan.NativeAdhanBridge

// class MainActivity : FlutterActivity() {

//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)

//         // ✅ طلب إذن عرض فوق التطبيقات الأخرى (Overlay permission)
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//             if (!Settings.canDrawOverlays(this)) {
//                 val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
//                     data = Uri.parse("package:$packageName")
//                     addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                 }
//                 startActivity(intent)
//             }
//         }
//     }

//     override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
//         super.configureFlutterEngine(flutterEngine)
//         // ✅ نمرّر this كـ Context للـ NativeAdhanBridge
//         NativeAdhanBridge.register(flutterEngine, this)
//     }
// }
package com.example.quran_app_android
import com.example.quran_app_android.permissions.PermissionsBridge
import android.content.Intent
import android.os.Build
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import com.example.quran_app_android.adhan.NativeAdhanBridge
import com.example.quran_app_android.adhan.AdhanBackgroundService

class MainActivity : FlutterActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ تشغيل الخدمة الخلفية لمراقبة الوقت وإعادة جدولة الأذان
        val serviceIntent = Intent(this, AdhanBackgroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        // ✅ تمرير الـ Context إلى NativeAdhanBridge
        NativeAdhanBridge.register(flutterEngine, this)
        PermissionsBridge.register(flutterEngine, this)
    }
}


