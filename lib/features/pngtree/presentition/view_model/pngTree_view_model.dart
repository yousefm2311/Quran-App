// ignore_for_file: file_names
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:shared_preferences/shared_preferences.dart';

class PngTreeViewModel extends GetxController {
  int counter = 0;
  int counterTree = 0;

  @override
  void onInit() {
    super.onInit();
    loadData();
  }

  Future<void> loadData() async {
    final prefs = await SharedPreferences.getInstance();
    counter = prefs.getInt('counter') ?? 0;
    counterTree = prefs.getInt('counterTree') ?? 0;
    update();
  }

  Future<void> saveData() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setInt('counter', counter);
    await prefs.setInt('counterTree', counterTree);
  }

  void increaseCounter() async {
    counter++;
    HapticFeedback.lightImpact(); // اهتزاز خفيف عند التسبيح

    if (counter == 33) {
      counterTree += 1;
      counter = 0;
      HapticFeedback.mediumImpact(); // اهتزاز أقوى عند 33
    }
    await saveData();
    update();
  }

  void decreaseCounter() async {
    if (counter > 0) {
      counter--;
      HapticFeedback.selectionClick(); // اهتزاز بسيط وقت الإنقاص
      await saveData();
      update();
    }
  }

  void clearConter() async {
    counter = 0;
    counterTree = 0;
    HapticFeedback.vibrate();
    await saveData();
    update();
  }
}
