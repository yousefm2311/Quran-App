import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class NativeAdhanBridge {
  static const _channel = MethodChannel('native_adhan_bridge');

  // Schedule today's prayers with exact alarms (millis per prayer)
  static Future<bool> schedulePrayerTimes(Map<String, int> prayerTimes) async {
    try {
      await _channel.invokeMethod('schedulePrayerTimes', prayerTimes);
      return true;
    } on PlatformException catch (e) {
      debugPrint('schedulePrayerTimes error: ${e.message}');
      return false;
    } catch (e) {
      debugPrint('schedulePrayerTimes error: $e');
      return false;
    }
  }

  // Persist location (for native rescheduling on time/boot changes)
  static Future<void> saveLocationToNative(double lat, double lng) async {
    await _channel.invokeMethod('saveLocation', {'lat': lat, 'lng': lng});
  }

  // Schedule the daily 12:01 AM reset alarm
  static Future<void> scheduleDailyReset() async {
    try {
      await _channel.invokeMethod('scheduleDailyReset');
      debugPrint('Daily reset scheduled at 12:01 AM');
    } on PlatformException catch (e) {
      debugPrint('scheduleDailyReset error: ${e.message}');
    }
  }

  // Debug: schedule a test reset after ~60 seconds
  static Future<void> scheduleTestReset() async {
    try {
      await _channel.invokeMethod('scheduleTestReset');
      debugPrint('[DEBUG] Scheduled test reset ~60s');
    } catch (e) {
      debugPrint('[DEBUG] scheduleTestReset error: $e');
    }
  }
}

