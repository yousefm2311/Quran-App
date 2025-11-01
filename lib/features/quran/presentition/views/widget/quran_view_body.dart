import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:quran_app_android/core/service/settings/SettingsServices.dart';
import 'package:quran_app_android/core/util/routes/routes.dart';
import 'package:quran_app_android/features/quran/data/models/model.dart';
import 'package:quran_app_android/features/quran/presentition/view_model/quran_screen_model_details.dart';
import 'package:quran_app_android/features/quran/presentition/view_model/quran_view_model.dart';
import 'package:quran_app_android/features/quran/presentition/views/widget/quran_view_body_items.dart';

class QuranViewBody extends StatelessWidget {
  const QuranViewBody(
      {super.key,
      required this.settingsServices,
      required this.quranScreenViewModel});

  final SettingsServices settingsServices;
  final QuranScreenViewModel quranScreenViewModel;
  QuranViewModel get _quranViewModel => Get.put(QuranViewModel());

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        return Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 12.0),
          child: GetBuilder<QuranViewModel>(
            init: _quranViewModel,
            autoRemove: false,
            builder: (controller) {
              if (controller.isLoading) {
                return const Center(child: CircularProgressIndicator());
              }
              if (controller.nameModel.isEmpty) {
                return const Center(child: Text('لم يتم تحميل السور بعد'));
              }

              return Column(
                children: [
                  Expanded(
                    child: PageView.builder(
                      controller: controller.pageController,
                      physics: const BouncingScrollPhysics(),
                      onPageChanged: controller.onPageChanged,
                      itemCount: controller.nameModel.length,
                      itemBuilder: (context, index) {
                        final model = controller.nameModel[index];
                        final bool isSaved =
                            settingsServices.sharedPref?.getInt(
                                  "currentIndex4Quran",
                                ) ==
                                model.id;
                        return AnimatedBuilder(
                          animation: controller.pageController,
                          builder: (context, child) {
                            double scale = 1.0;
                            if (controller.pageController.hasClients) {
                              final currentPage =
                                  controller.pageController.page ??
                                      controller.currentPage.toDouble();
                              scale = (1 - (currentPage - index).abs() * 0.08)
                                  .clamp(0.92, 1.0);
                            }
                            return Transform.scale(
                              scale: scale,
                              child: child,
                            );
                          },
                          child: QuranPageCard(
                            index: index,
                            model: model,
                            isSaved: isSaved,
                            onOpen: () => _openSurah(index, model),
                            constraints: constraints,
                          ),
                        );
                      },
                    ),
                  ),
                  const SizedBox(height: 18),
                  _buildFooter(context),
                ],
              );
            },
          ),
        );
      },
    );
  }

  Widget _buildFooter(BuildContext context) {
    final quranViewModel = _quranViewModel;
    if (quranViewModel.nameModel.isEmpty) {
      return const SizedBox.shrink();
    }
    final current = quranViewModel
        .nameModel[quranViewModel.currentPage.clamp(0, quranViewModel.nameModel.length - 1)];
    return Column(
      children: [
        Text(
          'اسحب يميناً ويساراً للتنقل بين السور',
          style: Theme.of(context)
              .textTheme
              .bodyMedium
              ?.copyWith(color: Colors.grey.shade600),
        ),
        const SizedBox(height: 10),
        Container(
          padding:
              const EdgeInsets.symmetric(horizontal: 18.0, vertical: 12.0),
          decoration: BoxDecoration(
            color: Colors.blueGrey.shade50,
            borderRadius: BorderRadius.circular(16.0),
          ),
          child: Text(
            'سورة ${current.name} • ${current.transliteration}',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
            textAlign: TextAlign.center,
          ),
        ),
      ],
    );
  }

  Future<void> _openSurah(int index, NameModel model) async {
    final prefs = settingsServices.sharedPref;
    prefs?.setInt('indexQuran', index);

    if (quranScreenViewModel.ayah_Model.isEmpty) {
      await quranScreenViewModel.readJson();
    }

    final int? currentSurahId = index >= 0 &&
            index < quranScreenViewModel.ayah_Model.length
        ? quranScreenViewModel.ayah_Model[index].id
        : null;
    final int? savedSurahId = prefs?.getInt('currentIndex4Quran');
    final int? savedPageIndex = prefs?.getInt('detailsPageIndex');
    final int? bookmarkPage =
        quranScreenViewModel.bookmarkPageIndexFor(index);
    final bool sameSurahAsSaved =
        savedSurahId != null && currentSurahId != null && savedSurahId == currentSurahId;
    final int initialPage = bookmarkPage ??
        (sameSurahAsSaved && savedPageIndex != null ? savedPageIndex : 0);

    prefs?.setInt('detailsPageIndex', initialPage);
    quranScreenViewModel.resetDetailsPaging(initialPage: initialPage);
    await Get.toNamed(AppRoutes.detailsScreen);
    settingsServices.sharedPref?.setString(
      'lastRead',
      '${model.transliteration} - ${model.name}',
    );
  }
}
