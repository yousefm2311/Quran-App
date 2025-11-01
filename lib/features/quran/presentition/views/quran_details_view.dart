// ignore_for_file: must_be_immutable, dead_code, deprecated_member_use
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:quran_app_android/core/service/settings/SettingsServices.dart';
import 'package:quran_app_android/core/util/color.dart';
import 'package:quran_app_android/core/util/widgets/custom_appBar.dart';
import 'package:quran_app_android/core/util/widgets/custom_back_button.dart';
import 'package:quran_app_android/core/util/widgets/my_text.dart';
import 'package:quran_app_android/features/quran/presentition/view_model/quran_screen_model_details.dart';
import 'package:quran_app_android/features/quran/presentition/views/widget/quran_app_bar_buttons.dart';
import 'package:quran_app_android/features/quran/presentition/views/widget/quran_details_list_view.dart';

class DetailsScreen extends StatelessWidget {
  DetailsScreen({super.key});
  SettingsServices sharedPref = Get.find<SettingsServices>();
  QuranScreenViewModel quranScreenViewModel =
      Get.find<QuranScreenViewModel>();
  @override
  Widget build(BuildContext context) {
    goToLastPosition();
    return Scaffold(
      resizeToAvoidBottomInset: false,
      backgroundColor: AppColors.kbackGroundColor,
      appBar: CustomAppBar(
        loading: const CustomBackButton(),
        action: [
          Padding(
              padding: const EdgeInsets.only(left: 20.0, right: 20.0),
              child: QuranAppBarButtons(
                sharedPref: sharedPref,
                quranScreenViewModel: quranScreenViewModel,
              )),
        ],
        title: MyText(
          text: sharedPref.sharedPref!.getInt('indexQuran') != null
              ? '${quranScreenViewModel.ayah_Model[sharedPref.sharedPref!.getInt('indexQuran')!.toInt()].name}'
              : '',
          textStyle: Theme.of(context).textTheme.bodyMedium!.copyWith(
                fontSize: 22,
                fontFamily: 'Rubik',
                fontWeight: FontWeight.bold,
                color: Colors.black,
              ),
        ),
        centerTitle: true,
      ),
      body: GetBuilder<QuranScreenViewModel>(
        init: quranScreenViewModel,
        autoRemove: false,
        builder: (controller) {
          return QuranDetailsListView(
              sharedPref: sharedPref, controller: controller);
        },
      ),
    );
  }

  void goToLastPosition() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final prefs = sharedPref.sharedPref;
      if (prefs == null || quranScreenViewModel.ayah_Model.isEmpty) {
        return;
      }
      final int? savedVerseId = prefs.getInt("currentIndex3Quran");
      final int? savedSurahId = prefs.getInt("currentIndex4Quran");
      final int currentSurahIndex = prefs.getInt('indexQuran') ?? 0;
      final int? currentSurahId = currentSurahIndex >= 0 &&
              currentSurahIndex < quranScreenViewModel.ayah_Model.length
          ? quranScreenViewModel.ayah_Model[currentSurahIndex].id
          : null;

      if (savedVerseId != null &&
          savedSurahId != null &&
          quranScreenViewModel.ayahModel?.id == savedVerseId &&
          currentSurahId == savedSurahId) {
        quranScreenViewModel.jumpToSavedPageIfBookmarkMatches();
      }
    });
  }
}
