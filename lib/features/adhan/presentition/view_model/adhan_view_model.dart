// ignore_for_file: prefer_typing_uninitialized_variables

import 'dart:async';
import 'dart:convert';
import 'package:adhan/adhan.dart';
import 'package:flutter/foundation.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:quran_app_android/core/native/native_adhan_bridge.dart';
import 'package:quran_app_android/core/service/database/database_helper.dart';
import 'package:quran_app_android/core/service/settings/SettingsServices.dart';
import 'package:quran_app_android/features/quran/presentition/view_model/quran_screen_model_details.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AdhanViewModel extends GetxController {
  double? latitude, longitude;
  RxBool isLoading = false.obs;
  SettingsServices settingsServices = Get.find<SettingsServices>();
  LocalStorageAdhanData localData = Get.find<LocalStorageAdhanData>();
  QuranScreenViewModel quranScreenViewModel = Get.find<QuranScreenViewModel>();

  @override
  void onInit() {
    super.onInit();
    requestLocationPermission();

  }
  Future<void> requestLocationPermission() async {
    var status = await Permission.location.status;
    if (!status.isGranted) {
      status = await Permission.location.request();
    }
    if (status.isGranted) {
      debugPrint('Location permission granted');
      await getCurrentLocation();
      isLoading.value = true;
      update();
    } else if (status.isDenied) {
      debugPrint('Location permission denied');
      isLoading.value = false;
      update();
    } else if (status.isPermanentlyDenied) {
      debugPrint('Location permission permanently denied');
      isLoading.value = false;
      update();
    }
  }

  Future<void> getCurrentLocation() async {
    try {
      isLoading.value = true;
      Position position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
      );
      latitude = position.latitude;
      longitude = position.longitude;
      isLoading.value = false;
      adhan();
      saveLocation(latitude!, longitude!);
      update();
    } catch (e) {
      isLoading.value = false;
      if (kDebugMode) {
        print("Error getting location: $e");
      }
      update();
    }
  }

PrayerTimes? prayerTimes;

  void adhan() async {
    if (latitude != null && longitude != null) {
      final myCoordinates = Coordinates(latitude!, longitude!);
      final param = CalculationMethod.egyptian.getParameters();
      param.madhab = Madhab.shafi;

      prayerTimes = PrayerTimes.today(myCoordinates, param);
      update();

      // نحول مواعيد الصلاة إلى خريطة Map<String, int>
      final timesMap = {
        'fajr': prayerTimes!.fajr.millisecondsSinceEpoch,
        'dhuhr': prayerTimes!.dhuhr.millisecondsSinceEpoch,
        'asr': prayerTimes!.asr.millisecondsSinceEpoch,
        'maghrib': prayerTimes!.maghrib.millisecondsSinceEpoch,
        'isha': prayerTimes!.isha.millisecondsSinceEpoch,
      };
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('last_prayer_times', jsonEncode(timesMap));
      // نرسلها إلى كوتلن
      try {
        await NativeAdhanBridge.schedulePrayerTimes(timesMap);
        debugPrint('✅ Prayer times scheduled successfully');
      } catch (e) {
        debugPrint('⚠️ Failed to schedule in Kotlin: $e');
      }


      // نحتفظ بالنوتيفيكيشن القديمة (اختياري)
    }
  }

  Future<void> saveLocation(double lat, double lng) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setDouble('lat', lat);
    await prefs.setDouble('lng', lng);
    await NativeAdhanBridge.saveLocationToNative(lat, lng);
    print('✅ Location saved for Adhan service: $lat, $lng');
  }
}
