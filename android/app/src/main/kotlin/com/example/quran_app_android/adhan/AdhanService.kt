package com.example.quran_app_android.adhan

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.quran_app_android.R

class AdhanService : Service() {

    private var player: MediaPlayer? = null
    private val CHANNEL_ID = "adhan_channel_foreground"

    override fun onCreate() {
        super.onCreate()
        ensureChannel()
        Log.i("AdhanService", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == "STOP_ADHAN") {
            Log.i("AdhanService", "Stop action received")
            stopPlayback()
            stopForeground(true)
            stopSelf()
            return START_NOT_STICKY
        }

        val prayerName = intent?.getStringExtra("prayer_name") ?: "الصلاة"

        val fullScreenIntent = Intent(this, AdhanAlertActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("prayer_name", prayerName)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🕌 وقت الأذان: $prayerName")
            .setContentText("جارٍ تشغيل الأذان.")
            .setSmallIcon(R.drawable.ic_mosque)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .build()

        startForeground(1, notification)

        startPlayback()
        Log.i("AdhanService", "Started foreground with prayer=$prayerName")
        return START_NOT_STICKY
    }

    private fun startPlayback() {
        try {
            stopPlayback() // ensure clean state
            player = MediaPlayer.create(this, R.raw.adhan).apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                isLooping = false
                setOnCompletionListener {
                    Log.i("AdhanService", "Playback completed; stopping service")
                    stopForeground(false)
                    stopSelf()
                }
                start()
            }
        } catch (e: Exception) {
            Log.e("AdhanService", "Failed to start playback: ${e.message}")
        }
    }

    private fun stopPlayback() {
        try {
            player?.let {
                if (it.isPlaying) it.stop()
                it.reset()
                it.release()
            }
        } catch (_: Exception) {
        } finally {
            player = null
        }
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Adhan Background Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel to keep Adhan background service running"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setShowBadge(false)
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        stopPlayback()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

