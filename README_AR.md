<div align="center">
  <h1>📖 تطبيق القرآن الكريم (Quran App)</h1>
  <p><strong>تطبيق إسلامي شامل للقرآن، التفسير، الأذكار، مواقيت الصلاة، واتجاه القبلة</strong></p>
  
  <p>
    <a href="https://flutter.dev"><img src="https://img.shields.io/badge/Flutter-02569B?style=for-the-badge&logo=flutter&logoColor=white" alt="Flutter" /></a>
    <a href="https://dart.dev"><img src="https://img.shields.io/badge/Dart-0175C2?style=for-the-badge&logo=dart&logoColor=white" alt="Dart" /></a>
    <a href="#"><img src="https://img.shields.io/badge/GetX-State_Management-FF6F00?style=for-the-badge" alt="GetX" /></a>
  </p>

  <p>
    <a href="README.md">🇺🇸 View in English</a>
  </p>
</div>

---

## 📖 نظرة عامة على المشروع

**تطبيق القرآن الكريم (Quran App)** هو تطبيق إسلامي متكامل مبني بإطار عمل Flutter، مصمم ليقدم تجربة مستخدم مريحة وعصرية. يهدف التطبيق إلى جمع كل ما يحتاجه المسلم في حياته اليومية في مكان واحد، بدءاً من قراءة القرآن وتفسيره، مروراً بالأذكار والأحاديث، وصولاً إلى مواقيت الصلاة الدقيقة وتحديد اتجاه القبلة.

يعتمد المشروع على بنية برمجية نظيفة (Feature-first Architecture) ويوظف تقنيات حديثة لإدارة الحالة عبر **GetX** لضمان أداء سلس وسريع.

---

## ✨ المميزات الأساسية

### 📖 القرآن الكريم والتفسير
- **قراءة مريحة:** عرض المصحف بخطوط واضحة مع دعم كامل للوضع الليلي (Dark Mode).
- **التفسير الميسر:** استعراض التفسير لكل آية لسهولة التدبر والفهم.
- **حفظ تقدم القراءة:** علامات مرجعية ذكية لتذكر آخر صفحة تم قراءتها.

### 🤲 الأذكار والأدعية
- **مكتبة أذكار شاملة:** أذكار الصباح، المساء، النوم، وغيرها.
- **عداد للأذكار:** مدمج مع نظام تتبع لتسهيل قراءة الأذكار ذات التكرار.
- **إشعارات وتنبيهات:** تذكير يومي بقراءة الأذكار.

### 📿 السبحة الإلكترونية
- **تسبيح ذكي:** عداد إلكتروني مع تفاعل لمسي (Haptic Feedback) واهتزاز عند الوصول لأرقام محددة (مثل 33).
- **متابعة الإنجاز:** حساب دورات التسبيح الكاملة.

### 🕋 مواقيت الصلاة واتجاه القبلة
- **حساب دقيق:** تحديد أوقات الصلاة بناءً على الموقع الجغرافي الفعلي للمستخدم.
- **بوصلة القبلة:** بوصلة دقيقة مدمجة لتحديد اتجاه الكعبة المشرفة بسلاسة.
- **منبه الأذان:** إشعارات وتنبيهات صوتية عند دخول وقت الصلاة.

### 📚 ميزات إضافية
- **الأحاديث النبوية:** مكتبة مرتبة لأهم الأحاديث الشريفة.
- **أسماء الله الحسنى:** استعراض 99 اسماً من أسماء الله الحسنى.
- **دعم الوضعين:** واجهة تتأقلم مع الوضع الفاتح والداكن (Light/Dark Themes).

---

## ⚡ التقنيات المستخدمة (Tech Stack)

- **إدارة الحالة والتوجيه (State & Route Management):** `GetX`
- **التخزين المحلي (Local Storage):** `shared_preferences` لحفظ الإعدادات والمحفوظات.
- **البيانات المحلية (Local JSON):** الاعتماد على ملفات JSON مدمجة لتوفير القرآن، التفسير، والأذكار ليعمل التطبيق (Offline).
- **تحديد الموقع والقبلة:** `geolocator`, `flutter_compass`, و `location`.
- **مواقيت الصلاة:** مكتبات `adhan` و `adhan_dart`.
- **الإشعارات والتنبيهات:** `flutter_local_notifications`, `flutter_native_timezone_latest`, `timezone`.
- **الواجهة الرسومية:** استخدام `staggered_grid_view_flutter`, `lottie`, `flutter_custom_clippers`, `smooth_page_indicator`.

---

## 🏗️ هيكل المشروع (Architecture)

تم بناء المشروع باستخدام هيكلية تقسيم حسب الميزات (Feature-Based Architecture)، مما يجعله قابلاً للتطوير والصيانة بسهولة:

```text
lib/
├── core/                     # الأدوات والمكونات المشتركة
│   ├── middleware/           # دوال التحقق والتوجيه (مثل الـ Onboarding)
│   ├── service/              # الخدمات الأساسية (الإعدادات، الثيمات، الإشعارات)
│   └── util/                 # الثوابت والتوجيهات (Routes & Bindings)
├── features/                 # الميزات الأساسية للتطبيق
│   ├── adhan/                # مواقيت الصلاة والأذان
│   ├── azkar/                # الأذكار اليومية
│   ├── hadith/               # الأحاديث النبوية
│   ├── home/                 # الشاشة الرئيسية والواجهة
│   ├── nameOfAllah/          # أسماء الله الحسنى
│   ├── notifications/        # إشعارات وتنبيهات الصلاة
│   ├── onboarding/           # شاشات الترحيب للمستخدم الجديد
│   ├── pngtree/              # السبحة الإلكترونية (Tasbeeh)
│   ├── qiblah/               # بوصلة تحديد اتجاه القبلة
│   ├── quran/                # عرض وقراءة القرآن الكريم
│   ├── settings/             # إعدادات التطبيق وتخصيص الواجهة
│   └── tafsser/              # التفسير الميسر للآيات
└── main.dart                 # نقطة البداية للمشروع
```

---

## 🚀 دليل التشغيل (Getting Started)

### المتطلبات الأساسية
- تثبيت [Flutter SDK](https://flutter.dev/docs/get-started/install).
- محرر أكواد مثل Android Studio أو VS Code.

### خطوات التشغيل

1. **استنساخ المشروع (Clone):**
   ```bash
   git clone https://github.com/yousefm2311/Quran_App.git
   cd Quran_App
   ```

2. **تحميل المكتبات (Get Packages):**
   ```bash
   flutter pub get
   ```

3. **تشغيل التطبيق (Run):**
   ```bash
   flutter run
   ```

---

## 📱 لقطات من التطبيق (Screenshots)

*(تم إرفاق بعض واجهات التطبيق أدناه لبيان شكل التصميم)*

![Quran App Preview](https://github.com/user-attachments/assets/6267444b-a13c-4a31-a7fd-6ed99743fccc)
![Home and Features](https://github.com/user-attachments/assets/f799112a-a9ed-4bcd-b12a-c155f552a70d)
![Azkar Section](https://github.com/user-attachments/assets/a1f5f88c-d744-4f9d-8ed7-555a15b0a24e)
![Qibla and Prayers](https://github.com/user-attachments/assets/ad8a3d76-93f2-4f82-bd1e-3751e9829633)

---

## 🛡️ المطور (Author)

تم التطوير بكل ❤️ بواسطة **Yousef Mohamed**
- **GitHub:** [yousefm2311](https://github.com/yousefm2311)
