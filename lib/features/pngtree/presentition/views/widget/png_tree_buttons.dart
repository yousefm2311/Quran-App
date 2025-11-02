import 'package:flutter/material.dart';
import 'package:quran_app_android/core/util/color.dart';
import 'package:quran_app_android/features/pngtree/presentition/view_model/pngTree_view_model.dart';

class PngTreeButtons extends StatelessWidget {
  const PngTreeButtons({super.key, required this.controller});

  final PngTreeViewModel controller;

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.end,
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        GestureDetector(
          onTap: controller.clearConter,
          child: Container(
            width: 55,
            height: 55,
            decoration: const BoxDecoration(
              shape: BoxShape.circle,
              color: Colors.white,
            ),
            child: const Icon(Icons.restart_alt_rounded, size: 30),
          ),
        ),
        const SizedBox(width: 20),
        GestureDetector(
          onTap: controller.increaseCounter,
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 180),
            width: 110,
            height: 110,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              gradient: RadialGradient(
                colors: [
                  AppColors.kPrimaryColor.withOpacity(0.9),
                  AppColors.kPrimaryColor.withOpacity(0.6),
                ],
              ),
              boxShadow: [
                BoxShadow(
                  color: AppColors.kPrimaryColor.withOpacity(0.3),
                  blurRadius: 18,
                  spreadRadius: 4,
                ),
              ],
            ),
            child: const Icon(Icons.fingerprint, color: Colors.white, size: 45),
          ),
        ),
        const SizedBox(width: 20),
        GestureDetector(
          onTap: controller.decreaseCounter,
          child: Container(
            width: 55,
            height: 55,
            decoration: const BoxDecoration(
              shape: BoxShape.circle,
              color: Colors.white,
            ),
            child: const Icon(Icons.remove_circle_outline, size: 30),
          ),
        ),
      ],
    );
  }
}
