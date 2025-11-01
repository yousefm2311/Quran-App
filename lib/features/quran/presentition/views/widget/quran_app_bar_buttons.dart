import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:quran_app_android/core/service/settings/SettingsServices.dart';
import 'package:quran_app_android/core/util/color.dart';
import 'package:quran_app_android/core/util/widgets/custom_toast.dart';
import 'package:quran_app_android/features/quran/data/models/details_model.dart';
import 'package:quran_app_android/features/quran/presentition/view_model/quran_screen_model_details.dart';

class QuranAppBarButtons extends StatelessWidget {
  const QuranAppBarButtons(
      {super.key,
      required this.sharedPref,
      required this.quranScreenViewModel});
  final SettingsServices sharedPref;
  final QuranScreenViewModel quranScreenViewModel;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        GetBuilder<QuranScreenViewModel>(
            init: quranScreenViewModel,
            autoRemove: false,
            builder: (cont) {
          final prefs = sharedPref.sharedPref;
          final int currentIndex = prefs?.getInt('indexQuran') ?? 0;
          final bool sameSurah = prefs?.getInt('currentIndex4Quran') != null &&
              cont.ayah_Model.isNotEmpty &&
              currentIndex < cont.ayah_Model.length &&
              prefs!.getInt('currentIndex4Quran') ==
                  cont.ayah_Model[currentIndex].id;
          final bool savedVerseMatches = prefs?.getInt('currentIndex3Quran') !=
                  null &&
              cont.ayahModell?.id != null &&
              prefs!.getInt('currentIndex3Quran') == cont.ayahModell!.id;
          final bool samePage =
              (prefs?.getInt('detailsPageIndex') ?? 0) == cont.currentDetailsPage;
          final bool isBookmarked = sameSurah && savedVerseMatches && samePage;

          return GestureDetector(
            onTap: () {
              addBookMark(cont);
            },
            child: Icon(
              isBookmarked ? Icons.bookmark : Icons.bookmark_border_rounded,
              size: 28.0,
              color: AppColors.kPrimaryColor,
            ),
          );
        }),
        const SizedBox(width: 20),
        GestureDetector(
          onTap: () {
            if (quranScreenViewModel.fontSize >= 14) {
              quranScreenViewModel.decreaseFont();
            } else {
              defaultToast(text: 'الخط صغير جداا');
            }
          },
          child: const Icon(
            Icons.remove_circle_outline_rounded,
            color: AppColors.kPrimaryColor,
          ),
        ),
        const SizedBox(width: 20),
        GestureDetector(
          onTap: () async {
            if (quranScreenViewModel.fontSize <= 24) {
              quranScreenViewModel.increaseFont();
            } else {
              await defaultToast(text: 'الخط كبير جداا');
            }
          },
          child: const Icon(
            Icons.add_circle_outline_rounded,
            color: AppColors.kPrimaryColor,
          ),
        ),
      ],
    );
  }

  void addBookMark(QuranScreenViewModel cont) {
    final prefs = sharedPref.sharedPref;
    if (prefs == null) {
      defaultToast(text: 'تعذر الوصول إلى بيانات الحفظ حالياً');
      return;
    }
    final int currentIndex = prefs.getInt('indexQuran') ?? 0;
    if (cont.ayah_Model.isEmpty ||
        currentIndex < 0 ||
        currentIndex >= cont.ayah_Model.length) {
      defaultToast(text: 'جاري تحميل السورة، حاول مرة أخرى بعد لحظات');
      return;
    }
    if (cont.ayah_Model[currentIndex].verses.isEmpty) {
      defaultToast(text: 'تعذر تحديد الآية الحالية');
      return;
    }
    final VersesModel activeVerse = cont.resolveCurrentBookmarkVerse();

    quranScreenViewModel.currentSave = prefs.getBool('Save') ?? false;
    cont.addBookMarkQuran(
      activeVerse.id,
      activeVerse.text,
      activeVerse.translation,
    );
    cont.changeIndex3Quran(activeVerse.id ?? 0);
    cont.changeIndex4Quran(cont.ayah_Model[currentIndex].id ?? 0);
    cont.getCurrentMarkQuran();
    prefs.setString(
      'lastRead',
      '${cont.ayah_Model[currentIndex].transliteration} - آية ${cont.replaceFarsiNumber((activeVerse.id ?? 0).toString())}',
    );
    if (!quranScreenViewModel.currentSave) {
      _showFirstTimeDialog().then((_) {
        prefs.setBool('Save', true);
      });
    }
    defaultToast(text: 'تم حفظ موضع التلاوة');
  }

  Future<void> _showFirstTimeDialog() {
    return Get.defaultDialog(
      titleStyle: const TextStyle(color: Colors.black87),
      title: 'حفظ',
      content: const Text(
        'للحفظ مرة أخرى اضغط على نفس الزر',
        style: TextStyle(color: Colors.black87),
      ),
      middleText: '',
    );
  }

}
