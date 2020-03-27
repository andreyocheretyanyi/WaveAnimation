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
    var diff = 0f
    private val func: (Float, Int, Float, Float, Float, Float, Boolean) -> PointF =
        { ampl, x, width, offset, funcCount, ferq, reverse ->
            PointF(
                x + offset,
                ampl * sin(ferq * (x / width * funcCount) + if (reverse) -diff else diff)
            )
        }
    private var verticalMid = 0.0f


    private val paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
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

    private val amplitude = 20f

    private lateinit var gradient: LinearGradient


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

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


        for (i in 0..1) {
            path.reset()
            when (i) {
                0 -> drawSinus(canvas)
                1 -> drawCubicTo(canvas)
            }

        }

        if (!heightAnimator.isRunning) {
            heightAnimator.start()
        }

    }

    private fun drawCubicTo(canvas: Canvas?) {
        val initX = width * 0.5f
        val initY =
            func(
                amplitude,
                initX.toInt(),
                width.toFloat(),
                0F,
                2f,
                PI.toFloat(),
                false
            ).y + verticalMid
        val seconPoint =
            func(amplitude, (width * 0.55f).toInt(), width.toFloat(), 0F, 2f, PI.toFloat(), true)
        val thirdPoind = func(amplitude, width, width.toFloat(), 0F, 2f, PI.toFloat(), true)
        path.moveTo(initX, initY)
        path.cubicTo(
            width.toFloat(),
            initY,
            seconPoint.x,
            seconPoint.y,
            thirdPoind.x,
            thirdPoind.y
        )
        path.lineTo(width.toFloat(), height.toFloat());
        path.lineTo(0F, height.toFloat());
        path.close()
        canvas!!.drawPath(path, paint)
    }

    private fun drawSinus(canvas: Canvas?) {
        for (x in 0..width step 5) {
            val point = func(amplitude, x, width.toFloat(), 0f, 2f, PI.toFloat(), false)
            if (x == 0) {
                path.moveTo(point.x, point.y + verticalMid)
            } else {
                path.lineTo(point.x, point.y + verticalMid)
            }

        }
        path.lineTo(width.toFloat(), height.toFloat());
        path.lineTo(0F, height.toFloat());
        path.close()
        canvas!!.drawPath(path, paint)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        verticalMid = height * 0.9f
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


    private val heightAnimator = ValueAnimator.ofFloat(-10f, 10f).apply {
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        duration = 40000
        addUpdateListener {
            swapPositionsAndColorsGradient()
            diff = it.animatedValue as Float * 5
            invalidate()
        }

    }


}