// // ignore_for_file: prefer_typing_uninitialized_variables

// import 'dart:async';
// import 'dart:convert';
// import 'package:adhan/adhan.dart';
// import 'package:flutter/foundation.dart';
// import 'package:geolocator/geolocator.dart';
// import 'package:get/get.dart';
// import 'package:permission_handler/permission_handler.dart';
// import 'package:quran_app_android/core/native/native_adhan_bridge.dart';
// import 'package:quran_app_android/core/service/database/database_helper.dart';
// import 'package:quran_app_android/core/service/settings/SettingsServices.dart';
// import 'package:quran_app_android/features/quran/presentition/view_model/quran_screen_model_details.dart';
// import 'package:shared_preferences/shared_preferences.dart';

// class AdhanViewModel extends GetxController {
//   double? latitude, longitude;
//   RxBool isLoading = false.obs;
//   SettingsServices settingsServices = Get.find<SettingsServices>();
//   LocalStorageAdhanData localData = Get.find<LocalStorageAdhanData>();
//   QuranScreenViewModel quranScreenViewModel = Get.find<QuranScreenViewModel>();

//   @override
//   void onInit() {
//     super.onInit();
//     requestLocationPermission();

//   }
//   Future<void> requestLocationPermission() async {
//     var status = await Permission.location.status;
//     if (!status.isGranted) {
//       status = await Permission.location.request();
//     }
//     if (status.isGranted) {
//       debugPrint('Location permission granted');
//       await getCurrentLocation();
//       isLoading.value = true;
//       update();
//     } else if (status.isDenied) {
//       debugPrint('Location permission denied');
//       isLoading.value = false;
//       update();
//     } else if (status.isPermanentlyDenied) {
//       debugPrint('Location permission permanently denied');
//       isLoading.value = false;
//       update();
//     }
//   }

//   Future<void> getCurrentLocation() async {
//     try {
//       isLoading.value = true;
//       Position position = await Geolocator.getCurrentPosition(
//         desiredAccuracy: LocationAccuracy.high,
//       );
//       latitude = position.latitude;
//       longitude = position.longitude;
//       isLoading.value = false;
//       adhan();
//       saveLocation(latitude!, longitude!);
//       update();
//     } catch (e) {
//       isLoading.value = false;
//       if (kDebugMode) {
//         print("Error getting location: $e");
//       }
//       update();
//     }
//   }

// PrayerTimes? prayerTimes;

//   void adhan() async {
//     if (latitude != null && longitude != null) {
//       final myCoordinates = Coordinates(latitude!, longitude!);
//       final param = CalculationMethod.egyptian.getParameters();
//       param.madhab = Madhab.shafi;

//       prayerTimes = PrayerTimes.today(myCoordinates, param);
//       update();

//       // نحول مواعيد الصلاة إلى خريطة Map<String, int>
//       final timesMap = {
//         'fajr': prayerTimes!.fajr.millisecondsSinceEpoch,
//         'dhuhr': prayerTimes!.dhuhr.millisecondsSinceEpoch,
//         'asr': prayerTimes!.asr.millisecondsSinceEpoch,
//         'maghrib': prayerTimes!.maghrib.millisecondsSinceEpoch,
//         'isha': prayerTimes!.isha.millisecondsSinceEpoch,
//       };
//       final prefs = await SharedPreferences.getInstance();
//       await prefs.setString('last_prayer_times', jsonEncode(timesMap));
//       // نرسلها إلى كوتلن
//       try {
//         await NativeAdhanBridge.schedulePrayerTimes(timesMap);
//         debugPrint('✅ Prayer times scheduled successfully');
//       } catch (e) {
//         debugPrint('⚠️ Failed to schedule in Kotlin: $e');
//       }
//     }
//   }

//   Future<void> saveLocation(double lat, double lng) async {
//     final prefs = await SharedPreferences.getInstance();
//     await prefs.setDouble('lat', lat);
//     await prefs.setDouble('lng', lng);
//     await NativeAdhanBridge.saveLocationToNative(lat, lng);
//     debugPrint('✅ Location saved for Adhan service: $lat, $lng');
//   }
// }

// ignore_for_file: prefer_typing_uninitialized_variables

import 'dart:async';
import 'dart:convert';
import 'package:adhan/adhan.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
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

  PrayerTimes? prayerTimes;
  bool isDefaultLocation = false; // ✅ نعرف إذا كنا بنستخدم موقع افتراضي

  @override
  void onInit() async {
    super.onInit();
    try {
      await initializeAdhan();
      debugPrint('💥 initializeAdhan true: ');
    } catch (e) {
      debugPrint('💥 initializeAdhan failed: $e');
      await useCairoFallback();
    }
  }

  /// 🔹 تهيئة النظام بالكامل
  Future<void> initializeAdhan() async {
    isLoading.value = true;
    update();

    var status = await Permission.location.status;
    if (!status.isGranted) {
      status = await Permission.location.request();
    }

    if (status.isGranted) {
      final prefs = await SharedPreferences.getInstance();
      final savedLat = prefs.getDouble('lat');
      final savedLng = prefs.getDouble('lng');

      if (savedLat != null &&
          savedLng != null &&
          savedLat != 0 &&
          savedLng != 0) {
        latitude = savedLat;
        longitude = savedLng;
        isDefaultLocation = prefs.getBool('isDefaultLocation') ?? false;
        debugPrint("✅ Loaded saved location: $latitude, $longitude");
        await adhan();
      } else {
        await getCurrentLocation();
      }
    } else {
      debugPrint('❌ Location permission not granted');
      await useCairoFallback();
    }

    isLoading.value = false;
    update();
  }

  /// 🔹 محاولة تحديد الموقع الحقيقي
  Future<void> getCurrentLocation() async {
    debugPrint('📡 Starting location request...');
    isDefaultLocation = false;
    Get.snackbar(
      "جارٍ تحديد الموقع...",
      "برجاء الانتظار لحساب مواقيت الصلاة 🕌",
      snackPosition: SnackPosition.BOTTOM,
      backgroundColor: Colors.blueAccent.withOpacity(0.9),
      colorText: Colors.white,
      duration: const Duration(seconds: 4),
    );
    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      debugPrint('❌ Location service is disabled on the device.');
      await useCairoFallback();
      return;
    }

    LocationPermission permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        debugPrint('🚫 Location permission denied by user.');
        await useCairoFallback();
        return;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      debugPrint('⛔ Permission permanently denied.');
      await useCairoFallback();
      return;
    }

    try {
      // نحاول أول مرة بوقت انتظار أطول شوية
      Position position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
        timeLimit: const Duration(seconds: 25),
      );

      latitude = position.latitude;
      longitude = position.longitude;
      isDefaultLocation = false;
      debugPrint('✅ Got new location: $latitude, $longitude');
    } catch (e) {
      debugPrint('⚠️ Failed to get location: $e');
      debugPrint('🔁 Trying last known position...');

      try {
        Position? lastPos = await Geolocator.getLastKnownPosition();
        if (lastPos != null) {
          latitude = lastPos.latitude;
          longitude = lastPos.longitude;
          isDefaultLocation = false;
          debugPrint('✅ Using last known position: $latitude, $longitude');
        } else {
          debugPrint('❌ No last known position found, fallback to Cairo');
          await useCairoFallback();
          return;
        }
      } catch (e2) {
        debugPrint('💥 Second attempt failed: $e2');
        await useCairoFallback();
        return;
      }
    }

    // لو وصلنا هنا يبقى الإحداثيات جاهزة
    if (latitude != null && longitude != null) {
      await saveLocation(latitude!, longitude!, isDefaultLocation);
      await adhan();
    } else {
      debugPrint('❌ Coordinates still null, using Cairo fallback');
      await useCairoFallback();
    }

    update();
  }

  /// 🔹 استخدام القاهرة كخطة احتياطية
  Future<void> useCairoFallback() async {
    latitude = 30.0444;
    longitude = 31.2357;
    isDefaultLocation = true;
    debugPrint('🟡 Using default Cairo coordinates');
    await saveLocation(latitude!, longitude!, isDefaultLocation);
    await adhan();
  }

  /// 🔹 حساب المواقيت وإرسالها إلى كوتلن
  Future<void> adhan() async {
    if (latitude == null || longitude == null) {
      await useCairoFallback();
    }

    final myCoordinates = Coordinates(latitude!, longitude!);
    final param = CalculationMethod.egyptian.getParameters();
    param.madhab = Madhab.shafi;

    prayerTimes = PrayerTimes.today(myCoordinates, param);
    update();

    final timesMap = {
      'fajr': prayerTimes!.fajr.millisecondsSinceEpoch,
      'dhuhr': prayerTimes!.dhuhr.millisecondsSinceEpoch,
      'asr': prayerTimes!.asr.millisecondsSinceEpoch,
      'maghrib': prayerTimes!.maghrib.millisecondsSinceEpoch,
      'isha': prayerTimes!.isha.millisecondsSinceEpoch,
    };

    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('last_prayer_times', jsonEncode(timesMap));

    try {
      await NativeAdhanBridge.schedulePrayerTimes(timesMap);
      debugPrint('✅ Prayer times scheduled successfully');
    } catch (e) {
      debugPrint('⚠️ Failed to schedule in Kotlin: $e');
    }
  }

  /// 🔹 حفظ الموقع
  Future<void> saveLocation(double lat, double lng, bool isDefault) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setDouble('lat', lat);
    await prefs.setDouble('lng', lng);
    await prefs.setBool('isDefaultLocation', isDefault);
    await NativeAdhanBridge.saveLocationToNative(lat, lng);
    debugPrint(
      '✅ Location saved: $lat, $lng | Default: ${isDefault ? "Yes" : "No"}',
    );
  }
}
