import 'package:flutter/services.dart';

class NativeAdhanBridge {
  static const _channel = MethodChannel('native_adhan_bridge');

  static Future<void> schedulePrayerTimes(Map<String, int> prayerTimes) async {
    await _channel.invokeMethod('schedulePrayerTimes', prayerTimes);
  }
}
