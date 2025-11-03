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
        Log.i("NativeAdhanBridge", "Registered native_adhan_bridge channel")
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "schedulePrayerTimes" -> {
                val context = appContext ?: return result.error("no_context", "Context not available", null)
                val args = call.arguments
                val asMap: Map<String, Any?>? = args as? Map<String, Any?>
                if (asMap == null) return result.error("invalid_args", "Expected Map<String, int/long>", null)
                val timesMap = asMap.mapValues { (_, v) ->
                    when (v) {
                        is Long -> v
                        is Int -> v.toLong()
                        is Number -> v.toLong()
                        is String -> v.toLongOrNull() ?: 0L
                        else -> 0L
                    }
                }

                Log.i("NativeAdhanBridge", "Scheduling ${timesMap.size} prayer alarms from Flutter")
                PrayerScheduler.scheduleAll(context, prayerTimes = timesMap)
                saveLastPrayerTimes(context, timesMap)
                result.success(true)
            }

            "saveLocation" -> {
                val args = call.arguments as? Map<String, Double>
                val lat = args?.get("lat") ?: 0.0
                val lng = args?.get("lng") ?: 0.0
                saveLocation(appContext, lat, lng)
                result.success(true)
            }

            "scheduleDailyReset" -> {
                val context = appContext ?: return result.error("no_context", "Context not available", null)
                Log.i("NativeAdhanBridge", "scheduleDailyReset invoked from Flutter")
                PrayerScheduler.scheduleDailyReset(context)
                result.success("Daily reset scheduled")
            }

            // Debug-only helper: schedule AdhanResetReceiver after 60s
            "scheduleTestReset" -> {
                val context = appContext ?: return result.error("no_context", "Context not available", null)
                PrayerScheduler.scheduleTestReset(context, 60)
                result.success(true)
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
        Log.i("NativeAdhanBridge", "Saved location lat=$lat, lng=$lng")
    }

    private fun saveLastPrayerTimes(context: Context, map: Map<String, Long>) {
        val json = JSONObject(map)
        val prefs = context.getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("last_prayer_times", json.toString()).apply()
        Log.i("NativeAdhanBridge", "Persisted last_prayer_times (${map.size} items)")
    }

    // Used by native side on boot/time change
    fun reschedule(context: Context, lat: Double, lng: Double) {
        try {
            val prefs = context.getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE)
            val jsonString = prefs.getString("last_prayer_times", null)
            if (jsonString != null) {
                val json = JSONObject(jsonString)
                val map = mutableMapOf<String, Long>()
                json.keys().forEach { map[it] = json.getLong(it) }
                PrayerScheduler.scheduleAll(context, map)
                Log.i("NativeAdhanBridge", "Rescheduled ${map.size} prayers with saved times")
            } else {
                Log.w("NativeAdhanBridge", "No saved last_prayer_times; cannot reschedule")
            }
        } catch (e: Exception) {
            Log.e("NativeAdhanBridge", "Reschedule error: ${e.message}")
        }
    }
}
