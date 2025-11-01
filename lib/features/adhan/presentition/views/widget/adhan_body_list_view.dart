import 'package:adhan/adhan.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:quran_app_android/features/adhan/presentition/view_model/adhan_view_model.dart';
import 'package:quran_app_android/features/adhan/presentition/views/widget/adhan_view_data.dart';

class AdhanBodyListView extends StatelessWidget {
  const AdhanBodyListView({super.key, required this.controller});
  final AdhanViewModel controller;
  @override
  Widget build(BuildContext context) {
    final prayerTimes = controller.prayerTimes;
    if (prayerTimes == null) {
      return const Expanded(
        child: Center(
          child: CircularProgressIndicator(),
        ),
      );
    }
    return Expanded(
      child: ListView(
        physics: const BouncingScrollPhysics(),
        children: [
          PrayerTimeItem(
              time: formateDate(prayerTimes.fajr),
              title: 'الفجر',
              isCurrent: _isCurrentTime(
                  prayerTimes, prayerTimes.fajr, prayerTimes.dhuhr)),
          PrayerTimeItem(
              time: formateDate(prayerTimes.dhuhr),
              title: 'الظهر',
              isCurrent:
                  _isCurrentTime(prayerTimes, prayerTimes.dhuhr, prayerTimes.asr)),
          PrayerTimeItem(
              time: formateDate(prayerTimes.asr),
              title: 'العصر',
              isCurrent: _isCurrentTime(
                  prayerTimes, prayerTimes.asr, prayerTimes.maghrib)),
          PrayerTimeItem(
              time: formateDate(prayerTimes.maghrib),
              title: 'المغرب',
              isCurrent: _isCurrentTime(
                  prayerTimes, prayerTimes.maghrib, prayerTimes.isha)),
          PrayerTimeItem(
              time: formateDate(prayerTimes.isha),
              title: 'العشاء',
              isCurrent: _isCurrentTime(
                  prayerTimes, prayerTimes.isha, prayerTimes.fajr)),
        ],
      ),
    );
  }

  String formateDate(DateTime date) {
    return DateFormat.jm().format(date);
  }

  bool _isCurrentTime(
      PrayerTimes prayerTimes, DateTime prayerTime, DateTime nextPrayerTime) {
    final now = DateTime.now();
    if (now.isAfter(prayerTime) && now.isBefore(nextPrayerTime)) {
      return true;
    } else if (now.isAfter(prayerTimes.isha) ||
        now.isBefore(prayerTimes.fajr)) {
      return true;
    }
    return false;
  }
}
