// package com.example.quran_app_android.adhan

// import android.app.Activity
// import android.content.Intent
// import android.os.Build
// import android.os.Bundle
// import android.os.Handler
// import android.os.Looper
// import android.view.WindowManager
// import android.widget.Button
// import android.widget.TextView
// import com.example.quran_app_android.R

// class AdhanAlertActivity : Activity() {

//     private val autoCloseDelay = 3 * 60 * 1000L // ⏱️ 3 دقائق (مدة الأذان تقريبًا)

//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)

//         // ✅ السماح بظهور الشاشة فوق شاشة القفل
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
//             setShowWhenLocked(true)
//             setTurnScreenOn(true)
//         } else {
//             @Suppress("DEPRECATION")
//             window.addFlags(
//     WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
//     WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
//     WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
//     WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
// )

//         }

//         setContentView(R.layout.activity_adhan_alert)

//         val prayerName = intent.getStringExtra("prayer_name") ?: "الصلاة"
//         val title = findViewById<TextView>(R.id.prayerTitle)
//         title.text = "🕌 حان الآن وقت $prayerName"

//         val stopButton = findViewById<Button>(R.id.stopButton)
//         stopButton.setOnClickListener {
//             stopService(Intent(this, AdhanService::class.java))
//             finish()
//         }

//         // ⏰ اغلاق تلقائي بعد مدة الصوت (3 دقائق)
//         Handler(Looper.getMainLooper()).postDelayed({
//             stopService(Intent(this, AdhanService::class.java))
//             if (!isFinishing) finish()
//         }, autoCloseDelay)
//     }
// }
package com.example.quran_app_android.adhan

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.util.Log
import com.example.quran_app_android.R

class AdhanAlertActivity : android.app.Activity() {

    private val autoCloseDelay = 3 * 60 * 1000L // ⏱️ 3 دقائق (مدة الأذان تقريبًا)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(true)
                setTurnScreenOn(true)
            } else {
                @Suppress("DEPRECATION")
                window.addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                )
            }

            setContentView(R.layout.activity_adhan_alert)
        } catch (e: Exception) {
            Log.e("AdhanAlert", "⚠️ فشل تحميل التصميم الكامل، استخدام النسخة البسيطة")
            setContentView(R.layout.activity_adhan_alert_simple)
        }

        val prayerName = intent.getStringExtra("prayer_name") ?: "الصلاة"
        val title = findViewById<TextView>(R.id.prayerTitle)
        title.text = "🕌 حان الآن وقت $prayerName"

        val stopButton = findViewById<Button>(R.id.stopButton)
        stopButton.setOnClickListener {
            stopAdhanAndNotification()
            finish()
        }

        // ⏰ اغلاق تلقائي بعد مدة الصوت (3 دقائق)
        Handler(Looper.getMainLooper()).postDelayed({
            stopAdhanAndNotification()
            if (!isFinishing) finish()
        }, autoCloseDelay)
    }

private fun stopAdhanAndNotification() {
    try {
        // 🔹 نبعث أمر STOP_ADHAN للخدمة
val stopIntent = Intent(this, AdhanService::class.java).apply {
    action = "STOP_ADHAN"
}
startService(stopIntent)


        // 🔕 نوقف الإشعار
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(1)

        Log.i("AdhanAlert", "🛑 تم إيقاف الأذان والإشعار بنجاح.")
    } catch (e: Exception) {
        Log.e("AdhanAlert", "⚠️ فشل في إيقاف الأذان أو الإشعار: ${e.message}")
    }
}

}

