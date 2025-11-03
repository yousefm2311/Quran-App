

package com.example.quran_app_android

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class MyHomeWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        super.onUpdate(context, manager, ids)
        Log.i("MyHomeWidget", "🔁 تم تحديث الويدجت يدويًا")

        for (id in ids) updateWidget(context, manager, id)

        scheduleNextUpdate(context) // إعادة الجدولة
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WIDGET) {
            Log.i("MyHomeWidget", "⏰ تم تشغيل التحديث التلقائي")
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(Intent(context, MyHomeWidget::class.java).component)
            for (id in ids) updateWidget(context, manager, id)
            scheduleNextUpdate(context)
        }
    }

    private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.my_home_widget)
        val azkar = loadAzkar(context)
        val zekr = if (azkar.isNotEmpty()) azkar.random() else "سبحان الله"
        views.setTextViewText(R.id.widget_text, zekr)
        manager.updateAppWidget(widgetId, views)
    }

    private fun loadAzkar(context: Context): List<String> {
        val list = mutableListOf<String>()
        try {
            val inputStream = context.assets.open("azkar.json")
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            val json = JSONArray(reader.readText())
            reader.close()

            for (i in 0 until json.length()) {
                val obj = json.getJSONObject(i)
                val arr = obj.getJSONArray("array")
                for (j in 0 until arr.length()) {
                    list.add(arr.getJSONObject(j).getString("text"))
                }
            }
        } catch (e: Exception) {
            Log.e("MyHomeWidget", "❌ فشل تحميل الأذكار: ${e.message}")
        }
        return list
    }

    private fun scheduleNextUpdate(context: Context) {
        val intent = Intent(context, MyHomeWidget::class.java).apply {
            action = ACTION_REFRESH_WIDGET
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val interval = 5 * 60 * 1000L // كل 3 دقائق
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + interval,
            pendingIntent
        )
        Log.i("MyHomeWidget", "📅 تم جدولة التحديث القادم بعد 3 دقائق")
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.example.quran_app_android.REFRESH_WIDGET"
    }
}




// package com.example.quran_app_android

// import android.app.AlarmManager
// import android.app.PendingIntent
// import android.appwidget.AppWidgetManager
// import android.appwidget.AppWidgetProvider
// import android.content.Context
// import android.content.Intent
// import android.os.Handler
// import android.os.Looper
// import android.os.SystemClock
// import android.util.Log
// import android.widget.RemoteViews
// import org.json.JSONArray
// import java.io.BufferedReader
// import java.io.InputStreamReader

// class MyHomeWidget : AppWidgetProvider() {

//     private val handler = Handler(Looper.getMainLooper())
//     private val scrollInterval = 10000L // كل 4 ثواني تغيير الجزء
//     private var currentIndex = 0
//     private var currentParts: List<String> = emptyList()

//     override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
//         super.onUpdate(context, manager, ids)
//         Log.i("MyHomeWidget", "🔁 تم تحديث الويدجت يدويًا")

//         for (id in ids) updateWidget(context, manager, id)

//         scheduleNextUpdate(context) // إعادة الجدولة كل 3 دقايق
//     }

//     override fun onReceive(context: Context, intent: Intent) {
//         super.onReceive(context, intent)
//         if (intent.action == ACTION_REFRESH_WIDGET) {
//             Log.i("MyHomeWidget", "⏰ تم تشغيل التحديث التلقائي")
//             val manager = AppWidgetManager.getInstance(context)
//             val ids = manager.getAppWidgetIds(Intent(context, MyHomeWidget::class.java).component)
//             for (id in ids) updateWidget(context, manager, id)
//             scheduleNextUpdate(context)
//         }
//     }

//     private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
//         val views = RemoteViews(context.packageName, R.layout.my_home_widget)
//         val azkar = loadAzkar(context)
//         val zekr = if (azkar.isNotEmpty()) azkar.random() else "سبحان الله"

//         // تقسيم الذكر الطويل لأجزاء صغيرة (كل جزء ~70 حرف)
//         currentParts = splitText(zekr, 70)
//         currentIndex = 0

//         // عرض أول جزء
//         if (currentParts.isNotEmpty()) {
//             views.setTextViewText(R.id.widget_text, currentParts[0])
//             manager.updateAppWidget(widgetId, views)
//         }

//         // تشغيل الـ AutoScroll (يبدّل النص كل فترة)
//         startAutoScroll(manager, widgetId, views)

//         Log.i("MyHomeWidget", "🕌 عرض ذكر جديد مع AutoScroll (${currentParts.size} جزء)")
//     }

//     private fun startAutoScroll(manager: AppWidgetManager, widgetId: Int, views: RemoteViews) {
//         handler.removeCallbacksAndMessages(null)
//         handler.postDelayed(object : Runnable {
//             override fun run() {
//                 if (currentParts.isNotEmpty()) {
//                     currentIndex = (currentIndex + 1) % currentParts.size
//                     views.setTextViewText(R.id.widget_text, currentParts[currentIndex])
//                     manager.updateAppWidget(widgetId, views)
//                     handler.postDelayed(this, scrollInterval)
//                 }
//             }
//         }, scrollInterval)
//     }

//     private fun splitText(text: String, chunkSize: Int): List<String> {
//         val words = text.split(" ")
//         val parts = mutableListOf<String>()
//         var currentPart = StringBuilder()

//         for (word in words) {
//             if (currentPart.length + word.length > chunkSize) {
//                 parts.add(currentPart.toString())
//                 currentPart = StringBuilder()
//             }
//             currentPart.append("$word ")
//         }
//         if (currentPart.isNotEmpty()) {
//             parts.add(currentPart.toString())
//         }
//         return parts
//     }

//     private fun loadAzkar(context: Context): List<String> {
//         val list = mutableListOf<String>()
//         try {
//             val inputStream = context.assets.open("azkar.json")
//             val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
//             val json = JSONArray(reader.readText())
//             reader.close()

//             for (i in 0 until json.length()) {
//                 val obj = json.getJSONObject(i)
//                 val arr = obj.getJSONArray("array")
//                 for (j in 0 until arr.length()) {
//                     list.add(arr.getJSONObject(j).getString("text"))
//                 }
//             }
//         } catch (e: Exception) {
//             Log.e("MyHomeWidget", "❌ فشل تحميل الأذكار: ${e.message}")
//         }
//         return list
//     }

//     private fun scheduleNextUpdate(context: Context) {
//         val intent = Intent(context, MyHomeWidget::class.java).apply {
//             action = ACTION_REFRESH_WIDGET
//         }

//         val pendingIntent = PendingIntent.getBroadcast(
//             context,
//             0,
//             intent,
//             PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//         )

//         val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//         val interval = 30 * 1000L // كل 3 دقائق
//         alarmManager.setExactAndAllowWhileIdle(
//             AlarmManager.ELAPSED_REALTIME_WAKEUP,
//             SystemClock.elapsedRealtime() + interval,
//             pendingIntent
//         )
//         Log.i("MyHomeWidget", "📅 تم جدولة التحديث القادم بعد 3 دقائق")
//     }

//     companion object {
//         const val ACTION_REFRESH_WIDGET = "com.example.quran_app_android.REFRESH_WIDGET"
//     }
// }
