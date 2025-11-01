import 'package:flutter/cupertino.dart';
import 'package:quran_app_android/core/service/settings/SettingsServices.dart';
import 'package:quran_app_android/features/quran/data/models/details_model.dart';
import 'package:quran_app_android/features/quran/presentition/view_model/quran_screen_model_details.dart';
import 'package:quran_app_android/features/quran/presentition/views/widget/quran_details_list_view_item.dart';

class QuranDetailsListView extends StatelessWidget {
  const QuranDetailsListView({
    super.key,
    required this.sharedPref,
    required this.controller,
  });
  final SettingsServices sharedPref;
  final QuranScreenViewModel controller;

  @override
  Widget build(BuildContext context) {
    if (sharedPref.sharedPref == null ||
        controller.ayah_Model.isEmpty ||
        sharedPref.sharedPref!.getInt('indexQuran') == null) {
      return const Column(
        children: [
          Expanded(
            child: Center(child: Text('لم يتم العثور على بيانات السورة')),
          ),
        ],
      );
    }

    final int surahIndex = sharedPref.sharedPref!
        .getInt('indexQuran')!
        .toInt()
        .clamp(0, controller.ayah_Model.length - 1);
    final List<List<VersesModel>> pages = controller.getPagedVersesFor(
      surahIndex,
    );
    Widget content;
    if (controller.isLoading.value) {
      content = const Center(child: CupertinoActivityIndicator());
    } else if (pages.isEmpty) {
      content = const Center(child: Text('لا توجد آيات للعرض في هذه السورة'));
    } else {
      controller.ensurePageWithinBounds(surahIndex);
      content = Container(
        margin: const EdgeInsets.all(20),
        decoration: BoxDecoration(
          // color: Colors.blue.shade100.withOpacity(.35),
          borderRadius: BorderRadius.circular(25.0),
        ),
        child: PageView.builder(
          controller: controller.detailsPageController,
          physics: const BouncingScrollPhysics(),
          onPageChanged: controller.updateDetailsPage,
          itemCount: pages.length,
          itemBuilder: (context, pageIndex) {
            final List<VersesModel> pageVerses = pages[pageIndex];
            final bool showBasmalah =
                pageIndex == 0 && controller.shouldShowBasmalah(surahIndex);
            final bool isBookmarkedPage = controller.isBookmarkedPage(
              surahIndex,
              pageIndex,
            );
            return QuranDetailsPageViewItem(
              pageIndex: pageIndex,
              totalPages: pages.length,
              verses: pageVerses,
              controller: controller,
              surahIndex: surahIndex,
              showBasmalah: showBasmalah,
              isBookmarkedPage: isBookmarkedPage,
            );
          },
        ),
      );
    }
    return Column(children: [Expanded(child: content)]);
  }
}
