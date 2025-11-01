import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:shared_preferences/shared_preferences.dart';

class PermissionsController extends GetxController {
  static const _ch = MethodChannel('permissions_bridge');

  /// 🔹 عرض BottomSheet الأذونات بشكل ذكي
  static Future<void> showPermissionsDialog() async {
    final prefs = await SharedPreferences.getInstance();
    final allGranted = await _checkAllPermissions();

    if (allGranted) {
      await prefs.setBool('permissions_shown', true);
      return;
    }

    HapticFeedback.lightImpact();
    Get.bottomSheet(
      _buildSheet(),
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      barrierColor: Colors.black54,
      enterBottomSheetDuration: const Duration(milliseconds: 400),
      exitBottomSheetDuration: const Duration(milliseconds: 250),
    );

    await prefs.setBool('permissions_shown', false);
  }

  /// 🔹 تصميم الـ BottomSheet
  static Widget _buildSheet() {
    return AnimatedContainer(
      duration: const Duration(milliseconds: 350),
      curve: Curves.easeOutCubic,
      margin: const EdgeInsets.only(top: 80),
      padding: const EdgeInsets.all(22),
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 45,
            height: 5,
            margin: const EdgeInsets.only(bottom: 18),
            decoration: BoxDecoration(
              color: Colors.grey.shade300,
              borderRadius: BorderRadius.circular(100),
            ),
          ),
          const Text(
            '⚙️ تفعيل الأذونات الضرورية',
            style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),
          const Text(
            'لتعمل ميزة الأذان والتنبيهات حتى لو التطبيق مقفول، فعّل الأذونات التالية:',
            textAlign: TextAlign.center,
            style: TextStyle(fontSize: 14, color: Colors.black87),
          ),
          const SizedBox(height: 18),

          // 🟢 عناصر الأذونات
          _buildAnimatedTile(
            emoji: '📍',
            title: 'تفعيل الموقع',
            desc: 'مطلوب لتحديد مواقيت الصلاة بدقة في مدينتك',
            method: 'openLocationSettings',
          ),
          _buildAnimatedTile(
            emoji: '🔋',
            title: 'تعطيل توفير البطارية',
            desc:
                'اسمح للتطبيق بالعمل في الخلفية وتشغيل الأذان تلقائيًا بدون تأخير',
            method: 'openAppBatterySettings',
          ),
          _buildAnimatedTile(
            emoji: '📲',
            title: 'الظهور فوق التطبيقات',
            desc: 'لعرض شاشة الأذان فوق أي تطبيق أو شاشة القفل',
            method: 'openOverlaySettings',
          ),
          _buildAnimatedTile(
            emoji: '🔔',
            title: 'تفعيل الإشعارات',
            desc: 'حتى توصلك تنبيهات الأذان والأذكار اليومية',
            method: 'requestPostNotifications',
          ),

          const SizedBox(height: 25),
          ElevatedButton.icon(
            onPressed: () async {
              HapticFeedback.mediumImpact();
              await ensureAll();
              Get.back();
              final prefs = await SharedPreferences.getInstance();
              final ok = await _checkAllPermissions();
              await prefs.setBool('permissions_shown', ok);

              Get.snackbar(
                ok ? "تم التفعيل" : "الأذونات ناقصة",
                ok
                    ? "✅ الأذونات تم ضبطها بنجاح"
                    : "⚠️ تأكد من تفعيل كل الأذونات المطلوبة",
                snackPosition: SnackPosition.BOTTOM,
                backgroundColor:
                    ok ? Colors.green.shade600 : Colors.orange.shade700,
                colorText: Colors.white,
                margin: const EdgeInsets.all(12),
                borderRadius: 12,
              );
            },
            icon: const Icon(Icons.check_circle_outline),
            label: const Text('تم التفعيل'),
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.green,
              padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 14),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(14),
              ),
            ),
          ),
          const SizedBox(height: 12),
        ],
      ),
    );
  }

  /// 🔸 تنفيذ الأذونات المطلوبة
  static Future<void> ensureAll() async {
    try {
      final canOverlay =
          await _ch.invokeMethod<bool>('canDrawOverlays') ?? true;
      if (!canOverlay) await _ch.invokeMethod('openOverlaySettings');

      final ignoring =
          await _ch.invokeMethod<bool>('isIgnoringBatteryOptimizations') ??
          false;
      if (!ignoring)
        await _ch.invokeMethod('requestIgnoreBatteryOptimizations');

      final exact =
          await _ch.invokeMethod<bool>('canScheduleExactAlarms') ?? true;
      if (!exact) await _ch.invokeMethod('requestScheduleExactAlarm');

      final notif =
          await _ch.invokeMethod<bool>('areNotificationsEnabled') ?? false;
      if (!notif) await _ch.invokeMethod('requestPostNotifications');

      final gpsOn =
          await _ch.invokeMethod<bool>('isLocationServiceEnabled') ?? false;
      if (!gpsOn) await _ch.invokeMethod('openLocationSettings');
    } catch (e) {
      debugPrint("⚠️ Permission error: $e");
    }
  }

  /// ✅ فحص شامل لكل الأذونات
  static Future<bool> _checkAllPermissions() async {
    try {
      final canOverlay =
          await _ch.invokeMethod<bool>('canDrawOverlays') ?? true;
      final ignoring =
          await _ch.invokeMethod<bool>('isIgnoringBatteryOptimizations') ??
          true;
      final notif =
          await _ch.invokeMethod<bool>('areNotificationsEnabled') ?? true;
      final gpsOn =
          await _ch.invokeMethod<bool>('isLocationServiceEnabled') ?? true;

      return canOverlay && ignoring && notif && gpsOn;
    } catch (_) {
      return false;
    }
  }

  /// ✨ عنصر إذن متحرك (pulse effect)
  static Widget _buildAnimatedTile({
    required String emoji,
    required String title,
    required String desc,
    required String method,
  }) {
    return TweenAnimationBuilder<double>(
      tween: Tween(begin: 1, end: 1.05),
      duration: const Duration(milliseconds: 800),
      curve: Curves.easeInOut,
      builder: (context, scale, child) {
        return Transform.scale(
          scale: scale,
          child: GestureDetector(
            onTap: () async {
              HapticFeedback.selectionClick();
              await _ch.invokeMethod(method);
            },
            child: AnimatedContainer(
              duration: const Duration(milliseconds: 200),
              margin: const EdgeInsets.symmetric(vertical: 5),
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                border: Border.all(color: Colors.grey.shade300),
                borderRadius: BorderRadius.circular(14),
                boxShadow: [
                  BoxShadow(
                    color: Colors.grey.shade200,
                    blurRadius: 5,
                    offset: const Offset(0, 3),
                  ),
                ],
                color: Colors.white,
              ),
              child: Row(
                children: [
                  Text(emoji, style: const TextStyle(fontSize: 22)),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          title,
                          style: const TextStyle(
                            fontWeight: FontWeight.w600,
                            fontSize: 16,
                          ),
                        ),
                        Text(
                          desc,
                          style: const TextStyle(
                            color: Colors.black87,
                            fontSize: 13,
                          ),
                        ),
                      ],
                    ),
                  ),
                  const Icon(
                    Icons.arrow_forward_ios_rounded,
                    size: 16,
                    color: Colors.grey,
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}
