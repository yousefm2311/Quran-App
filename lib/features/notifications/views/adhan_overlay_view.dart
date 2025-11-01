import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';
import 'package:lottie/lottie.dart';
import 'package:quran_app_android/core/service/settings/notifications_services.dart';
import 'package:quran_app_android/core/util/assets.dart';
import 'package:quran_app_android/core/util/color.dart';

class AdhanOverlayView extends StatelessWidget {
  const AdhanOverlayView({super.key});

  static const Map<String, String> _prayerNameLookup = {
    'fajr': 'الفجر',
    'dhuhr': 'الظهر',
    'asr': 'العصر',
    'maghrib': 'المغرب',
    'isha': 'العشاء',
  };

  @override
  Widget build(BuildContext context) {
    final Map<String, dynamic>? args =
        Get.arguments as Map<String, dynamic>?;
    final String prayerKey = (args?['prayerKey'] as String?) ?? '';
    final int? notificationId = args?['notificationId'] as int?;
    final DateTime scheduledAt =
        (args?['scheduledAt'] as DateTime?) ?? DateTime.now();
    final String prayerName =
        _prayerNameLookup[prayerKey] ?? 'الصلاة';
    final String formattedTime =
        DateFormat('hh:mm a', 'ar').format(scheduledAt.toLocal());

    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Align(
                alignment: Alignment.centerRight,
                child: IconButton(
                  icon: const Icon(Icons.close, size: 28),
                  color: Colors.black87,
                  onPressed: () => _closeOverlay(notificationId),
                ),
              ),
              const SizedBox(height: 12),
              Expanded(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Lottie.asset(
                      AssetsData.coming,
                      repeat: true,
                      height: 200,
                    ),
                    const SizedBox(height: 20),
                    Text(
                      'حان الآن وقت $prayerName',
                      style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                            fontWeight: FontWeight.bold,
                            color: AppColors.kPrimaryColor,
                          ),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 12),
                    Text(
                      'الوقت المحدد: $formattedTime',
                      style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                            color: Colors.black87,
                            fontWeight: FontWeight.w500,
                          ),
                    ),
                    const SizedBox(height: 16),
                    Text(
                      'افتح التطبيق لقراءة الأذكار بعد الصلاة أو لمتابعة وردك اليومي.',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: Colors.grey.shade700,
                            height: 1.5,
                          ),
                      textAlign: TextAlign.center,
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 12),
              Row(
                children: [
                  Expanded(
                    child: ElevatedButton.icon(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppColors.kPrimaryColor,
                        padding: const EdgeInsets.symmetric(
                          vertical: 14,
                        ),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(16),
                        ),
                      ),
                      onPressed: () => _closeOverlay(notificationId),
                      icon: const Icon(Icons.done_all_rounded, size: 22),
                      label: const Text(
                        'تم الاستجابة',
                        style: TextStyle(fontSize: 16),
                      ),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: OutlinedButton.icon(
                      style: OutlinedButton.styleFrom(
                        padding:
                            const EdgeInsets.symmetric(vertical: 14),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(16),
                        ),
                      ),
                      onPressed: () => _closeOverlay(notificationId),
                      icon: const Icon(Icons.notifications_off_outlined),
                      label: const Text(
                        'إيقاف الأذان',
                        style: TextStyle(fontSize: 16),
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _closeOverlay(int? notificationId) {
    if (notificationId != null) {
      NotifyHelper().flutterLocalNotificationsPlugin.cancel(notificationId);
    }
    Get.back(closeOverlays: true);
  }
}
