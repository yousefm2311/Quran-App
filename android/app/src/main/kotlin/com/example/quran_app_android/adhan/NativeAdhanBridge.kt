// package com.example.quran_app_android.adhan

// import android.content.Context
// import android.util.Log
// import io.flutter.embedding.engine.FlutterEngine
// import io.flutter.plugin.common.MethodCall
// import io.flutter.plugin.common.MethodChannel
// import org.json.JSONObject

// object NativeAdhanBridge : MethodChannel.MethodCallHandler {

//     private var channel: MethodChannel? = null
//     private var appContext: Context? = null

//     // ✅ نمرّر الـ Context من MainActivity عند تسجيل القناة
//     fun register(engine: FlutterEngine, context: Context) {
//         channel = MethodChannel(engine.dartExecutor.binaryMessenger, "native_adhan_bridge")
//         channel?.setMethodCallHandler(this)
//         appContext = context.applicationContext   // <--- هنا الصح
//         Log.i("NativeAdhanBridge", "✅ تم تسجيل NativeAdhanBridge")
//     }

//     override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
//         when (call.method) {
//             "schedulePrayerTimes" -> {
//                 val timesMap = call.arguments as? Map<String, Long>
//                 if (timesMap == null) {
//                     result.error("invalid_args", "لم يتم تمرير مواعيد صحيحة", null)
//                     return
//                 }

//                 val context = appContext
//                 if (context == null) {
//                     result.error("no_context", "لم يتم تهيئة Context", null)
//                     return
//                 }

//                 Log.i("NativeAdhanBridge", "🕌 جدولة الأذان من Flutter (${timesMap.size} صلاة)")
//                 PrayerScheduler.scheduleAll(context, prayerTimes = timesMap)
//                 saveLastPrayerTimes(context, timesMap)
//                 result.success(true)
//             }

//             else -> result.notImplemented()
//         }
//     }

//     private fun saveLastPrayerTimes(context: Context, map: Map<String, Long>) {
//         val json = JSONObject(map)
//         val prefs = context.getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE)
//         prefs.edit().putString("last_prayer_times", json.toString()).apply()
//     }

//     fun reschedule(context: Context, lat: Double, lng: Double) {
//         try {
//             Log.i("NativeAdhanBridge", "♻️ إعادة جدولة الأذان ($lat,$lng)")
//             val prefs = context.getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE)
//             val jsonString = prefs.getString("last_prayer_times", null)
//             if (jsonString != null) {
//                 val json = JSONObject(jsonString)
//                 val map = mutableMapOf<String, Long>()
//                 json.keys().forEach { map[it] = json.getLong(it) }
//                 PrayerScheduler.scheduleAll(context, map)
//                 Log.i("NativeAdhanBridge", "✅ تم إعادة الجدولة (${map.size} صلاة)")
//             } else {
//                 Log.w("NativeAdhanBridge", "⚠️ لا توجد مواقيت محفوظة لإعادة الجدولة.")
//             }
//         } catch (e: Exception) {
//             Log.e("NativeAdhanBridge", "❌ فشل إعادة الجدولة: ${e.message}")
//         }
//     }
// }


package com.example.quran_app_android.adhan

import android.content.Context
import android.util.Log
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.json.JSONObject

object NativeAdhanBridge : MethodChannel.MethodCallHandler {

    private var channel: MethodChannel? = null
    private var appContext: Context? = null

    fun register(engine: FlutterEngine, context: Context) {
        channel = MethodChannel(engine.dartExecutor.binaryMessenger, "native_adhan_bridge")
        channel?.setMethodCallHandler(this)
        appContext = context.applicationContext
        Log.i("NativeAdhanBridge", "✅ تم تسجيل NativeAdhanBridge")
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {

            // 📅 جدولة الصلوات
            "schedulePrayerTimes" -> {
                val timesMap = call.arguments as? Map<String, Long>
                if (timesMap == null) {
                    result.error("invalid_args", "لم يتم تمرير مواعيد صحيحة", null)
                    return
                }

                val context = appContext
                if (context == null) {
                    result.error("no_context", "لم يتم تهيئة Context", null)
                    return
                }

                Log.i("NativeAdhanBridge", "🕌 جدولة الأذان من Flutter (${timesMap.size} صلاة)")
                PrayerScheduler.scheduleAll(context, prayerTimes = timesMap)
                saveLastPrayerTimes(context, timesMap)
                result.success(true)
            }

            // 📍 حفظ الإحداثيات القادمة من Flutter
            "saveLocation" -> {
                val args = call.arguments as? Map<String, Double>
                if (args != null) {
                    val lat = args["lat"] ?: 0.0
                    val lng = args["lng"] ?: 0.0
                    saveLocation(appContext, lat, lng)
                    result.success(true)
                } else {
                    result.error("invalid_args", "إحداثيات غير صحيحة", null)
                }
            }

            else -> result.notImplemented()
        }
    }

    private fun saveLocation(context: Context?, lat: Double, lng: Double) {
        if (context == null) return
        val prefs = context.getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putFloat("lat", lat.toFloat())
            putFloat("lng", lng.toFloat())
            apply()
        }
        Log.i("NativeAdhanBridge", "📍 تم حفظ الموقع ($lat, $lng) في location_prefs")
    }

    private fun saveLastPrayerTimes(context: Context, map: Map<String, Long>) {
        val json = JSONObject(map)
        val prefs = context.getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("last_prayer_times", json.toString()).apply()
    }

    fun reschedule(context: Context, lat: Double, lng: Double) {
        try {
            Log.i("NativeAdhanBridge", "♻️ إعادة جدولة الأذان ($lat,$lng)")
            val prefs = context.getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE)
            val jsonString = prefs.getString("last_prayer_times", null)
            if (jsonString != null) {
                val json = JSONObject(jsonString)
                val map = mutableMapOf<String, Long>()
                json.keys().forEach { map[it] = json.getLong(it) }
                PrayerScheduler.scheduleAll(context, map)
                Log.i("NativeAdhanBridge", "✅ تم إعادة الجدولة (${map.size} صلاة)")
            } else {
                Log.w("NativeAdhanBridge", "⚠️ لا توجد مواقيت محفوظة لإعادة الجدولة.")
            }
        } catch (e: Exception) {
            Log.e("NativeAdhanBridge", "❌ فشل إعادة الجدولة: ${e.message}")
        }
    }
}
