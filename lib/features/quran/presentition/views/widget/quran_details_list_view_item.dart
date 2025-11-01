import 'package:flutter/material.dart';
import 'package:quran_app_android/core/util/color.dart';
import 'package:quran_app_android/features/quran/data/models/details_model.dart';
import 'package:quran_app_android/features/quran/presentition/view_model/quran_screen_model_details.dart';

class QuranDetailsPageViewItem extends StatelessWidget {
  const QuranDetailsPageViewItem({
    super.key,
    required this.pageIndex,
    required this.totalPages,
    required this.verses,
    required this.controller,
    required this.surahIndex,
    required this.showBasmalah,
    required this.isBookmarkedPage,
  });

  final int pageIndex;
  final int totalPages;
  final List<VersesModel> verses;
  final QuranScreenViewModel controller;
  final int surahIndex;
  final bool showBasmalah;
  final bool isBookmarkedPage;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    // final String surahName = controller.ayah_Model[surahIndex].name ?? 'السورة';
    return LayoutBuilder(
      builder: (context, constraints) {
        return Container(
          margin: const EdgeInsets.symmetric(horizontal: 0, vertical: 0),
          padding: const EdgeInsets.symmetric(horizontal: 28, vertical: 30),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(30),
            border:
                isBookmarkedPage
                    ? Border.all(
                      color: AppColors.kPrimaryColor.withOpacity(0.8),
                      width: 1.6,
                    )
                    : null,
            gradient: const LinearGradient(
              colors: [Color(0xFFFDF8EE), Color(0xFFFFFFFF), Color(0xFFE8F2FF)],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.08),
                blurRadius: 18,
                offset: const Offset(0, 12),
              ),
            ],
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 16,
                      vertical: 8,
                    ),
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(18),
                      color: AppColors.kPrimaryColor.withOpacity(0.12),
                    ),
                    child: Text(
                      'صفحة ${pageIndex + 1} / $totalPages',
                      style: theme.textTheme.bodyMedium?.copyWith(
                        fontWeight: FontWeight.w600,
                        color: AppColors.kPrimaryColor,
                      ),
                    ),
                  ),
                  const Spacer(),
                  if (isBookmarkedPage)
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 12,
                        vertical: 6,
                      ),
                      decoration: BoxDecoration(
                        color: AppColors.kPrimaryColor.withOpacity(0.18),
                        borderRadius: BorderRadius.circular(16),
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Icon(
                            Icons.bookmark,
                            size: 18,
                            color: AppColors.kPrimaryColor,
                          ),
                          const SizedBox(width: 6),
                          Text(
                            'محفوظ',
                            style: theme.textTheme.bodySmall?.copyWith(
                              color: AppColors.kPrimaryColor,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ],
                      ),
                    ),
                  if (isBookmarkedPage) const SizedBox(width: 12),
                  // Text(
                  //   surahName,
                  //   style: theme.textTheme.bodyLarge?.copyWith(
                  //     fontWeight: FontWeight.bold,
                  //     color: Colors.black87,
                  //   ),
                  // ),
                ],
              ),
              const SizedBox(height: 20),
              if (showBasmalah)
                Padding(
                  padding: const EdgeInsets.only(bottom: 18.0),
                  child: Column(
                    children: [
                      Text(
                        verses.first.text ?? '',
                        textAlign: TextAlign.center,
                        style: TextStyle(
                          fontSize: controller.fontSize + 2,
                          fontFamily: 'Kitab',
                          color: Colors.black87,
                        ),
                      ),
                      const SizedBox(height: 6),
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 10,
                          vertical: 4,
                        ),
                        decoration: BoxDecoration(
                          color: AppColors.kPrimaryColor.withOpacity(0.18),
                          borderRadius: BorderRadius.circular(14),
                        ),
                        child: Text(
                          controller.replaceFarsiNumber(
                            (verses.first.id ?? 0).toString(),
                          ),
                          style: const TextStyle(
                            fontSize: 14,
                            fontFamily: 'Quran',
                            color: AppColors.kPrimaryColor,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              Expanded(
                child: SingleChildScrollView(
                  physics: const BouncingScrollPhysics(),
                  child: Directionality(
                    textDirection: TextDirection.rtl,
                    child: Text.rich(
                      TextSpan(children: _buildVerseSpans()),
                      textAlign: TextAlign.justify,
                    ),
                  ),
                ),
              ),
              const SizedBox(height: 20),
              Align(
                alignment: Alignment.center,
                child: Container(
                  width: 120,
                  height: 4,
                  decoration: BoxDecoration(
                    color: AppColors.kPrimaryColor.withOpacity(0.25),
                    borderRadius: BorderRadius.circular(50),
                  ),
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  List<InlineSpan> _buildVerseSpans() {
    final List<InlineSpan> spans = [];
    final Iterable<VersesModel> versesToRender =
        showBasmalah ? verses.skip(1) : verses;
    for (final verse in versesToRender) {
      final String verseNumber = controller.replaceFarsiNumber(
        (verse.id ?? 0).toString(),
      );
      spans.add(
        TextSpan(
          text: '${verse.text} ',
          style: TextStyle(
            fontSize: controller.fontSize,
            height: 1.8,
            fontFamily: 'Kitab',
            color: Colors.black87,
          ),
        ),
      );
      spans.add(
        WidgetSpan(
          alignment: PlaceholderAlignment.middle,
          child: Container(
            margin: const EdgeInsets.symmetric(horizontal: 4, vertical: 8),
            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
            decoration: BoxDecoration(
              color: AppColors.kPrimaryColor.withOpacity(0.18),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Text(
              verseNumber,
              style: const TextStyle(
                fontSize: 14,
                fontFamily: 'Quran',
                color: AppColors.kPrimaryColor,
              ),
            ),
          ),
        ),
      );
      spans.add(const WidgetSpan(child: SizedBox(width: 6)));
    }
    return spans;
  }
}
