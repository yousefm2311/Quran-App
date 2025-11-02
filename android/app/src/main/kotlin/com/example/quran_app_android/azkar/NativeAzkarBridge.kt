// package com.example.quran_app_android.azkar

// import android.content.Context
// import io.flutter.embedding.engine.FlutterEngine
// import io.flutter.plugin.common.MethodChannel
// import android.util.Log

// object NativeAzkarBridge {

//     private const val CHANNEL = "native_azkar_bridge"

//     fun register(flutterEngine: FlutterEngine, context: Context) {
//         MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
//             .setMethodCallHandler { call, result ->
//                 when (call.method) {
//                     "scheduleAzkar" -> {
//                         val intervalMinutes = call.argument<Int>("interval") ?: 120
//                         Log.i("NativeAzkarBridge", "📲 scheduleAzkar called from Flutter ($intervalMinutes min)")
//                         AzkarScheduler.scheduleAzkar(context, intervalMinutes)
//                         result.success("Azkar scheduled every $intervalMinutes minutes")
//                     }
//                     else -> result.notImplemented()
//                 }
//             }
//     }
// }
package com.example.quran_app_android.azkar

import android.content.Context
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.util.Log

object NativeAzkarBridge {

    private const val CHANNEL = "native_azkar_bridge"

    fun register(flutterEngine: FlutterEngine, context: Context) {
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    // ✅ بدء الجدولة اليومية
                    "scheduleAzkar" -> {
                        val intervalHours = call.argument<Int>("interval") ?: 2
                        Log.i("NativeAzkarBridge", "📲 scheduleAzkar called from Flutter ($intervalHours hour interval)")
                        AzkarScheduler.scheduleDailyAzkar(context, intervalHours)
                        result.success("Azkar scheduled every $intervalHours hours between 10AM–10PM")
                    }

                    // ❌ إلغاء الجدولة
                    "cancelAzkar" -> {
                        Log.i("NativeAzkarBridge", "🛑 cancelAzkar called from Flutter")
                        AzkarScheduler.cancelAzkar(context)
                        result.success("Azkar scheduling cancelled")
                    }

                    else -> result.notImplemented()
                }
            }
    }
}
