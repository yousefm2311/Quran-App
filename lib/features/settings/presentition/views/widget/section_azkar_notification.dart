import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:quran_app_android/core/service/settings/notifications_services.dart';
import 'package:quran_app_android/core/util/color.dart';
import 'package:quran_app_android/core/util/widgets/custom_toast.dart';
import 'package:quran_app_android/features/settings/presentition/view_model/settins_view_model.dart';

class SectionAzkarNotification extends StatefulWidget {
  final SettingsViewModel controller;
  const SectionAzkarNotification({super.key, required this.controller});

  @override
  State<SectionAzkarNotification> createState() =>
      _SectionAzkarNotificationState();
}

class _SectionAzkarNotificationState extends State<SectionAzkarNotification> {
  @override
  Widget build(BuildContext context) {
    final controller = widget.controller;
    controller.checkNotificationPermissions();

    return Container(
      padding: const EdgeInsets.all(20.0),
      decoration: BoxDecoration(
        color: Colors.white38,
        borderRadius: BorderRadius.circular(12.0),
      ),
      child: Row(
        children: [
          CupertinoSwitch(
            activeTrackColor: AppColors.kPrimaryColor,
            value:
                controller.settingsServices.sharedPref!.getBool('enable') ?? true,
            onChanged: (value) async {
              await switchMethod(controller, value);
              setState(() {});
            },
          ),
          const Spacer(),
          GestureDetector(
            onTap: () async {
              await showTimePickerMethod(context, controller);
            },
            child: Text(
              'تعديل وايقاف اشعار ذكر الصباح',
              style: Theme.of(context).textTheme.bodyMedium!.copyWith(
                    color: Colors.black87,
                    fontSize: 18,
                    fontFamily: 'Rubik',
                    fontWeight: FontWeight.w600,
                  ),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> switchMethod(SettingsViewModel controller, bool value) async {
    if (controller.timeOfDay == null &&
        controller.settingsServices.sharedPref!.getBool('enable') == false) {
      controller.toggleSwitch(value);
      defaultToast(text: 'تم تعيين الوقت الافتراضي');
    }
    controller.toggleSwitch(value);

    if (controller.settingsServices.sharedPref!.getBool('enable') == true) {
      defaultToast(text: 'تم تفعيل اشعار ذكر الصباح');
      await NotifyHelper().scheduleAzkar(
        timeOfDay: controller.timeOfDay ?? const TimeOfDay(hour: 8, minute: 0),
      );
    } else {
      defaultToast(text: 'تم ايقاف اشعار ذكر الصباح');
      await NotifyHelper().flutterLocalNotificationsPlugin.cancel(1);
    }
  }

  Future<void> showTimePickerMethod(
      BuildContext context, SettingsViewModel controller) async {
    try {
      final selectedTime = await showTimePicker(
        context: context,
        initialTime: controller.timeOfDay ?? TimeOfDay.now(),
      );

      if (selectedTime == null) {
        defaultToast(text: 'تم إلغاء اختيار الوقت');
        return;
      }

      controller.timeOfDay = selectedTime;

      if (controller.settingsServices.sharedPref!.getBool('enable') == true) {
        await NotifyHelper().scheduleAzkar(timeOfDay: selectedTime);
        defaultToast(
          text:
              'تم ضبط إشعار ذكر الصباح على ${selectedTime.format(context)}',
        );
      } else {
        defaultToast(text: 'الإشعارات غير مفعلة حاليًا');
      }
    } catch (e) {
      defaultToast(text: 'حدث خطأ أثناء تحديد الوقت');
    }
  }
}
