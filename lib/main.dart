import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:get/get.dart';
import 'package:quran_app_android/core/service/settings/SettingsServices.dart';
import 'package:quran_app_android/core/service/settings/notifications_services.dart';
import 'package:quran_app_android/core/service/themes/dark_theme.dart';
import 'package:quran_app_android/core/service/themes/light_theme.dart';
import 'package:quran_app_android/core/util/binding.dart';
import 'package:quran_app_android/core/util/routes/routes.dart';

void main() {
  runZonedGuarded(
    () async {
      WidgetsFlutterBinding.ensureInitialized();
      await SystemChrome.setPreferredOrientations([
        DeviceOrientation.portraitUp,
      ]);

      // Global error handling to catch uncaught Flutter errors and zone errors
      FlutterError.onError = (FlutterErrorDetails details) {
        FlutterError.presentError(details);
        debugPrint(
          'FlutterError caught: ${details.exception}\n${details.stack}',
        );
      };
      await initService();
      final notify = NotifyHelper();
      await notify.initializeNotification(); // ← أضف دي هنا
      runApp(const MyApp());
    },
    (error, stack) {
      debugPrint('Uncaught zone error: $error\n$stack');
    },
  );
}

Future initService() async {
  await Get.putAsync(() => SettingsServices().init());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});
  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final SettingsServices settingsServices = Get.find<SettingsServices>();

  @override
  void initState() {
    super.initState();
    // setupWorkManager();
    _listenToInitialNotification();
    // Future.delayed(const Duration(seconds: 3), () async {
    //   await NotifyHelper().displayNotification();
    // });
    Future.microtask(() async {
      final notify = NotifyHelper();
      await notify.initializeNotification();
      await Future.delayed(const Duration(seconds: 2));
      final granted = await notify.ensureSchedulingPermissions(
        requestIfNeeded: true,
      );
      if (!granted) {
        debugPrint(
          'Exact alarms or notification permissions are missing; scheduled notifications will not fire.',
        );
      }
      try {
        //  notify.scheduleAzkar(timeOfDay: null);
        debugPrint('✅ Azkar scheduled successfully.');
      } catch (e, st) {
        debugPrint('⚠️ scheduleAzkar skipped: $e\n$st');
      }
    });
  }

  Future<void> _listenToInitialNotification() async {
    final NotificationAppLaunchDetails? details =
        await NotifyHelper().flutterLocalNotificationsPlugin
            .getNotificationAppLaunchDetails();

    if (details?.didNotificationLaunchApp ?? false) {
      final String? payload = details?.notificationResponse?.payload;
      if (payload != null && payload.isNotEmpty) {
        WidgetsBinding.instance.addPostFrameCallback((_) {
          NotifyHelper().handleNotificationPayload(payload);
        });
      }
    }
  }

  // Future<void> setupWorkManager() async {
  //   bool? isWorkManagerRunning = settingsServices.sharedPref!.getBool(
  //     'isWorkManager',
  //   );
  //   if (settingsServices.sharedPref!.getBool('stop_noti') == true) {
  //     if (isWorkManagerRunning == null || !isWorkManagerRunning) {
  //       await settingsServices.sharedPref!.setBool('isWorkManager', true);
  //     }
  //   }
  // }

  @override
  Widget build(BuildContext context) {
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);
    return GetMaterialApp(
      debugShowCheckedModeBanner: false,
      initialRoute: AppRoutes.onboarding,
      initialBinding: Binding(),
      getPages: AppRoutes.routes,
      theme: LightTheme().customLightTheme,
      darkTheme: DarkTheme().customDarkTheme,
      themeMode: ThemeMode.light,
    );
  }
}
