// package com.example.quran_app_android.adhan

// import android.content.BroadcastReceiver
// import android.content.Context
// import android.content.Intent
// import android.content.Intent.ACTION_BOOT_COMPLETED
// import android.content.Intent.ACTION_LOCKED_BOOT_COMPLETED
// import android.content.Intent.ACTION_MY_PACKAGE_REPLACED
// import android.content.Intent.ACTION_TIME_CHANGED
// import android.content.Intent.ACTION_TIMEZONE_CHANGED

// class BootReceiver : BroadcastReceiver() {
//     override fun onReceive(context: Context, intent: Intent?) {
//         val action = intent?.action ?: return

//         if (action in listOf(
//                 ACTION_BOOT_COMPLETED,
//                 ACTION_LOCKED_BOOT_COMPLETED,
//                 ACTION_MY_PACKAGE_REPLACED,
//                 ACTION_TIME_CHANGED,
//                 ACTION_TIMEZONE_CHANGED
//             )) {
//             // إعادة جدولة الأذان بعد إعادة التشغيل أو تغيير التوقيت
//             PrayerScheduler.scheduleAll(context)
//         }
//     }
// }


package com.example.quran_app_android.adhan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action in listOf(
                Intent.ACTION_BOOT_COMPLETED,
                Intent.ACTION_LOCKED_BOOT_COMPLETED,
                Intent.ACTION_MY_PACKAGE_REPLACED,
                Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED
            )) {

            Log.i("BootReceiver", "♻️ النظام تغيّر (${intent.action}) → إعادة جدولة الأذان")
            Toast.makeText(context, "⏱️ إعادة جدولة الأذان تلقائيًا", Toast.LENGTH_SHORT).show()

            val prefs = context.getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
            val lat = prefs.getFloat("lat", 0f).toDouble()
            val lng = prefs.getFloat("lng", 0f).toDouble()
            if (lat != 0.0 && lng != 0.0) {
                // استدعاء الجدولة من Kotlin
                NativeAdhanBridge.reschedule(context, lat, lng)
            }
        }
    }
}
