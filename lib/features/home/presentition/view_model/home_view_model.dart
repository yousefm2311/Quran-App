import 'dart:async';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:home_widget/home_widget.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:quran_app_android/core/native/native_adhan_bridge.dart';
import 'package:quran_app_android/core/native/native_azkar_bridge.dart';
import 'package:quran_app_android/core/native/permissions_helper.dart';
import 'package:quran_app_android/core/service/settings/SettingsServices.dart';
import 'package:quran_app_android/core/util/assets.dart';
import 'package:quran_app_android/core/util/constant/static_vars.dart';
import 'package:quran_app_android/core/util/routes/routes.dart';
import 'package:shared_preferences/shared_preferences.dart';

class HomeViewModel extends GetxController {
  HomeViewModel() {
    _init();
  }

  final SettingsServices settingsServices = Get.find<SettingsServices>();
  final StaticVars staticVars = StaticVars();

  RxString lastRead = ''.obs;
  String currentZekr = 'سبحان الله';
  String appGroupId = 'group.com.homeScreenApp';
  String iOSWidgetName = 'MyHomeWidget';
  String androidWidgetName = 'MyHomeWidget';
  String dataKey = 'currentZekr';

  Future<void> _init() async {
    await requestBasicPermissions();
    await getLastRead();
    await _setupHomeWidget();
    await NativeAzkarBridge.scheduleDailyAzkar(2);
    await NativeAdhanBridge.scheduleDailyReset();

    // ✅ إظهار نافذة الأذونات لمرة واحدة فقط
    final prefs = await SharedPreferences.getInstance();
    final shown = prefs.getBool('permissions_shown') ?? false;
    if (!shown) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        PermissionsController.showPermissionsDialog();
      });
    }
  }

  // 🔹 طلب الأذونات الأساسية (الموقع + الإشعارات)
  Future<void> requestBasicPermissions() async {
    try {
      // الموقع
      if (await Permission.location.isDenied) {
        await Permission.location.request();
      }

      // الإشعارات
      if (await Permission.notification.isDenied) {
        await Permission.notification.request();
      }

      // exact alarm
      if (await Permission.scheduleExactAlarm.isDenied) {
        await Permission.scheduleExactAlarm.request();
      }

      debugPrint('✅ Basic permissions handled.');
    } catch (e) {
      debugPrint('⚠️ Permission error: $e');
    }
  }

  Future<void> getLastRead() async {
    final prefs = settingsServices.sharedPref;
    if (prefs != null && prefs.getString('lastRead') != null) {
      lastRead.value = prefs.getString('lastRead')!;
      update();
    }
  }

  Future<void> _setupHomeWidget() async {
    try {
      await HomeWidget.setAppGroupId(appGroupId);
      await Future.delayed(const Duration(seconds: 2));
      await _updateWidget();
    } catch (e, st) {
      debugPrint('⚠️ HomeWidget init failed: $e\n$st');
    }
  }

  Future<void> _updateWidget() async {
    try {
      final randomIndex = DateTime.now().second % staticVars.smallDo3a2.length;
      currentZekr = staticVars.smallDo3a2[randomIndex];
      await HomeWidget.saveWidgetData(dataKey, currentZekr);
      await HomeWidget.saveWidgetData('deepLink', 'quranapp://azkar');
      await Future.delayed(const Duration(seconds: 1));
      await HomeWidget.updateWidget(
        iOSName: iOSWidgetName,
        androidName: androidWidgetName,
      );
      debugPrint('✅ Widget updated successfully');
    } catch (e, st) {
      debugPrint('⚠️ Widget update failed: $e\n$st');
    }
  }

  // 🕌 بيانات الصفحة الرئيسية
  List<String> titles = ['القرآن الكريم', 'حديث', 'أسماء الله الحسنى', 'تفسير'];

  List<String> images = [
    AssetsData.mushaf_1,
    AssetsData.moon,
    AssetsData.nameOfAllah,
    AssetsData.mushaf,
  ];

  List<String> routes = [
    AppRoutes.quranScreen,
    AppRoutes.sectionHadith,
    AppRoutes.nameofAllah,
    AppRoutes.tafsser,
  ];

  List<String> titleListView = ['أذكار', 'مواقيت الصلاة', 'القبلة', 'المسبحة'];

  List<String> imageListView = [
    AssetsData.azkar,
    AssetsData.ramadan,
    AssetsData.qiblahImage,
    AssetsData.pngTree,
  ];

  List<String> routesListView = [
    AppRoutes.azkar,
    AppRoutes.adhan,
    AppRoutes.qiblah,
    AppRoutes.pngTree,
  ];
  TextEditingController timeController = TextEditingController();
  var formKey = GlobalKey<FormState>();
}
