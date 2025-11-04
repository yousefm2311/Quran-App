// // package com.example.quran_app_android.azkar

// // import android.animation.ObjectAnimator
// // import android.animation.PropertyValuesHolder
// // import android.animation.ValueAnimator
// // import android.app.Service
// // import android.content.Intent
// // import android.graphics.PixelFormat
// // import android.media.MediaPlayer
// // import android.os.Build
// // import android.os.Handler
// // import android.os.IBinder
// // import android.view.Gravity
// // import android.view.LayoutInflater
// // import android.view.MotionEvent
// // import android.view.View
// // import android.view.WindowManager
// // import android.view.animation.BounceInterpolator
// // import android.widget.FrameLayout
// // import android.widget.TextView
// // import com.example.quran_app_android.R
// // import kotlin.math.abs

// // class AzkarBubbleService : Service() {

// //     private lateinit var windowManager: WindowManager
// //     private var bubbleView: View? = null
// //     private var player: MediaPlayer? = null
// //     private val handler = Handler()

// //     override fun onBind(intent: Intent?): IBinder? = null

// //     override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
// //         val zikrText = intent?.getStringExtra("zikr_text") ?: "فذكّر ✨"
// //         showBubble(zikrText)
// //         return START_NOT_STICKY
// //     }

// //     private fun showBubble(zikrText: String) {
// //         windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
// //         val inflater = LayoutInflater.from(this)
// //         bubbleView = inflater.inflate(R.layout.azkar_bubble_layout, null)

// //         val params = WindowManager.LayoutParams(
// //             WindowManager.LayoutParams.WRAP_CONTENT,
// //             WindowManager.LayoutParams.WRAP_CONTENT,
// //             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
// //                 WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
// //             else
// //                 WindowManager.LayoutParams.TYPE_PHONE,
// //             WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
// //             PixelFormat.TRANSLUCENT
// //         )

// //         params.gravity = Gravity.TOP or Gravity.END
// //         params.x = -60  // جزء منها داخل الشاشة
// //         params.y = 250

// //         val container = bubbleView!!.findViewById<FrameLayout>(R.id.azkarContainer)
// //         val textView = bubbleView!!.findViewById<TextView>(R.id.azkarText)
// //         textView.text = zikrText

// //         // 🎵 تشغيل صوت الذكر
// //         player = MediaPlayer.create(this, R.raw.fazakkir)
// //         player?.start()

// //         // ✅ أول ما يوصل الإشعار: يعرض الذكر في كونتينر 5 ثواني
// //         showZikr(textView)

// //         // ✅ الضغط على البابلز يعرض الذكر تاني
// //         container.setOnClickListener {
// //             if (textView.visibility == View.GONE) {
// //                 showZikr(textView)
// //             } else {
// //                 hideZikr(textView)
// //             }
// //         }

// //         // ✅ حركة لطيفة + Bounce + Drift
// //         pulseAnimation()
// //         driftAnimation()
// //         entryAnimation()

// //         // ✅ سحب وتحريك + Snap ناعم
// //         enableDrag(container, params)

// //         windowManager.addView(bubbleView, params)
// //     }

// //     private fun showZikr(textView: TextView) {
// //         textView.visibility = View.VISIBLE
// //         textView.alpha = 0f
// //         textView.animate().alpha(1f).setDuration(400).start()

// //         handler.postDelayed({
// //             hideZikr(textView)
// //         }, 5000)
// //     }

// //     private fun hideZikr(textView: TextView) {
// //         textView.animate().alpha(0f).setDuration(400).withEndAction {
// //             textView.visibility = View.GONE
// //         }.start()
// //     }

// //     private fun pulseAnimation() {
// //         val pulse = ObjectAnimator.ofPropertyValuesHolder(
// //             bubbleView,
// //             PropertyValuesHolder.ofFloat(View.SCALE_X, 0.85f, 1.1f, 1.0f),
// //             PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.85f, 1.1f, 1.0f)
// //         )
// //         pulse.duration = 800
// //         pulse.start()
// //     }

// //     private fun driftAnimation() {
// //         val drift = ValueAnimator.ofFloat(0f, 10f, 0f, -8f, 0f)
// //         drift.duration = 6000
// //         drift.repeatCount = ValueAnimator.INFINITE
// //         drift.addUpdateListener {
// //             bubbleView?.translationX = it.animatedValue as Float
// //         }
// //         drift.start()
// //     }

// //     private fun entryAnimation() {
// //         bubbleView?.translationX = 200f
// //         bubbleView?.alpha = 0f
// //         val entryAnim = ObjectAnimator.ofPropertyValuesHolder(
// //             bubbleView,
// //             PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 200f, 0f),
// //             PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
// //         )
// //         entryAnim.duration = 900
// //         entryAnim.interpolator = BounceInterpolator()
// //         entryAnim.start()
// //     }

// //     private fun enableDrag(container: View, params: WindowManager.LayoutParams) {
// //         bubbleView!!.setOnTouchListener(object : View.OnTouchListener {
// //             var initialX = 0
// //             var initialY = 0
// //             var touchX = 0f
// //             var touchY = 0f
// //             var moved = false

// //             override fun onTouch(v: View?, event: MotionEvent): Boolean {
// //                 when (event.action) {
// //                     MotionEvent.ACTION_DOWN -> {
// //                         initialX = params.x
// //                         initialY = params.y
// //                         touchX = event.rawX
// //                         touchY = event.rawY
// //                         moved = false
// //                         return true
// //                     }

// //                     MotionEvent.ACTION_MOVE -> {
// //                         val deltaX = (event.rawX - touchX).toInt()
// //                         val deltaY = (event.rawY - touchY).toInt()
// //                         if (abs(deltaX) > 5 || abs(deltaY) > 5) {
// //                             moved = true
// //                             params.x = initialX - deltaX
// //                             params.y = initialY + deltaY
// //                             windowManager.updateViewLayout(bubbleView, params)
// //                         }
// //                         return true
// //                     }

// //                     MotionEvent.ACTION_UP -> {
// //                         if (!moved) {
// //                             container.performClick()
// //                         } else {
// //                             val screenWidth = resources.displayMetrics.widthPixels
// //                             val targetX = if (params.x > screenWidth / 2) screenWidth - bubbleView!!.width else 0
// //                             val snapAnim = ObjectAnimator.ofInt(params.x, targetX)
// //                             snapAnim.duration = 400
// //                             snapAnim.interpolator = BounceInterpolator()
// //                             snapAnim.addUpdateListener {
// //                                 params.x = it.animatedValue as Int
// //                                 windowManager.updateViewLayout(bubbleView, params)
// //                             }
// //                             snapAnim.start()
// //                         }
// //                         return true
// //                     }
// //                 }
// //                 return false
// //             }
// //         })
// //     }

// //     override fun onDestroy() {
// //         super.onDestroy()
// //         bubbleView?.let { windowManager.removeView(it) }
// //         player?.release()
// //     }
// // }
// package com.example.quran_app_android.azkar

// import android.animation.ObjectAnimator
// import android.animation.PropertyValuesHolder
// import android.app.Service
// import android.content.Intent
// import android.graphics.PixelFormat
// import android.media.MediaPlayer
// import android.os.Build
// import android.os.Handler
// import android.os.IBinder
// import android.view.*
// import android.view.animation.AccelerateDecelerateInterpolator
// import android.view.animation.BounceInterpolator
// import android.widget.FrameLayout
// import android.widget.LinearLayout
// import android.widget.TextView
// import com.example.quran_app_android.R
// import kotlin.math.abs

// class AzkarBubbleService : Service() {

//     private lateinit var windowManager: WindowManager
//     private var bubbleView: View? = null
//     private var overlayView: View? = null
//     private var player: MediaPlayer? = null
//     private val handler = Handler()

//     override fun onBind(intent: Intent?): IBinder? = null

//     override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//         val zikrText = intent?.getStringExtra("zikr_text") ?: "فذكّر ✨"
//         showBubble(zikrText)
//         return START_NOT_STICKY
//     }

//     private fun showBubble(zikrText: String) {
//         windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//         val inflater = LayoutInflater.from(this)
//         bubbleView = inflater.inflate(R.layout.azkar_bubble_layout, null)

//         val params = WindowManager.LayoutParams(
//             WindowManager.LayoutParams.WRAP_CONTENT,
//             WindowManager.LayoutParams.WRAP_CONTENT,
//             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                 WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//             else
//                 WindowManager.LayoutParams.TYPE_PHONE,
//             WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
//             PixelFormat.TRANSLUCENT
//         )

//         params.gravity = Gravity.CENTER_VERTICAL or Gravity.END
//         params.x = 0
//         params.y = 100

//         val container = bubbleView!!.findViewById<LinearLayout>(R.id.azkarContainer)
//         val bubbleCircle = bubbleView!!.findViewById<LinearLayout>(R.id.bubbleCircle)
//         val textView = bubbleView!!.findViewById<TextView>(R.id.azkarText)
//         textView.text = zikrText

//         player = MediaPlayer.create(this, R.raw.fazakkir)
//         player?.start()

//         // أول مرة يظهر الذكر جنب البابل ويختفي بعد 5 ثواني
//         showZikr(textView, autoHide = true)

//         // الضغط على البابل يعيد إظهار الذكر
//         bubbleCircle.setOnClickListener {
//             if (textView.visibility == View.GONE) {
//                 showZikr(textView, autoHide = false)
//                 addOutsideTouchOverlay(textView)
//             }
//         }

//         enableDrag(container, params)
//         entryAnimation()
//         windowManager.addView(bubbleView, params)
//     }

//     private fun showZikr(textView: TextView, autoHide: Boolean) {
//         textView.visibility = View.VISIBLE
//         textView.alpha = 0f
//         textView.translationX = 150f

//         val anim = ObjectAnimator.ofPropertyValuesHolder(
//             textView,
//             PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
//             PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 150f, 0f)
//         )
//         anim.duration = 500
//         anim.interpolator = AccelerateDecelerateInterpolator()
//         anim.start()

//         if (autoHide) {
//             handler.postDelayed({
//                 hideZikr(textView)
//             }, 5000)
//         }
//     }

//     private fun hideZikr(textView: TextView) {
//         val anim = ObjectAnimator.ofPropertyValuesHolder(
//             textView,
//             PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f),
//             PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, 150f)
//         )
//         anim.duration = 400
//         anim.interpolator = AccelerateDecelerateInterpolator()
//         anim.start()

//         handler.postDelayed({
//             textView.visibility = View.GONE
//         }, 400)
//         removeOutsideTouchOverlay()
//     }

//     private fun addOutsideTouchOverlay(textView: TextView) {
//         if (overlayView != null) return

//         overlayView = View(this).apply {
//             setBackgroundColor(0x00000000)
//             setOnTouchListener { _, _ ->
//                 hideZikr(textView)
//                 true
//             }
//         }

//         val overlayParams = WindowManager.LayoutParams(
//             WindowManager.LayoutParams.MATCH_PARENT,
//             WindowManager.LayoutParams.MATCH_PARENT,
//             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                 WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//             else
//                 WindowManager.LayoutParams.TYPE_PHONE,
//             WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
//             PixelFormat.TRANSLUCENT
//         )

//         windowManager.addView(overlayView, overlayParams)
//     }

//     private fun removeOutsideTouchOverlay() {
//         overlayView?.let {
//             windowManager.removeView(it)
//             overlayView = null
//         }
//     }

//     private fun enableDrag(container: View, params: WindowManager.LayoutParams) {
//         bubbleView!!.setOnTouchListener(object : View.OnTouchListener {
//             var initialX = 0
//             var initialY = 0
//             var touchX = 0f
//             var touchY = 0f
//             var moved = false

//             override fun onTouch(v: View?, event: MotionEvent): Boolean {
//                 when (event.action) {
//                     MotionEvent.ACTION_DOWN -> {
//                         initialX = params.x
//                         initialY = params.y
//                         touchX = event.rawX
//                         touchY = event.rawY
//                         moved = false
//                         return true
//                     }
//                     MotionEvent.ACTION_MOVE -> {
//                         val deltaX = (event.rawX - touchX).toInt()
//                         val deltaY = (event.rawY - touchY).toInt()
//                         if (abs(deltaX) > 5 || abs(deltaY) > 5) {
//                             moved = true
//                             params.x = initialX - deltaX
//                             params.y = initialY + deltaY
//                             windowManager.updateViewLayout(bubbleView, params)
//                         }
//                         return true
//                     }
//                     MotionEvent.ACTION_UP -> {
//                         if (!moved) container.performClick()
//                         return true
//                     }
//                 }
//                 return false
//             }
//         })
//     }

//     private fun entryAnimation() {
//         bubbleView?.translationY = 200f
//         bubbleView?.alpha = 0f
//         val entry = ObjectAnimator.ofPropertyValuesHolder(
//             bubbleView,
//             PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 200f, 0f),
//             PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
//         )
//         entry.duration = 800
//         entry.interpolator = BounceInterpolator()
//         entry.start()
//     }

//     override fun onDestroy() {
//         super.onDestroy()
//         bubbleView?.let { windowManager.removeView(it) }
//         removeOutsideTouchOverlay()
//         player?.release()
//     }
// }
// package com.example.quran_app_android.azkar

// import android.animation.ObjectAnimator
// import android.animation.PropertyValuesHolder
// import android.animation.ValueAnimator
// import android.app.Service
// import android.content.Intent
// import android.graphics.PixelFormat
// import android.media.MediaPlayer
// import android.os.*
// import android.view.*
// import android.view.animation.AccelerateDecelerateInterpolator
// import android.view.animation.BounceInterpolator
// import android.widget.FrameLayout
// import android.widget.ImageView
// import android.widget.TextView
// import com.example.quran_app_android.R
// import kotlin.math.abs

// class AzkarBubbleService : Service() {

//     private lateinit var windowManager: WindowManager
//     private var bubbleView: View? = null
//     private var closeView: View? = null
//     private var player: MediaPlayer? = null
//     private val handler = Handler(Looper.getMainLooper())
//     private var isDragging = false
//     private var screenWidth = 0
//     private var screenHeight = 0

//     override fun onBind(intent: Intent?): IBinder? = null

//     override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//         val zikrText = intent?.getStringExtra("zikr_text") ?: "فذكّر ✨"
//         showBubble(zikrText)
//         return START_NOT_STICKY
//     }

//     private fun showBubble(zikrText: String) {
//         windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//         val inflater = LayoutInflater.from(this)
//         bubbleView = inflater.inflate(R.layout.azkar_bubble_layout, null)

//         val displayMetrics = resources.displayMetrics
//         screenWidth = displayMetrics.widthPixels
//         screenHeight = displayMetrics.heightPixels

//         val params = WindowManager.LayoutParams(
//             WindowManager.LayoutParams.WRAP_CONTENT,
//             WindowManager.LayoutParams.WRAP_CONTENT,
//             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                 WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//             else
//                 WindowManager.LayoutParams.TYPE_PHONE,
//             WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//             PixelFormat.TRANSLUCENT
//         )

//         // ✅ تبدأ على يمين الشاشة فعليًا
//         params.gravity = Gravity.TOP or Gravity.START
//         params.x = screenWidth - 120   // قريب من الحافة اليمين
//         params.y = screenHeight / 3

//         val bubbleIcon = bubbleView!!.findViewById<ImageView>(R.id.bubbleIcon)
//         val textContainer = bubbleView!!.findViewById<FrameLayout>(R.id.zikrContainer)
//         val textView = bubbleView!!.findViewById<TextView>(R.id.azkarText)
//         textView.text = zikrText
//         textContainer.visibility = View.GONE

//         player = MediaPlayer.create(this, R.raw.fazakkir)
//         player?.start()

//         // ✅ عند استقبال الإشعار يظهر الذكر 5 ثواني تلقائي
//         showZikr(textContainer)

//         // ✅ الضغط على البابلز يفتح/يقفل الكونتينر
//         bubbleIcon.setOnClickListener {
//             if (!isDragging) {
//                 if (textContainer.visibility == View.GONE) showZikr(textContainer)
//                 else hideZikr(textContainer)
//             }
//         }

//         enableDrag(bubbleIcon, params)
//         entryAnimation()
//         windowManager.addView(bubbleView, params)
//     }

//     private fun showZikr(container: FrameLayout) {
//         container.visibility = View.VISIBLE
//         container.alpha = 0f
//         ObjectAnimator.ofPropertyValuesHolder(
//             container,
//             PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
//             PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 100f, 0f)
//         ).apply {
//             duration = 400
//             interpolator = AccelerateDecelerateInterpolator()
//             start()
//         }

//         handler.postDelayed({
//             hideZikr(container)
//         }, 5000)
//     }

//     private fun hideZikr(container: FrameLayout) {
//         ObjectAnimator.ofPropertyValuesHolder(
//             container,
//             PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f),
//             PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, 100f)
//         ).apply {
//             duration = 400
//             interpolator = AccelerateDecelerateInterpolator()
//             start()
//         }

//         handler.postDelayed({
//             container.visibility = View.GONE
//         }, 400)
//     }

//     private fun enableDrag(view: View, params: WindowManager.LayoutParams) {
//         view.setOnTouchListener(object : View.OnTouchListener {
//             var initialX = 0
//             var initialY = 0
//             var touchX = 0f
//             var touchY = 0f
//             var moved = false

//             override fun onTouch(v: View?, event: MotionEvent): Boolean {
//                 when (event.action) {
//                     MotionEvent.ACTION_DOWN -> {
//                         initialX = params.x
//                         initialY = params.y
//                         touchX = event.rawX
//                         touchY = event.rawY
//                         moved = false
//                         return true
//                     }

//                     MotionEvent.ACTION_MOVE -> {
//                         val dx = (event.rawX - touchX).toInt()
//                         val dy = (event.rawY - touchY).toInt()
//                         if (abs(dx) > 10 || abs(dy) > 10) {
//                             moved = true
//                             isDragging = true
//                             showCloseButton()
//                             params.x = initialX + dx
//                             params.y = initialY + dy
//                             windowManager.updateViewLayout(bubbleView, params)
//                             moveCloseButton(event.rawX, event.rawY)
//                         }
//                         return true
//                     }

//                     MotionEvent.ACTION_UP -> {
//                         hideCloseButton()

//                         // ✅ لو مفيش سحب تقريبًا، نفّذ الـ click
//                         if (!moved) {
//                             isDragging = false
//                             v?.performClick()
//                             return true
//                         }

//                         if (isOverCloseButton(event.rawX, event.rawY)) {
//                             bubbleView?.let { windowManager.removeView(it) }
//                             closeView?.let { windowManager.removeView(it) }
//                             stopSelf()
//                             return true
//                         }
//                         snapToEdge(params)
//                         isDragging = false
//                         return true
//                     }
//                 }
//                 return false
//             }
//         })
//     }

//     private fun snapToEdge(params: WindowManager.LayoutParams) {
//         val middle = screenWidth / 2
//         val targetX = if (params.x >= middle) screenWidth - 100 else 0
//         val anim = ValueAnimator.ofInt(params.x, targetX)
//         anim.duration = 400
//         anim.addUpdateListener {
//             params.x = it.animatedValue as Int
//             windowManager.updateViewLayout(bubbleView, params)
//         }
//         anim.start()
//     }

//     private fun showCloseButton() {
//         if (closeView != null) return
//         closeView = ImageView(this).apply {
//             setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
//             alpha = 0.8f
//         }

//         val params = WindowManager.LayoutParams(
//             120, 120,
//             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                 WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//             else
//                 WindowManager.LayoutParams.TYPE_PHONE,
//             WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//             PixelFormat.TRANSLUCENT
//         )

//         params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
//         params.y = 100
//         windowManager.addView(closeView, params)
//     }

//     private fun moveCloseButton(x: Float, y: Float) {
//         closeView?.alpha = if (isOverCloseButton(x, y)) 1f else 0.7f
//     }

//     private fun hideCloseButton() {
//         closeView?.let {
//             windowManager.removeView(it)
//             closeView = null
//         }
//     }

//     private fun isOverCloseButton(x: Float, y: Float): Boolean {
//         closeView?.let {
//             val loc = IntArray(2)
//             it.getLocationOnScreen(loc)
//             val left = loc[0].toFloat()
//             val top = loc[1].toFloat()
//             val right = left + it.width
//             val bottom = top + it.height
//             return x in left..right && y in top..bottom
//         }
//         return false
//     }

//     private fun entryAnimation() {
//         bubbleView?.translationY = 200f
//         bubbleView?.alpha = 0f
//         val entry = ObjectAnimator.ofPropertyValuesHolder(
//             bubbleView,
//             PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 200f, 0f),
//             PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
//         )
//         entry.duration = 600
//         entry.interpolator = BounceInterpolator()
//         entry.start()
//     }

//     override fun onDestroy() {
//         super.onDestroy()
//         bubbleView?.let { windowManager.removeView(it) }
//         closeView?.let { windowManager.removeView(it) }
//         player?.release()
//     }
// }




package com.example.quran_app_android.azkar

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.*
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.quran_app_android.R
import kotlin.math.abs

class AzkarBubbleService : Service() {

    private lateinit var windowManager: WindowManager
    private var bubbleView: View? = null
    private var closeView: View? = null
    private var player: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isDragging = false
    private var screenWidth = 0
    private var screenHeight = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // ✅ أول حاجة: إنشاء القناة وتشغيل الخدمة كمقدمة (Foreground)
        createNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this, "azkar_bubble_channel")
            .setContentTitle("📿 الأذكار تعمل في الخلفية")
            .setContentText("جارٍ عرض الأذكار المنبثقة...")
            .setSmallIcon(R.drawable.ic_mosque)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        startForeground(2001, notification)

        // ✅ بعدين نكمل تشغيل البابل عادي
        val zikrText = intent?.getStringExtra("zikr_text") ?: "فذكّر ✨"
        showBubble(zikrText)
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "azkar_bubble_channel",
                "Azkar Floating Bubble",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "قناة إشعارات لخدمة الأذكار العائمة"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun showBubble(zikrText: String) {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = LayoutInflater.from(this)
        bubbleView = inflater.inflate(R.layout.azkar_bubble_layout, null)

        val displayMetrics = resources.displayMetrics
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = screenWidth - 120
        params.y = screenHeight / 3

        val bubbleIcon = bubbleView!!.findViewById<ImageView>(R.id.bubbleIcon)
        val textContainer = bubbleView!!.findViewById<FrameLayout>(R.id.zikrContainer)
        val textView = bubbleView!!.findViewById<TextView>(R.id.azkarText)
        textView.text = zikrText
        textContainer.visibility = View.GONE

        player = MediaPlayer.create(this, R.raw.fazakkir)
        player?.start()

        showZikr(textContainer)

        bubbleIcon.setOnClickListener {
            if (!isDragging) {
                if (textContainer.visibility == View.GONE) showZikr(textContainer)
                else hideZikr(textContainer)
            }
        }

        enableDrag(bubbleIcon, params)
        entryAnimation()
        windowManager.addView(bubbleView, params)
    }

    private fun showZikr(container: FrameLayout) {
        container.visibility = View.VISIBLE
        container.alpha = 0f
        ObjectAnimator.ofPropertyValuesHolder(
            container,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 100f, 0f)
        ).apply {
            duration = 400
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        handler.postDelayed({
            hideZikr(container)
        }, 5000)
    }

    private fun hideZikr(container: FrameLayout) {
        ObjectAnimator.ofPropertyValuesHolder(
            container,
            PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f),
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, 100f)
        ).apply {
            duration = 400
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        handler.postDelayed({
            container.visibility = View.GONE
        }, 400)
    }

    private fun enableDrag(view: View, params: WindowManager.LayoutParams) {
        view.setOnTouchListener(object : View.OnTouchListener {
            var initialX = 0
            var initialY = 0
            var touchX = 0f
            var touchY = 0f
            var moved = false

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        touchX = event.rawX
                        touchY = event.rawY
                        moved = false
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - touchX).toInt()
                        val dy = (event.rawY - touchY).toInt()
                        if (abs(dx) > 10 || abs(dy) > 10) {
                            moved = true
                            isDragging = true
                            showCloseButton()
                            params.x = initialX + dx
                            params.y = initialY + dy
                            windowManager.updateViewLayout(bubbleView, params)
                            moveCloseButton(event.rawX, event.rawY)
                        }
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        hideCloseButton()

                        if (!moved) {
                            isDragging = false
                            v?.performClick()
                            return true
                        }

                        if (isOverCloseButton(event.rawX, event.rawY)) {
                            bubbleView?.let { windowManager.removeView(it) }
                            closeView?.let { windowManager.removeView(it) }
                            stopSelf()
                            return true
                        }
                        snapToEdge(params)
                        isDragging = false
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun snapToEdge(params: WindowManager.LayoutParams) {
        val middle = screenWidth / 2
        val targetX = if (params.x >= middle) screenWidth - 100 else 0
        val anim = ValueAnimator.ofInt(params.x, targetX)
        anim.duration = 400
        anim.addUpdateListener {
            params.x = it.animatedValue as Int
            windowManager.updateViewLayout(bubbleView, params)
        }
        anim.start()
    }

    private fun showCloseButton() {
        if (closeView != null) return
        closeView = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            alpha = 0.8f
        }

        val params = WindowManager.LayoutParams(
            120, 120,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.y = 100
        windowManager.addView(closeView, params)
    }

    private fun moveCloseButton(x: Float, y: Float) {
        closeView?.alpha = if (isOverCloseButton(x, y)) 1f else 0.7f
    }

    private fun hideCloseButton() {
        closeView?.let {
            windowManager.removeView(it)
            closeView = null
        }
    }

    private fun isOverCloseButton(x: Float, y: Float): Boolean {
        closeView?.let {
            val loc = IntArray(2)
            it.getLocationOnScreen(loc)
            val left = loc[0].toFloat()
            val top = loc[1].toFloat()
            val right = left + it.width
            val bottom = top + it.height
            return x in left..right && y in top..bottom
        }
        return false
    }

    private fun entryAnimation() {
        bubbleView?.translationY = 200f
        bubbleView?.alpha = 0f
        val entry = ObjectAnimator.ofPropertyValuesHolder(
            bubbleView,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 200f, 0f),
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        )
        entry.duration = 600
        entry.interpolator = BounceInterpolator()
        entry.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        bubbleView?.let { windowManager.removeView(it) }
        closeView?.let { windowManager.removeView(it) }
        player?.release()
    }
}
