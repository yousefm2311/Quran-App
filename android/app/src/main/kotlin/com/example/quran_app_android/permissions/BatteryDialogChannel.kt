// // package com.example.quran_app_android.permissions

// // import android.content.Context
// // import android.content.Intent
// // import android.net.Uri
// // import android.os.Build
// // import android.os.PowerManager
// // import android.provider.Settings
// // import android.widget.Toast
// // import io.flutter.embedding.engine.FlutterEngine
// // import io.flutter.plugin.common.MethodChannel

// // object BatteryDialogChannel {

// //     private const val CHANNEL_NAME = "battery_permission_channel"

// //     fun register(flutterEngine: FlutterEngine, context: Context) {
// //         MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_NAME)
// //             .setMethodCallHandler { call, result ->
// //                 when (call.method) {
// //                     "checkBatteryOptimization" -> {
// //                         val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
// //                         val ignoring = pm.isIgnoringBatteryOptimizations(context.packageName)
// //                         result.success(ignoring)
// //                     }

// //                     "openBatterySettings" -> {
// //                         try {
// //                             val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
// //                             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
// //                             context.startActivity(intent)
// //                             result.success(true)
// //                         } catch (e: Exception) {
// //                             Toast.makeText(context, "⚠️ فشل فتح إعدادات البطارية", Toast.LENGTH_SHORT).show()
// //                             result.error("open_failed", e.message, null)
// //                         }
// //                     }

// //                     "openAppBatteryPage" -> {
// //                         try {
// //                             val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
// //                             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
// //                             intent.data = Uri.parse("package:${context.packageName}")
// //                             context.startActivity(intent)
// //                             result.success(true)
// //                         } catch (e: Exception) {
// //                             Toast.makeText(context, "⚠️ فشل فتح صفحة التطبيق", Toast.LENGTH_SHORT).show()
// //                             result.error("open_failed", e.message, null)
// //                         }
// //                     }

// //                     else -> result.notImplemented()
// //                 }
// //             }
// //     }
// // }
// package com.example.quran_app_android.permissions

// import android.app.AlarmManager
// import android.app.NotificationManager
// import android.content.ActivityNotFoundException
// import android.content.Context
// import android.content.Intent
// import android.location.LocationManager
// import android.net.Uri
// import android.os.Build
// import android.os.PowerManager
// import android.provider.Settings
// import androidx.core.app.NotificationManagerCompat
// import io.flutter.embedding.engine.FlutterEngine
// import io.flutter.plugin.common.MethodChannel
// import androidx.core.app.ActivityCompat
// import android.app.Activity
// import android.Manifest
// import android.content.pm.PackageManager

// object PermissionsBridge {

//     private const val CHANNEL = "permissions_bridge"
//     private var activity: Activity? = null

//     fun register(flutterEngine: FlutterEngine, hostActivity: Activity) {
//         activity = hostActivity
//         MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
//             .setMethodCallHandler { call, result ->
//                 when (call.method) {

//                     // --------- Location ----------
//                     "isLocationGranted" -> {
//                         result.success(isLocationGranted(hostActivity))
//                     }
//                     "isLocationServiceEnabled" -> {
//                         result.success(isLocationServiceEnabled(hostActivity))
//                     }
//                     "openLocationSettings" -> {
//                         openLocationSettings(hostActivity)
//                         result.success(true)
//                     }

//                     // --------- Notifications ----------
//                     "areNotificationsEnabled" -> {
//                         result.success(areNotificationsEnabled(hostActivity))
//                     }
//                     "requestPostNotifications" -> {
//                         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                             requestPostNotifications(hostActivity)
//                             result.success(true)
//                         } else {
//                             openAppNotificationSettings(hostActivity)
//                             result.success(true)
//                         }
//                     }
//                     "openAppNotificationSettings" -> {
//                         openAppNotificationSettings(hostActivity)
//                         result.success(true)
//                     }

//                     // --------- Overlay (draw over apps) ----------
//                     "canDrawOverlays" -> {
//                         result.success(canDrawOverlays(hostActivity))
//                     }
//                     "openOverlaySettings" -> {
//                         openOverlaySettings(hostActivity)
//                         result.success(true)
//                     }

//                     // --------- Battery Optimization ----------
//                     "isIgnoringBatteryOptimizations" -> {
//                         result.success(isIgnoringBatteryOptimizations(hostActivity))
//                     }
//                     "requestIgnoreBatteryOptimizations" -> {
//     val pm = hostActivity.getSystemService(Context.POWER_SERVICE) as PowerManager
//     if (!pm.isIgnoringBatteryOptimizations(hostActivity.packageName)) {
//         try {
//             val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
//             intent.data = Uri.parse("package:${hostActivity.packageName}")
//             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//             hostActivity.startActivity(intent)

//             // بعد ثانية، نحاول نفتح صفحة التطبيق مباشرة في إعدادات البطارية
//             hostActivity.window.decorView.postDelayed({
//                 try {
//                     val appIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                     appIntent.data = Uri.parse("package:${hostActivity.packageName}")
//                     appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                     hostActivity.startActivity(appIntent)
//                 } catch (_: Exception) {}
//             }, 1500)
//         } catch (e: Exception) {
//             val fallback = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
//             fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//             hostActivity.startActivity(fallback)
//         }
//     }
//     result.success(true)
// }

//                     "openBatteryOptimizationSettings" -> {
//                         openBatteryOptimizationSettings(hostActivity)
//                         result.success(true)
//                     }
                    
//                     // --------- Exact Alarm ----------
//                     "canScheduleExactAlarms" -> {
//                         result.success(canScheduleExactAlarms(hostActivity))
//                     }
//                     "requestScheduleExactAlarm" -> {
//                         requestScheduleExactAlarm(hostActivity)
//                         result.success(true)
//                     }

//                     // --------- App details / vendor auto-start ----------
//                     "openAppDetails" -> {
//                         openAppDetails(hostActivity)
//                         result.success(true)
//                     }
//                     "openVendorAutoStart" -> {
//                         openVendorAutoStart(hostActivity)
//                         result.success(true)
//                     }

//                     else -> result.notImplemented()
//                 }
//             }
//     }

//     // ==== Location ====
//     private fun isLocationGranted(ctx: Context): Boolean {
//         val fine = ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//         val coarse = ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//         return fine || coarse
//     }

//     private fun isLocationServiceEnabled(ctx: Context): Boolean {
//         val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//         return try {
//             lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//         } catch (_: Exception) { false }
//     }

//     private fun openLocationSettings(ctx: Context) {
//         ctx.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//     }

//     // ==== Notifications ====
//     private fun areNotificationsEnabled(ctx: Context): Boolean {
//         return NotificationManagerCompat.from(ctx).areNotificationsEnabled()
//     }

//     private fun requestPostNotifications(act: Activity) {
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//             if (ActivityCompat.checkSelfPermission(act, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                 ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
//             }
//         }
//     }

//     private fun openAppNotificationSettings(ctx: Context) {
//         try {
//             val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
//                 .putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName)
//                 .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//             ctx.startActivity(intent)
//         } catch (_: Exception) {
//             openAppDetails(ctx)
//         }
//     }

//     // ==== Overlay ====
//     private fun canDrawOverlays(ctx: Context): Boolean {
//         return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.canDrawOverlays(ctx) else true
//     }

//     private fun openOverlaySettings(ctx: Context) {
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//             val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${ctx.packageName}"))
//             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//             ctx.startActivity(intent)
//         } else {
//             openAppDetails(ctx)
//         }
//     }

//     // ==== Battery Optimization ====
//     private fun isIgnoringBatteryOptimizations(ctx: Context): Boolean {
//         val pm = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
//         return pm.isIgnoringBatteryOptimizations(ctx.packageName)
//     }

//     private fun requestIgnoreBatteryOptimizations(ctx: Context) {
//         try {
//             val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
//                 .setData(Uri.parse("package:${ctx.packageName}"))
//                 .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//             ctx.startActivity(intent)
//         } catch (_: Exception) {
//             openBatteryOptimizationSettings(ctx)
//         }
//     }

//     private fun openBatteryOptimizationSettings(ctx: Context) {
//         ctx.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//     }

//     // ==== Exact Alarm ====
//     private fun canScheduleExactAlarms(ctx: Context): Boolean {
//         return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//             val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//             am.canScheduleExactAlarms()
//         } else true
//     }

//     private fun requestScheduleExactAlarm(ctx: Context) {
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//             try {
//                 val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
//                     .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                 ctx.startActivity(intent)
//             } catch (_: ActivityNotFoundException) {
//                 openAppDetails(ctx)
//             }
//         }
//     }

//     // ==== App details / Vendor Auto-start ====
//     private fun openAppDetails(ctx: Context) {
//         val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//             .setData(Uri.parse("package:${ctx.packageName}"))
//             .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//         ctx.startActivity(intent)
//     }

//     /** محاولات شائعة لشاشات “Auto start” عند بعض الشركات */
//     private fun openVendorAutoStart(ctx: Context) {
//         val intents = listOf(
//             Intent().setClassName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"),
//             Intent("oppo.intent.action.OPPO_AUTO_START").setClassName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity"),
//             Intent().setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"),
//             Intent().setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")
//         )

//         for (i in intents) {
//             i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//             try { ctx.startActivity(i); return } catch (_: Exception) {}
//         }
//         // fallback
//         openAppDetails(ctx)
//     }
// }



package com.example.quran_app_android.permissions

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.*
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.Manifest
import android.content.pm.PackageManager

object PermissionsBridge {

    private const val CHANNEL = "permissions_bridge"
    private var activity: Activity? = null

    fun register(flutterEngine: FlutterEngine, hostActivity: Activity) {
        activity = hostActivity
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->
                when (call.method) {

                    // ===== LOCATION =====
                    "isLocationGranted" -> result.success(isLocationGranted(hostActivity))
                    "isLocationServiceEnabled" -> result.success(isLocationServiceEnabled(hostActivity))
                    "openLocationSettings" -> {
                        openLocationSettings(hostActivity)
                        result.success(true)
                    }

                    // ===== NOTIFICATIONS =====
                    "areNotificationsEnabled" -> result.success(areNotificationsEnabled(hostActivity))
                    "requestPostNotifications" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            requestPostNotifications(hostActivity)
                        else
                            openAppNotificationSettings(hostActivity)
                        result.success(true)
                    }
                    "openAppNotificationSettings" -> {
                        openAppNotificationSettings(hostActivity)
                        result.success(true)
                    }

                    // ===== OVERLAY =====
                    "canDrawOverlays" -> result.success(canDrawOverlays(hostActivity))
                    "openOverlaySettings" -> {
                        openOverlaySettings(hostActivity)
                        result.success(true)
                    }

                    // ===== BATTERY OPTIMIZATION =====
                    "isIgnoringBatteryOptimizations" ->
                        result.success(isIgnoringBatteryOptimizations(hostActivity))

                    "requestIgnoreBatteryOptimizations" -> {
                        handleBatteryOptimization(hostActivity)
                        result.success(true)
                    }

                    "openAppBatterySettings" -> {
                        openAppBatterySettings(hostActivity)
                        result.success(true)
                    }

                    // ===== EXACT ALARM =====
                    "canScheduleExactAlarms" -> result.success(canScheduleExactAlarms(hostActivity))
                    "requestScheduleExactAlarm" -> {
                        requestScheduleExactAlarm(hostActivity)
                        result.success(true)
                    }

                    // ===== AUTO-START =====
                    "openAppDetails" -> {
                        openAppDetails(hostActivity)
                        result.success(true)
                    }
                    "openVendorAutoStart" -> {
                        openVendorAutoStart(hostActivity)
                        result.success(true)
                    }

                    else -> result.notImplemented()
                }
            }
    }

    // -------- LOCATION ----------
    private fun isLocationGranted(ctx: Context): Boolean {
        val fine = ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    private fun isLocationServiceEnabled(ctx: Context): Boolean {
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return try {
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (_: Exception) { false }
    }

    private fun openLocationSettings(ctx: Context) {
        ctx.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    // -------- NOTIFICATIONS ----------
    private fun areNotificationsEnabled(ctx: Context): Boolean {
        return NotificationManagerCompat.from(ctx).areNotificationsEnabled()
    }

    private fun requestPostNotifications(act: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(act, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }

    private fun openAppNotificationSettings(ctx: Context) {
        try {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(intent)
        } catch (_: Exception) {
            openAppDetails(ctx)
        }
    }

    // -------- OVERLAY ----------
    private fun canDrawOverlays(ctx: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.canDrawOverlays(ctx) else true
    }

    private fun openOverlaySettings(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${ctx.packageName}"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(intent)
        } else {
            openAppDetails(ctx)
        }
    }

    // -------- BATTERY ----------
    private fun isIgnoringBatteryOptimizations(ctx: Context): Boolean {
        val pm = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(ctx.packageName)
    }

    /** التعامل الذكي مع تحسينات البطارية */
    private fun handleBatteryOptimization(ctx: Activity) {
        val pm = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(ctx.packageName)) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${ctx.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                ctx.startActivity(intent)

                // بعد ثانية نفتح صفحة التطبيق مباشرة داخل إعدادات البطارية
                ctx.window.decorView.postDelayed({
                    try {
                        openAppBatterySettings(ctx)
                    } catch (_: Exception) {}
                }, 1500)
            } catch (e: Exception) {
                openBatteryOptimizationSettings(ctx)
            }
        }
    }

    private fun openBatteryOptimizationSettings(ctx: Context) {
        ctx.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** هنا الذكاء: فتح تبويب "استخدام البطارية" داخل التطبيق إن وُجد */
    private fun openAppBatterySettings(ctx: Context) {
        val packageName = ctx.packageName
        try {
            when {
                Build.MANUFACTURER.contains("xiaomi", true) -> {
                    val intent = Intent("miui.intent.action.POWER_HIDE_MODE_APP_LIST")
                    intent.putExtra("package_name", packageName)
                    ctx.startActivity(intent)
                }
                Build.MANUFACTURER.contains("oppo", true) -> {
                    val intent = Intent().apply {
                        setClassName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")
                        putExtra("package_name", packageName)
                    }
                    ctx.startActivity(intent)
                }
                Build.MANUFACTURER.contains("vivo", true) -> {
                    val intent = Intent().apply {
                        setClassName("com.vivo.abe", "com.vivo.applicationbehaviorengine.ui.ExcessivePowerManagerActivity")
                        putExtra("package_name", packageName)
                    }
                    ctx.startActivity(intent)
                }
                Build.MANUFACTURER.contains("samsung", true) -> {
                    val intent = Intent("com.samsung.android.sm.ACTION_BATTERY")
                    ctx.startActivity(intent)
                }
                else -> {
                    // fallback على صفحة التطبيق العادية
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packageName")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    ctx.startActivity(intent)
                }
            }
        } catch (e: Exception) {
            Log.e("PermissionsBridge", "❌ فشل فتح تبويب البطارية: ${e.message}")
            val fallback = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            fallback.data = Uri.parse("package:$packageName")
            fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(fallback)
        }
    }

    // -------- EXACT ALARM ----------
    private fun canScheduleExactAlarms(ctx: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.canScheduleExactAlarms()
        } else true
    }

    private fun requestScheduleExactAlarm(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx.startActivity(intent)
            } catch (_: Exception) {
                openAppDetails(ctx)
            }
        }
    }

    // -------- APP DETAILS & AUTO START ----------
    private fun openAppDetails(ctx: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.parse("package:${ctx.packageName}"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(intent)
    }

    private fun openVendorAutoStart(ctx: Context) {
        val intents = listOf(
            Intent().setClassName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"),
            Intent("oppo.intent.action.OPPO_AUTO_START").setClassName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity"),
            Intent().setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"),
            Intent().setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")
        )

        for (i in intents) {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                ctx.startActivity(i)
                return
            } catch (_: Exception) {}
        }

        // fallback
        openAppDetails(ctx)
    }
}
