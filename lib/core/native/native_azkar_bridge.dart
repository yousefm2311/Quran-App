// // import 'package:flutter/material.dart';
// // import 'package:flutter/services.dart';

// // class NativeAzkarBridge {
// //   static const _channel = MethodChannel('native_azkar_bridge');

// //   static Future<void> startAzkar() async {
// //     try {
// //       await _channel.invokeMethod('startAzkar');
// //     } catch (e) {
// //       debugPrint("❌ Error starting Azkar service: $e");
// //     }
// //   }
// // }
// import 'package:flutter/services.dart';

// class NativeAzkarBridge {
//   static const _channel = MethodChannel('native_azkar_bridge');

//   static Future<void> startAzkar() async {
//     try {
//       final result = await _channel.invokeMethod('scheduleAzkar', {
//         'interval': 1,
//       });
//       print('✅ $result');
//     } catch (e) {
//       print("⚠️ Error sending azkar schedule: $e");
//     }
//   }
// }
import 'package:flutter/services.dart';

class NativeAzkarBridge {
  static const MethodChannel _channel = MethodChannel('native_azkar_bridge');

  /// 📿 جدولة الأذكار من 10 صباحًا إلى 10 مساءً
  static Future<void> scheduleDailyAzkar(int intervalHours) async {
    try {
      await _channel.invokeMethod('scheduleAzkar', {'interval': intervalHours});
      print("✅ Azkar scheduled every $intervalHours hour(s)");
    } catch (e) {
      print("❌ Error sending azkar schedule: $e");
    }
  }

  /// ❌ إلغاء الجدولة
  static Future<void> cancelAzkar() async {
    try {
      await _channel.invokeMethod('cancelAzkar');
      print("🛑 Azkar schedule cancelled");
    } catch (e) {
      print("❌ Error cancelling azkar: $e");
    }
  }
}
