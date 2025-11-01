import 'dart:math';

import 'package:flutter/material.dart';
import 'package:quran_app_android/core/util/assets.dart';
import 'package:quran_app_android/core/util/color.dart';
import 'package:quran_app_android/features/quran/data/models/model.dart';

class QuranPageCard extends StatelessWidget {
  const QuranPageCard({
    super.key,
    required this.index,
    required this.model,
    required this.isSaved,
    required this.onOpen,
    required this.constraints,
  });

  final int index;
  final NameModel model;
  final bool isSaved;
  final VoidCallback onOpen;
  final BoxConstraints constraints;

  @override
  Widget build(BuildContext context) {
    final double maxWidth = min(constraints.maxWidth, 480);
    return Center(
      child: Stack(
        children: [
          Positioned.fill(
            child: DecoratedBox(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(28.0),
                gradient: LinearGradient(
                  colors: [
                    const Color(0xFFFDF8EE),
                    Colors.white,
                    const Color(0xFFE6F1FF),
                  ],
                  begin: Alignment.centerLeft,
                  end: Alignment.centerRight,
                ),
              ),
            ),
          ),
          Positioned.fill(
            child: Align(
              alignment: Alignment.center,
              child: FractionallySizedBox(
                widthFactor: 0.04,
                child: Container(
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      colors: [
                        Colors.black.withOpacity(0.05),
                        Colors.black.withOpacity(0.0),
                        Colors.black.withOpacity(0.05),
                      ],
                      begin: Alignment.centerLeft,
                      end: Alignment.centerRight,
                    ),
                  ),
                ),
              ),
            ),
          ),
          Container(
            width: maxWidth,
            margin: const EdgeInsets.symmetric(horizontal: 6.0, vertical: 14.0),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(28.0),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.08),
                  blurRadius: 20,
                  offset: const Offset(0, 14),
                ),
              ],
            ),
            child: Material(
              borderRadius: BorderRadius.circular(28.0),
              color: Colors.transparent,
              child: InkWell(
                borderRadius: BorderRadius.circular(28.0),
                onTap: onOpen,
                child: Padding(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 28.0,
                    vertical: 28.0,
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      _buildHeader(context),
                      const SizedBox(height: 24),
                      _buildNamesSection(context),
                      const SizedBox(height: 24),
                      _buildMetaSection(context),
                      const SizedBox(height: 28),
                      _buildHintRow(context),
                    ],
                  ),
                ),
              ),
            ),
          ),
          Positioned(
            top: 18,
            right: 40,
            child: Image.asset(
              AssetsData.ayah,
              color: AppColors.kPrimaryColor.withOpacity(0.12),
              width: 64,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildHeader(BuildContext context) {
    return Row(
      children: [
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
          decoration: BoxDecoration(
            shape: BoxShape.rectangle,
            borderRadius: BorderRadius.circular(16),
            color: AppColors.kPrimaryColor.withOpacity(0.1),
          ),
          child: Text(
            '${index + 1}',
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
              fontWeight: FontWeight.bold,
              fontSize: 18,
              color: AppColors.kPrimaryColor,
            ),
          ),
        ),
        const SizedBox(width: 14),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                model.transliteration ?? '',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.w600,
                  fontSize: 18,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                _buildTypeLabel(),
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: Colors.grey.shade600,
                  fontSize: 13,
                ),
              ),
            ],
          ),
        ),
        if (isSaved) _buildBookmarkChip(context),
      ],
    );
  }

  Widget _buildBookmarkChip(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: AppColors.kPrimaryColor.withOpacity(0.16),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: AppColors.kPrimaryColor.withOpacity(0.4)),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Icon(Icons.bookmark, size: 18, color: AppColors.kPrimaryColor),
          const SizedBox(width: 6),
          Text(
            'محفوظة',
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              color: AppColors.kPrimaryColor,
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildNamesSection(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          model.name ?? '',
          style: Theme.of(context).textTheme.displaySmall?.copyWith(
            fontSize: 32,
            fontFamily: 'Kitab',
            color: Colors.black87,
          ),
          textDirection: TextDirection.rtl,
        ),
        const SizedBox(height: 6),
        Text(
          model.transliteration ?? '',
          style: Theme.of(
            context,
          ).textTheme.titleMedium?.copyWith(color: Colors.grey.shade700),
        ),
      ],
    );
  }

  Widget _buildMetaSection(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        _MetaTile(
          label: 'عدد الآيات',
          value: model.total_verses?.toString() ?? '--',
        ),
        _MetaTile(
          label: 'نوع السورة',
          value:
              model.type != null && model.type!.isNotEmpty
                  ? (model.type == 'meccan' ? 'مكية' : 'مدنية')
                  : '--',
        ),
        _MetaTile(label: 'الترتيب', value: '${index + 1}'),
      ],
    );
  }

  Widget _buildHintRow(BuildContext context) {
    return Row(
      children: [
        const Icon(Icons.swipe_left, color: AppColors.kPrimaryColor),
        const SizedBox(width: 10),
        Expanded(
          child: Text(
            'اضغط على الصفحة لبدء التلاوة • اسحب للتنقل مثل تقليب صفحات المصحف',
            style: Theme.of(
              context,
            ).textTheme.bodySmall?.copyWith(color: Colors.grey.shade700),
          ),
        ),
        const Icon(Icons.swipe_right, color: AppColors.kPrimaryColor),
      ],
    );
  }

  String _buildTypeLabel() {
    if (model.type == null) {
      return '${model.total_verses ?? '--'} آيات';
    }
    final String typeLabel =
        model.type == 'meccan'
            ? 'مكية'
            : model.type == 'medinan'
            ? 'مدنية'
            : model.type!;
    return '$typeLabel • ${model.total_verses ?? '--'} آيات';
  }
}

class _MetaTile extends StatelessWidget {
  const _MetaTile({required this.label, required this.value});

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: AppColors.kPrimaryColor.withOpacity(0.2)),
        color: Colors.white.withOpacity(0.65),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            value,
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
              fontWeight: FontWeight.bold,
              color: AppColors.kPrimaryColor,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            label,
            style: Theme.of(
              context,
            ).textTheme.bodySmall?.copyWith(color: Colors.grey.shade700),
          ),
        ],
      ),
    );
  }
}
