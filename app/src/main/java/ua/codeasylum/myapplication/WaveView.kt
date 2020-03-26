package ua.codeasylum.myapplication

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import java.math.BigDecimal
import kotlin.math.PI
import kotlin.math.sin


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
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeWidth = 5f
    }

    private var point1 = BigDecimal(-.5)
    private var point2 = BigDecimal(0)
    private var point3 = BigDecimal(.5)
    private var point4 = BigDecimal(1)
    private val step = BigDecimal(0.0025)
    private val compareArg = BigDecimal(1.0)

    private var colors = intArrayOf(
        ContextCompat.getColor(context, R.color.colorPurple),
        ContextCompat.getColor(context, R.color.colorOrange),
        ContextCompat.getColor(context, R.color.colorRed),
        ContextCompat.getColor(context, R.color.colorPurple)

    )

    private var diff = 0f
    private val amplitude = 60f

    private lateinit var gradient: LinearGradient

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
                    point1.toFloat(),
                    point2.toFloat(),
                    point3.toFloat(),
                    point4.toFloat()
                ),
                Shader.TileMode.CLAMP
            )

        paint.shader = gradient
        val verticalMid = height / 1.2f

        for (x in 0..width step 5) {

            val y = interpolateYByTime(x) + verticalMid


            if (x == 0) {
                path.moveTo(x.toFloat(), y.toFloat())
            } else {
                path.lineTo(x.toFloat(), y.toFloat())
            }

        }
//        path.lineTo(width.toFloat(), height.toFloat());
//        path.lineTo(0F, height.toFloat());
        canvas!!.drawPath(path, paint)

        if (!heightAnimator.isRunning) {
            heightAnimator.start()
        }

    }

    private fun interpolateYByTime(x: Int): Double {
        var y =
              amplitude * -sin(2*PI * (x.toFloat() / width))

        return y
    }

    private fun swapPositionsAndColorsGradient() {
        if (point3.compareTo(compareArg) == 0 || point3.compareTo(compareArg) == 1) {
            point4 = point3
            point3 = point2
            point2 = point1
            point1 = BigDecimal(-.5)
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
            floatArrayOf(point1.toFloat(), point2.toFloat(), point3.toFloat(), point4.toFloat()),
            Shader.TileMode.CLAMP
        )

        point1 = point1.add(step)
        point2 = point2.add(step)
        point3 = point3.add(step)
        point4 = point4.add(step)
    }


    private val heightAnimator = ValueAnimator.ofFloat(1f, .75f, .5f, .25f, 0f).apply {
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        duration = 20000
        addUpdateListener {
            swapPositionsAndColorsGradient()
            diff = (it.animatedValue as Float) * 100
            invalidate()
        }

    }


}