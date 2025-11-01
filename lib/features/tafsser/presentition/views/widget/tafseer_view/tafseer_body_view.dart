import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:quran_app_android/core/service/settings/SettingsServices.dart';
import 'package:quran_app_android/features/quran/presentition/view_model/quran_screen_model_details.dart';
import 'package:quran_app_android/features/quran/presentition/view_model/quran_view_model.dart';
import 'package:quran_app_android/features/tafsser/presentition/view_model.dart/tafseer_details_view_model.dart';
import 'package:quran_app_android/features/tafsser/presentition/views/widget/tafseer_view/tafseer_view_item.dart';

class TafseerBodyView extends StatelessWidget {
  const TafseerBodyView({
    super.key,
    required this.quranScreenViewModel,
    required this.settingsServices,
  });
  final QuranScreenViewModel quranScreenViewModel;
  final SettingsServices settingsServices;
  QuranViewModel get _quranViewModel => Get.find<QuranViewModel>();
  TafseerDetailsViewModel get _tafseerDetailsViewModel =>
      Get.find<TafseerDetailsViewModel>();
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 20.0, left: 20.0, right: 20.0),
      child: Column(
        children: [
          Expanded(
            child: GetBuilder<QuranViewModel>(
              init: _quranViewModel,
              autoRemove: false,
              builder: (controller) {
                return GetBuilder<TafseerDetailsViewModel>(
                  init: _tafseerDetailsViewModel,
                  autoRemove: false,
                  builder: (controllerTafseerDetailsViewModel) {
                    return ListView.separated(
                        scrollDirection: Axis.vertical,
                        physics: const BouncingScrollPhysics(),
                        itemBuilder: (context, index) {
                          return Padding(
                            padding: const EdgeInsets.symmetric(vertical: 8.0),
                            child: TafseerViewItem(
                                index: index,
                                model: controller.nameModel[index],
                                quranScreenViewModel: quranScreenViewModel,
                                settingsServices: settingsServices),
                          );
                        },
                        separatorBuilder: (context, index) => const SizedBox(),
                        itemCount: controller.nameModel.length);
                  }
                );
              },
            ),
          )
        ],
      ),
    );
  }
}
