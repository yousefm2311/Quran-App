// ignore_for_file: non_constant_identifier_names

import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:quran_app_android/core/service/database/local_storage_data.dart';
import 'package:quran_app_android/core/service/settings/SettingsServices.dart';
import 'package:quran_app_android/core/util/app_url.dart';
import 'package:quran_app_android/features/quran/data/models/details_model.dart';

class QuranScreenViewModel extends GetxController {
  QuranScreenViewModel();

  final SettingsServices sharedPref = Get.find<SettingsServices>();
  final LocalStorageData localStorageData = Get.find<LocalStorageData>();
  RxBool isLoading = false.obs;
  List<dynamic> items = [];
  List<AyahModel> ayah_Model = [];

  bool currentSave = false;
  int? currentIndex3Quran;
  int? currentIndex4Quran;

  double fontSize = 18;

  late PageController detailsPageController;
  bool _pageControllerInitialised = false;
  int currentDetailsPage = 0;

  List<List<VersesModel>> _cachedPages = const <List<VersesModel>>[];
  int? _cachedSurahIndex;
  double _cachedFontSize = -1;

  VersesModel? ayahModel;
  VersesModel? get ayahModell => ayahModel;

  @override
  void onInit() {
    super.onInit();
    final savedPage = sharedPref.sharedPref?.getInt('detailsPageIndex') ?? 0;
    _initPageController(savedPage);
    currentDetailsPage = savedPage;
    getCurrentMarkQuran();
    readJson();
  }

  @override
  void onClose() {
    if (_pageControllerInitialised) {
      detailsPageController.dispose();
    }
    super.onClose();
  }

  void _initPageController(int initialPage) {
    if (_pageControllerInitialised) {
      detailsPageController.dispose();
    }
    detailsPageController = PageController(initialPage: initialPage);
    _pageControllerInitialised = true;
  }

  void resetDetailsPaging({int initialPage = 0}) {
    currentDetailsPage = initialPage;
    sharedPref.sharedPref?.setInt('detailsPageIndex', initialPage);
    _initPageController(initialPage);
    _invalidatePagesCache();
    update();
  }

  Future<void> readJson() async {
    try {
      isLoading.value = true;
      final String response = await rootBundle.loadString(AppUrl.quranUrl);
      final List<dynamic> data = json.decode(response) as List<dynamic>;
      items = data;
      ayah_Model
        ..clear()
        ..addAll(
          items.map<AyahModel>(
            (item) => AyahModel.fromJson(item as Map<String, dynamic>),
          ),
        );
      _invalidatePagesCache();
      if (sharedPref.sharedPref?.getInt('indexQuran') != null) {
        currentIndex4Quran =
            sharedPref.sharedPref!.getInt('currentIndex4Quran');
      }
    } catch (e) {
      if (kDebugMode) {
        print(e.toString());
      }
    } finally {
      isLoading.value = false;
      update();
    }
  }

  void changeIndex3Quran(int index) {
    if (sharedPref.sharedPref != null) {
      sharedPref.sharedPref!.setInt("currentIndex3Quran", index);
      currentIndex3Quran = sharedPref.sharedPref!.getInt("currentIndex3Quran");
      update();
    }
  }

  void changeIndex4Quran(int index) {
    if (sharedPref.sharedPref != null) {
      sharedPref.sharedPref!.setInt("currentIndex4Quran", index);
      currentIndex4Quran = sharedPref.sharedPref!.getInt("currentIndex4Quran");
      update();
    }
  }

  void setDataQuran(VersesModel user) async {
    await localStorageData.setUser(user);
    update();
  }

  void addBookMarkQuran(int? id, String? text, String? translation) {
    if (sharedPref.sharedPref != null) {
      final VersesModel versesModel = VersesModel(
        id: id,
        text: text,
        translation: translation,
      );
      sharedPref.sharedPref!.setBool('bookQuran', true);
      sharedPref.sharedPref!.setInt('detailsPageIndex', currentDetailsPage);
      setDataQuran(versesModel);
      update();
    }
  }

  void getCurrentMarkQuran() async {
    await localStorageData.getUser
        .then((value) {
          ayahModel = value;
          update();
        })
        .catchError((error) {
          debugPrint(error.toString());
        });
  }

  void updateDetailsPage(int page) {
    currentDetailsPage = page;
    sharedPref.sharedPref?.setInt('detailsPageIndex', page);
    update();
  }

  List<List<VersesModel>> getPagedVersesFor(int surahIndex) {
    if (_cachedSurahIndex == surahIndex &&
        _cachedFontSize == fontSize &&
        _cachedPages.isNotEmpty) {
      return _cachedPages;
    }
    if (surahIndex < 0 ||
        surahIndex >= ayah_Model.length ||
        ayah_Model[surahIndex].verses.isEmpty) {
      _cachedSurahIndex = surahIndex;
      _cachedFontSize = fontSize;
      _cachedPages = const <List<VersesModel>>[];
      return _cachedPages;
    }
    final List<VersesModel> verses = ayah_Model[surahIndex].verses;
    final List<List<VersesModel>> pages = [];
    final int maxChars = _maxCharsPerPage();
    final int maxVersesPerPage = fontSize >= 26 ? 5 : 9;
    List<VersesModel> buffer = [];
    int charCounter = 0;
    for (final verse in verses) {
      final int verseLength = (verse.text?.length ?? 0) + 6;
      final bool exceedsChars =
          buffer.isNotEmpty && (charCounter + verseLength) > maxChars;
      final bool exceedsCount = buffer.length >= maxVersesPerPage;
      if (exceedsChars || exceedsCount) {
        pages.add(buffer);
        buffer = [];
        charCounter = 0;
      }
      buffer.add(verse);
      charCounter += verseLength;
    }
    if (buffer.isNotEmpty) {
      pages.add(buffer);
    }
    _cachedSurahIndex = surahIndex;
    _cachedFontSize = fontSize;
    _cachedPages = pages;
    return _cachedPages;
  }

  void ensurePageWithinBounds(int surahIndex) {
    final pages = getPagedVersesFor(surahIndex);
    if (pages.isEmpty) {
      return;
    }
    final int clamped =
        currentDetailsPage.clamp(0, pages.length - 1).toInt();
    if (currentDetailsPage != clamped) {
      currentDetailsPage = clamped;
      sharedPref.sharedPref?.setInt('detailsPageIndex', clamped);
    }
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (detailsPageController.hasClients &&
          detailsPageController.page?.round() != clamped) {
        detailsPageController.jumpToPage(clamped);
      }
    });
  }

  VersesModel resolveCurrentBookmarkVerse() {
    final int surahIndex = sharedPref.sharedPref?.getInt('indexQuran') ?? 0;
    if (surahIndex < 0 ||
        surahIndex >= ayah_Model.length ||
        ayah_Model[surahIndex].verses.isEmpty) {
      return VersesModel(
        id: ayah_Model.isNotEmpty ? ayah_Model.first.verses.first.id : 0,
        text: ayah_Model.isNotEmpty ? ayah_Model.first.verses.first.text : '',
        translation: ayah_Model.isNotEmpty
            ? ayah_Model.first.verses.first.translation
            : '',
      );
    }
    final pages = getPagedVersesFor(surahIndex);
    final int pageIndex =
        currentDetailsPage.clamp(0, pages.length - 1).toInt();
    final List<VersesModel> currentPage =
        pages.isNotEmpty ? pages[pageIndex] : ayah_Model[surahIndex].verses;
    return currentPage.first;
  }

  int? bookmarkPageIndexFor(int surahIndex) {
    final prefs = sharedPref.sharedPref;
    if (prefs == null ||
        surahIndex < 0 ||
        surahIndex >= ayah_Model.length) {
      return null;
    }
    final int? savedSurahId = prefs.getInt('currentIndex4Quran');
    final int? savedVerseId = prefs.getInt('currentIndex3Quran');
    final AyahModel ayah = ayah_Model[surahIndex];
    if (savedSurahId == null ||
        savedVerseId == null ||
        ayah.id == null ||
        ayah.id != savedSurahId) {
      return null;
    }
    final pages = getPagedVersesFor(surahIndex);
    for (var i = 0; i < pages.length; i++) {
      if (pages[i].any((verse) => verse.id == savedVerseId)) {
        return i;
      }
    }
    return null;
  }

  bool isBookmarkedPage(int surahIndex, int pageIndex) {
    final int? bookmarkedPage = bookmarkPageIndexFor(surahIndex);
    return bookmarkedPage != null && bookmarkedPage == pageIndex;
  }

  void jumpToSavedPageIfBookmarkMatches() {
    if (ayah_Model.isEmpty) return;
    final int currentSurahIndex =
        sharedPref.sharedPref?.getInt('indexQuran') ?? 0;
    if (currentSurahIndex < 0 || currentSurahIndex >= ayah_Model.length) {
      return;
    }
    final int? bookmarkedPage = bookmarkPageIndexFor(currentSurahIndex);
    if (bookmarkedPage == null) {
      return;
    }
    final pages = getPagedVersesFor(currentSurahIndex);
    if (pages.isEmpty) return;
    final int clampedPage = bookmarkedPage.clamp(0, pages.length - 1);
    currentDetailsPage = clampedPage;
    sharedPref.sharedPref?.setInt('detailsPageIndex', clampedPage);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (detailsPageController.hasClients) {
        detailsPageController.jumpToPage(currentDetailsPage);
      }
    });
  }

  void increaseFont() {
    if (fontSize < 30) {
      fontSize++;
      _invalidatePagesCache();
      update();
    }
  }

  void decreaseFont() {
    if (fontSize > 14) {
      fontSize--;
      _invalidatePagesCache();
      update();
    }
  }

  String replaceFarsiNumber(String input) {
    const english = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'];
    const arabic = ['۰', '۱', '۲', '۳', '٤', '۵', '٦', '۷', '۸', '۹'];
    for (int i = 0; i < english.length; i++) {
      input = input.replaceAll(english[i], arabic[i]);
    }
    return input;
  }

  bool shouldShowBasmalah(int surahIndex) {
    if (surahIndex < 0 || surahIndex >= ayah_Model.length) {
      return false;
    }
    final int? surahId = ayah_Model[surahIndex].id;
    // سورة التوبة (9) لا يتم عرض البسملة فيها.
    return surahId != 9;
  }

  int _maxCharsPerPage() {
    if (fontSize <= 18) {
      return 620;
    }
    final int reduction = ((fontSize - 18) * 28).round();
    final int result = 620 - reduction;
    return result.clamp(400, 620);
  }

  void _invalidatePagesCache() {
    _cachedPages = const <List<VersesModel>>[];
    _cachedSurahIndex = null;
    _cachedFontSize = -1;
  }
}
