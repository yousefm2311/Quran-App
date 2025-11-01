import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:quran_app_android/core/service/settings/SettingsServices.dart';
import 'package:quran_app_android/core/util/app_url.dart';
import 'package:quran_app_android/features/quran/data/models/model.dart';

class QuranViewModel extends GetxController {
  final SettingsServices settingsServices = Get.find<SettingsServices>();
  late final PageController pageController;
  int currentPage = 0;
  bool isLoading = false;
  List<dynamic> items = [];
  List<NameModel> nameModel = [];

  final TextEditingController searchController = TextEditingController();

  @override
  void onInit() {
    super.onInit();
    final savedPage =
        settingsServices.sharedPref?.getInt('lastVisitedSurahPage') ?? 0;
    currentPage = savedPage;
    pageController = PageController(initialPage: savedPage);
    readJson();
  }

  @override
  void onClose() {
    pageController.dispose();
    searchController.dispose();
    super.onClose();
  }

  Future<void> readJson() async {
    try {
      isLoading = true;
      update();
      final String response = await rootBundle.loadString(AppUrl.nameQuranUrl);
      final data = json.decode(response) as List<dynamic>;
      items = data;
      nameModel
        ..clear()
        ..addAll(
          data.map(
            (item) => NameModel.fromJson(item as Map<String, dynamic>),
          ),
        );
      if (nameModel.isNotEmpty) {
        final int safePage =
            currentPage.clamp(0, nameModel.length - 1);
        if (safePage != currentPage) {
          currentPage = safePage;
        }
        if (pageController.hasClients) {
          pageController.jumpToPage(currentPage);
        } else {
          WidgetsBinding.instance.addPostFrameCallback((_) {
            if (pageController.hasClients) {
              pageController.jumpToPage(currentPage);
            }
          });
        }
      }
    } finally {
      isLoading = false;
      update();
    }
  }

  void onPageChanged(int index) {
    currentPage = index;
    settingsServices.sharedPref?.setInt('lastVisitedSurahPage', index);
    update();
  }

  // List<NameModel> searchResult = [];
  // onSearchTextChange(String text) async {
  //   searchResult.clear();
  //   if (text.isEmpty) {
  //     update();
  //     return;
  //   }
  //   for (var element in nameModel) {
  //     if (element.name!.contains(text.capitalizeFirst!) ||
  //         element.transliteration!.contains(text.capitalizeFirst!)) {
  //       searchResult.add(element);
  //       update();
  //     }
  //   }
  // }
}
