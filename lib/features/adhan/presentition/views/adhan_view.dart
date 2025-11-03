import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:quran_app_android/core/util/color.dart';
import 'package:quran_app_android/core/util/widgets/custom_appBar.dart';
import 'package:quran_app_android/core/util/widgets/custom_back_button.dart';
import 'package:quran_app_android/core/util/widgets/my_text.dart';
import 'package:quran_app_android/features/adhan/presentition/view_model/adhan_view_model.dart';
import 'package:quran_app_android/features/adhan/presentition/views/widget/adhan_body_view.dart';
class AdhanView extends GetWidget<AdhanViewModel> {
  const AdhanView({super.key});


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(
        loading: const CustomBackButton(),
        title: MyText(
          text: 'مواقيت الصلاة ',
          textStyle: Theme.of(context).textTheme.bodyMedium!.copyWith(
                fontFamily: 'Rubik',
                color: Colors.black,
                fontWeight: FontWeight.bold,
                fontSize: 20,
              ),
        ),
        centerTitle: true,
      ),
      body: GetBuilder<AdhanViewModel>(
        builder: (controller) {
          if (controller.prayerTimes == null) {}
          return controller.prayerTimes != null
              ? const AdhanBodyView()
              : Center(
                  child: MaterialButton(
                  color: AppColors.kPrimaryColor,
                  onPressed: () {
                    controller.adhan();
                  },
                  child: Text(
                    'Get Data',
                    style: Theme.of(context)
                        .textTheme
                        .bodyMedium!
                        .copyWith(fontSize: 20, color: Colors.white),
                  ),
                ));
        },
      ),
    );
  }
}




// import 'package:flutter/material.dart';
// import 'package:get/get.dart';
// import 'package:quran_app_android/core/util/color.dart';
// import 'package:quran_app_android/core/util/widgets/custom_appBar.dart';
// import 'package:quran_app_android/core/util/widgets/custom_back_button.dart';
// import 'package:quran_app_android/core/util/widgets/my_text.dart';
// import 'package:quran_app_android/features/adhan/presentition/view_model/adhan_view_model.dart';
// import 'package:quran_app_android/features/adhan/presentition/views/widget/adhan_body_view.dart';

// class AdhanView extends GetWidget<AdhanViewModel> {
//   const AdhanView({super.key});

//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: CustomAppBar(
//         loading: const CustomBackButton(),
//         title: MyText(
//           text: 'مواقيت الصلاة',
//           textStyle: Theme.of(context).textTheme.bodyMedium!.copyWith(
//             fontFamily: 'Rubik',
//             color: Colors.black,
//             fontWeight: FontWeight.bold,
//             fontSize: 20,
//           ),
//         ),
//         centerTitle: true,
//       ),
//       body: GetBuilder<AdhanViewModel>(
//         builder: (controller) {
//           if (controller.isLoading.value) {
//             return const Center(child: CircularProgressIndicator());
//           }

//           if (controller.prayerTimes != null) {
//             return const AdhanBodyView();
//           }

//           return Center(
//             child: Column(
//               mainAxisAlignment: MainAxisAlignment.center,
//               children: [
//                 const Icon(Icons.location_off, color: Colors.grey, size: 50),
//                 const SizedBox(height: 16),
//                 const Text(
//                   "لم يتم تحديد موقعك بعد",
//                   style: TextStyle(fontSize: 18, color: Colors.black87),
//                 ),
//                 const SizedBox(height: 16),
//                 ElevatedButton(
//                   style: ElevatedButton.styleFrom(
//                     backgroundColor: AppColors.kPrimaryColor,
//                     padding: const EdgeInsets.symmetric(
//                       horizontal: 24,
//                       vertical: 12,
//                     ),
//                     shape: RoundedRectangleBorder(
//                       borderRadius: BorderRadius.circular(12),
//                     ),
//                   ),
//                   onPressed: controller.initializeAdhan,
//                   child: const Text(
//                     'تحديث الموقع',
//                     style: TextStyle(color: Colors.white, fontSize: 18),
//                   ),
//                 ),
//               ],
//             ),
//           );
//         },
//       ),
//     );
//   }
// }
