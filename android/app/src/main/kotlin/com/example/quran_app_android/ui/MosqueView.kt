package com.example.quran_app_android.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class MosqueView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    // ألوان خفيفة ومتناسقة
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#41A05F") // أخضر أساسي
    }
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(1.5f)
        color = Color.parseColor("#6DD08A") // أخضر فاتح للتحديد
    }
    private val gold = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FFD55A") // ذهبي للهلال
    }
    private val cut = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK // لعمل الهلال بطرح دائرة صغيرة
    }

    // بنبني الأشكال مرة واحدة حسب المقاس — أداء أفضل
    private val pathMinaretLeft = Path()
    private val pathMinaretRight = Path()
    private val pathMainBuilding = Path()
    private val rectMainDome = RectF()
    private val rectLeftDome = RectF()
    private val rectRightDome = RectF()
    private val rectDoor = RectF()
    private val rectDoorArch = RectF()

    private var centerX = 0f
    private var unit = 0f // وحدة قياس نسبية حسب أصغر بعد

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // نحافظ على رسم نظيف مهما كان المقاس
        val size = min(w, h).toFloat()
        unit = size / 10f
        centerX = w / 2f

        // المبنى الأساسي
        pathMainBuilding.reset()
        val bLeft = centerX - 3.2f * unit
        val bRight = centerX + 3.2f * unit
        val bTop = 5.2f * unit
        val bBottom = 8.8f * unit
        pathMainBuilding.addRect(bLeft, bTop, bRight, bBottom, Path.Direction.CW)

        // القبة الرئيسية (نصف دائرة + قاعدة صغيرة)
        rectMainDome.set(centerX - 3.0f * unit, 3.3f * unit, centerX + 3.0f * unit, 6.0f * unit)
        // القبتان الجانبيتان
        rectLeftDome.set(centerX - 4.7f * unit, 4.6f * unit, centerX - 2.8f * unit, 6.1f * unit)
        rectRightDome.set(centerX + 2.8f * unit, 4.6f * unit, centerX + 4.7f * unit, 6.1f * unit)

        // الباب (قوس)
        val doorW = 1.8f * unit
        val doorH = 2.2f * unit
        rectDoor.set(centerX - doorW / 2, bBottom - doorH, centerX + doorW / 2, bBottom)
        rectDoorArch.set(rectDoor.left, rectDoor.top - doorW / 2, rectDoor.right, rectDoor.top + doorW / 2)

        // المئذنة اليسار (مستطيل + رأس مثلث + هلال صغير)
        pathMinaretLeft.reset()
        val mlX = centerX - 4.7f * unit
        val mW = 0.8f * unit
        val mH = 3.8f * unit
        val mlTop = 4.0f * unit
        // البرج
        pathMinaretLeft.addRect(mlX - mW / 2, mlTop, mlX + mW / 2, mlTop + mH, Path.Direction.CW)
        // رأس مثلث
        pathMinaretLeft.moveTo(mlX, mlTop - 0.7f * unit)
        pathMinaretLeft.lineTo(mlX - 0.55f * mW, mlTop)
        pathMinaretLeft.lineTo(mlX + 0.55f * mW, mlTop)
        pathMinaretLeft.close()

        // المئذنة اليمين
        pathMinaretRight.reset()
        val mrX = centerX + 4.7f * unit
        pathMinaretRight.addRect(mrX - mW / 2, mlTop, mrX + mW / 2, mlTop + mH, Path.Direction.CW)
        pathMinaretRight.moveTo(mrX, mlTop - 0.7f * unit)
        pathMinaretRight.lineTo(mrX - 0.55f * mW, mlTop)
        pathMinaretRight.lineTo(mrX + 0.55f * mW, mlTop)
        pathMinaretRight.close()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // القبة الرئيسية
        canvas.drawArc(rectMainDome, 180f, 180f, true, fill)
        canvas.drawArc(rectMainDome, 180f, 180f, true, stroke)
        // قاعدة بسيطة للقبة
        val base = RectF(rectMainDome.left - 0.2f * unit, rectMainDome.centerY(),
            rectMainDome.right + 0.2f * unit, rectMainDome.centerY() + 0.25f * unit)
        canvas.drawRect(base, fill)

        // القبتان الجانبيتان
        canvas.drawArc(rectLeftDome, 180f, 180f, true, fill)
        canvas.drawArc(rectRightDome, 180f, 180f, true, fill)

        // المبنى
        canvas.drawPath(pathMainBuilding, fill)
        canvas.drawPath(pathMainBuilding, stroke)

        // الباب والقوس
        val doorColor = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#2B6D3E")
        }
        canvas.drawRect(rectDoor, doorColor)
        canvas.drawArc(rectDoorArch, 180f, 180f, true, doorColor)

        // المآذن
        canvas.drawPath(pathMinaretLeft, fill)
        canvas.drawPath(pathMinaretRight, fill)

        // الأهلة الصغيرة أعلى المآذن
        drawCrescent(canvas, centerX - 4.7f * unit, 3.1f * unit, unit * 0.38f)
        drawCrescent(canvas, centerX + 4.7f * unit, 3.1f * unit, unit * 0.38f)

        // هلال أكبر فوق القبة الرئيسية
        drawCrescent(canvas, centerX, 2.1f * unit, unit * 0.55f)
    }

    private fun drawCrescent(canvas: Canvas, cx: Float, cy: Float, r: Float) {
        // دائرة ذهبية + قطع بدائرة سوداء لصنع الهلال
        canvas.drawCircle(cx, cy, r, gold)
        canvas.drawCircle(cx + 0.33f * r, cy - 0.10f * r, 0.82f * r, cut)
    }

    private fun dp(x: Float) = x * resources.displayMetrics.density
}
