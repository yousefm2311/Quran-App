// package com.example.quran_app_android.adhan

// import android.app.Notification
// import android.app.NotificationChannel
// import android.app.NotificationManager
// import android.app.PendingIntent
// import android.app.Service
// import android.content.Context
// import android.content.Intent
// import android.media.MediaPlayer
// import android.os.Build
// import android.os.IBinder
// import android.util.Log
// import androidx.core.app.NotificationCompat
// import com.example.quran_app_android.R

// class AdhanService : Service() {

//     private lateinit var player: MediaPlayer
//     private val CHANNEL_ID = "adhan_channel_foreground"

//     override fun onCreate() {
//         super.onCreate()
//         Log.i("AdhanService", "Service created.")
//         createNotificationChannel()
//     }

//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//     val prayerName = intent?.getStringExtra("prayer_name") ?: "الصلاة"

//     // Intent لصفحة الأذان
//     val alertIntent = Intent(this, AdhanAlertActivity::class.java).apply {
//         addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//         putExtra("prayer_name", prayerName)
//     }

//     val fullScreenPendingIntent = PendingIntent.getActivity(
//         this, 0, alertIntent,
//         PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//     )

//     // ✅ إشعار بنمط FullScreen يفتح فوق شاشة القفل
//     val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
//         .setContentTitle("🕌 حان الآن وقت $prayerName")
//         .setContentText("تشغيل الأذان الآن")
//         .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
//         .setPriority(NotificationCompat.PRIORITY_HIGH)
//         .setCategory(NotificationCompat.CATEGORY_ALARM)
//         .setFullScreenIntent(fullScreenPendingIntent, true)
//         .setOngoing(true)
//         .build()

//     startForeground(1, notification)

//     // ✅ تشغيل صوت الأذان
//     player = MediaPlayer.create(this, R.raw.adhan)
//     player.isLooping = false
//     player.start()

//     Log.i("AdhanService", "🎵 بدأ تشغيل الأذان لصلاة $prayerName")

//     return START_NOT_STICKY
// }


// override fun onDestroy() {
//     if (::player.isInitialized) {
//         try {
//             // 🔉 تقليل الصوت تدريجيًا قبل الإغلاق (fade-out)
//             Thread {
//                 for (i in 10 downTo 0) {
//                     player.setVolume(i / 10f, i / 10f)
//                     Thread.sleep(200)
//                 }
//                 player.stop()
//                 player.release()
//             }.start()
//         } catch (e: Exception) {
//             player.stop()
//             player.release()
//         }
//     }
//     super.onDestroy()
// }


//     override fun onBind(intent: Intent?): IBinder? = null

//     // 🛠️ دالة إنشاء قناة الإشعارات
//     private fun createNotificationChannel() {
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//             val channel = NotificationChannel(
//                 CHANNEL_ID,
//                 "قناة الأذان",
//                 NotificationManager.IMPORTANCE_HIGH
//             ).apply {
//                 description = "تشغيل الأذان في الخلفية"
//                 lockscreenVisibility = Notification.VISIBILITY_PUBLIC
//             }

//             val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//             manager.createNotificationChannel(channel)
//         }
//     }
// }
// package com.example.quran_app_android.adhan

// import android.app.*
// import android.content.Context
// import android.content.Intent
// import android.media.MediaPlayer
// import android.os.Build
// import android.os.IBinder
// import android.util.Log
// import androidx.core.app.NotificationCompat
// import com.example.quran_app_android.R

// class AdhanService : Service() {

//     private lateinit var player: MediaPlayer
//     private val CHANNEL_ID = "adhan_channel_foreground"

//     override fun onCreate() {
//         super.onCreate()
//         createNotificationChannel()
//         Log.i("AdhanService", "✅ Service created.")
//     }

//     override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//         val prayerName = intent?.getStringExtra("prayer_name") ?: "الصلاة"

//         // 🔹 Intent لفتح شاشة الأذان (حتى لو المستخدم على الهوم سكرين)
//         val alertIntent = Intent(this, AdhanAlertActivity::class.java).apply {
//             addFlags(
//                 Intent.FLAG_ACTIVITY_NEW_TASK or
//                 Intent.FLAG_ACTIVITY_CLEAR_TOP or
//                 Intent.FLAG_ACTIVITY_SINGLE_TOP
//             )
//             putExtra("prayer_name", prayerName)
//         }

//         val pendingIntent = PendingIntent.getActivity(
//             this,
//             100,
//             alertIntent,
//             PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//         )

//         // 🔔 إشعار بنمط التنبيه الكامل (Full Screen)
//         val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//             .setContentTitle("🕌 حان الآن وقت $prayerName")
//             .setContentText("اضغط لعرض شاشة الأذان")
//             .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
//             .setPriority(NotificationCompat.PRIORITY_MAX)
//             .setCategory(NotificationCompat.CATEGORY_ALARM)
//             .setFullScreenIntent(pendingIntent, true)
//             .setContentIntent(pendingIntent)
//             .setOngoing(true)
//             .setAutoCancel(false)
//             .build()

//         startForeground(1, notification)

//         // ✅ فتح شاشة الأذان تلقائيًا فوق التطبيقات
//         val topIntent = Intent(applicationContext, AdhanAlertActivity::class.java).apply {
//             addFlags(
//                 Intent.FLAG_ACTIVITY_NEW_TASK or
//                 Intent.FLAG_ACTIVITY_CLEAR_TOP or
//                 Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
//                 Intent.FLAG_ACTIVITY_SINGLE_TOP
//             )
//             putExtra("prayer_name", prayerName)
//         }
//         applicationContext.startActivity(topIntent)

//         // 🔉 تشغيل صوت الأذان (بدون توقف عند الضغط على الإشعار)
//         try {
//             player = MediaPlayer().apply {
//                 val afd = resources.openRawResourceFd(R.raw.adhan)
//                 setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
//                 afd.close()
//                 isLooping = false
//                 prepare()
//                 start()
//             }
//             Log.i("AdhanService", "🎵 تشغيل الأذان لصلاة $prayerName")
//         } catch (e: Exception) {
//             Log.e("AdhanService", "❌ فشل تشغيل الصوت: ${e.message}")
//         }

//         return START_STICKY
//     }

//     override fun onDestroy() {
//         try {
//             if (::player.isInitialized) {
//                 player.stop()
//                 player.release()
//             }
//         } catch (e: Exception) {
//             Log.e("AdhanService", "Error stopping player: ${e.message}")
//         }
//         super.onDestroy()
//         Log.i("AdhanService", "🛑 تم إيقاف الأذان.")
//     }

//     override fun onBind(intent: Intent?): IBinder? = null

//     private fun createNotificationChannel() {
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//             val channel = NotificationChannel(
//                 CHANNEL_ID,
//                 "قناة الأذان",
//                 NotificationManager.IMPORTANCE_HIGH
//             ).apply {
//                 description = "تشغيل الأذان في الخلفية"
//                 lockscreenVisibility = Notification.VISIBILITY_PUBLIC
//             }
//             val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//             manager.createNotificationChannel(channel)
//         }
//     }
// }
package com.example.quran_app_android.adhan

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.quran_app_android.R

class AdhanService : Service() {

    private var player: MediaPlayer? = null
    private val CHANNEL_ID = "adhan_channel_foreground"
    private var isPlaying = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.i("AdhanService", "✅ Service created.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val prayerName = intent?.getStringExtra("prayer_name") ?: "الصلاة"

        when (action) {

            // 🕌 تشغيل الأذان فعلاً
            "START_ADHAN" -> {
                if (isPlaying) {
                    Log.w("AdhanService", "⚠️ الأذان شغال بالفعل، تجاهل التشغيل المكرر.")
                    return START_NOT_STICKY
                }

                startAdhan(prayerName)
            }

            // 🛑 أمر إيقاف
            "STOP_ADHAN" -> {
                Log.i("AdhanService", "🛑 تم استقبال أمر إيقاف الأذان من Activity")
                stopAdhan()
            }

            // 🧭 Intent آخر (زي لما المستخدم يفتح الشاشة يدويًا)
            else -> {
                Log.d("AdhanService", "ℹ️ تم تجاهل Intent بدون Action (فتح الشاشة فقط).")
            }
        }

        return START_STICKY
    }

    private fun startAdhan(prayerName: String) {
        // 🔹 Intent لفتح شاشة الأذان فوق التطبيقات والقفل
        val alertIntent = Intent(this, AdhanAlertActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
            putExtra("prayer_name", prayerName)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            101,
            alertIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🕌 حان الآن وقت $prayerName")
            .setContentText("اضغط لعرض شاشة الأذان")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        startForeground(1, notification)

        // ✅ فتح الشاشة فوق القفل
        // ✅ فتح الشاشة فوق التطبيقات أو القفل
try {
    val alertIntent = Intent(this, AdhanAlertActivity::class.java).apply {
        addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
            Intent.FLAG_ACTIVITY_SINGLE_TOP or
            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
            Intent.FLAG_ACTIVITY_NO_USER_ACTION
        )
        putExtra("prayer_name", prayerName)
    }

    startActivity(alertIntent)
    Log.i("AdhanService", "🕌 تم فتح شاشة الأذان فوق التطبيقات")
} catch (e: Exception) {
    Log.e("AdhanService", "❌ فشل فتح شاشة الأذان: ${e.message}")
}


        // 🎵 تشغيل الصوت
        try {
            val afd = resources.openRawResourceFd(R.raw.adhan)
            player = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                isLooping = false
                prepare()
                start()
            }
            isPlaying = true
            Log.i("AdhanService", "🎶 بدأ تشغيل الأذان لصلاة $prayerName")
        } catch (e: Exception) {
            Log.e("AdhanService", "❌ فشل تشغيل الصوت: ${e.message}")
        }
    }

    private fun stopAdhan() {
        try {
            player?.apply {
                stop()
                release()
            }
            isPlaying = false

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(1)

            stopSelf()
            Log.i("AdhanService", "🛑 تم إيقاف الأذان والإشعار.")
        } catch (e: Exception) {
            Log.e("AdhanService", "⚠️ فشل إيقاف الأذان: ${e.message}")
        }
    }

    override fun onDestroy() {
        stopAdhan()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "قناة الأذان",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تشغيل الأذان في الخلفية"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
