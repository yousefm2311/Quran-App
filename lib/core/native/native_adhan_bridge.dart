import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class NativeAdhanBridge {
  static const _channel = MethodChannel('native_adhan_bridge');

  /// 🕌 استدعاء جدولة مواقيت الصلاة في كوتلن
  static Future<bool> schedulePrayerTimes(Map<String, int> prayerTimes) async {
    try {
      await _channel.invokeMethod('schedulePrayerTimes', prayerTimes);
      return true;
    } on PlatformException catch (e) {
      debugPrint('❌ فشل استدعاء الجدولة من Flutter: ${e.message}');
      return false;
    } catch (e) {
      debugPrint('⚠️ خطأ غير متوقع أثناء استدعاء الجدولة: $e');
      return false;
    }
  }

    static Future<void> saveLocationToNative(double lat, double lng) async {
    await _channel.invokeMethod('saveLocation', {'lat': lat, 'lng': lng});
  }
}
