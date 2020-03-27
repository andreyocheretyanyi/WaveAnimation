package ua.codeasylum.myapplication

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import java.math.BigDecimal


class WaveView : View {

    constructor(context: Context) : super(context)
    constructor (context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor (
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    private val path = Path()
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        strokeWidth = 5f
    }

    private lateinit var gradient: LinearGradient
    private var gradientPoint1 = BigDecimal(-.5)
    private var gradientPoint2 = BigDecimal(0)
    private var gradientPoint3 = BigDecimal(.5)
    private var gradientPoint4 = BigDecimal(1)
    private val step = BigDecimal(0.0025)
    private val compareArg = BigDecimal(1.0)

    private var colors = intArrayOf(
        ContextCompat.getColor(context, R.color.colorPurple),
        ContextCompat.getColor(context, R.color.colorOrange),
        ContextCompat.getColor(context, R.color.colorRed),
        ContextCompat.getColor(context, R.color.colorPurple)

    )

    private var diff = 0f
    private val valueForNormalizeDiff = 50f
    private val initialPoint = PointF()
    private val point11 = PointF()
    private val point12 = PointF()
    private val point13 = PointF()
    private val point21 = PointF()
    private val point22 = PointF()
    private val point23 = PointF()


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        path.reset()

        if (!::gradient.isInitialized)
            gradient = LinearGradient(
                0f,
                height.toFloat(),
                width.toFloat(),
                height.toFloat(),
                colors,
                floatArrayOf(
                    gradientPoint1.toFloat(),
                    gradientPoint2.toFloat(),
                    gradientPoint3.toFloat(),
                    gradientPoint4.toFloat()
                ),
                Shader.TileMode.CLAMP
            )

        paint.shader = gradient


        setPoints()
        path.moveTo(initialPoint.x, initialPoint.y)


        path.cubicTo(
            point11.x, point11.y,
            point12.x, point12.y,
            point13.x, point13.y
        )
        path.cubicTo(
            point21.x, point21.y,
            point22.x, point22.y,
            point23.x, point23.y
        )

        path.lineTo(width.toFloat(), height.toFloat());
        path.lineTo(0F, height.toFloat());
        canvas!!.drawPath(path, paint)

        if (!heightAnimator.isRunning) {
            heightAnimator.start()
        }

    }

    private fun setPoints() {
        val verticalMid = height / 1.2f

        initialPoint.x = -valueForNormalizeDiff
        initialPoint.y = verticalMid + diff

        point11.set(width * 0.25f, verticalMid - (height - verticalMid) * 0.5f + diff)
        point12.set(width * 0.33f, (height - verticalMid) * 0.5f + verticalMid - diff)
        point13.set(width * 0.5f, (height - verticalMid) * 0.3f  + verticalMid - diff)

        point21.set(width * 0.7f, verticalMid - diff)
        point22.set(width * 0.65f - diff, height*0.15f - diff)
        point23.set(width.toFloat() + valueForNormalizeDiff + diff, height*0.15f - diff)
    }

    private fun swapPositionsAndColorsGradient() {
        if (gradientPoint3.compareTo(compareArg) == 0 || gradientPoint3.compareTo(compareArg) == 1) {
            gradientPoint4 = gradientPoint3
            gradientPoint3 = gradientPoint2
            gradientPoint2 = gradientPoint1
            gradientPoint1 = BigDecimal(-.5)
            val col1 = colors[0]
            val col2 = colors[1]
            val col3 = colors[2]
            colors = intArrayOf(col3, col1, col2, col3)
        }
        gradient = LinearGradient(
            0f,
            height.toFloat(),
            width.toFloat(),
            height.toFloat(),
            colors,
            floatArrayOf(
                gradientPoint1.toFloat(),
                gradientPoint2.toFloat(),
                gradientPoint3.toFloat(),
                gradientPoint4.toFloat()
            ),
            Shader.TileMode.CLAMP
        )

        gradientPoint1 = gradientPoint1.add(step)
        gradientPoint2 = gradientPoint2.add(step)
        gradientPoint3 = gradientPoint3.add(step)
        gradientPoint4 = gradientPoint4.add(step)
    }


    private val heightAnimator =
        ValueAnimator.ofFloat(-1f, 1f).apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            duration = 1500
            addUpdateListener {
                swapPositionsAndColorsGradient()
                diff = (it.animatedValue as Float) * 50
                invalidate()

            }

        }


}