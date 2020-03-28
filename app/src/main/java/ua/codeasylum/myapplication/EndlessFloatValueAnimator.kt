package ua.codeasylum.myapplication

import android.animation.FloatEvaluator
import android.animation.TimeAnimator
import android.animation.TimeAnimator.TimeListener
import android.animation.TimeInterpolator
import android.view.animation.LinearInterpolator

class EndlessFloatValueAnimator : TimeAnimator() {

    private val evaluator = FloatEvaluator()
    private val timeListener: TimeListener =
        TimeListener { _, _, deltaTime ->
            currentTime += deltaTime
            if (currentTime <= animDuration) {
                val evaluatedValue =
                    evaluator.evaluate(
                        this.animInterpolator.getInterpolation(
                            ((currentTime / 1000f) / (animDuration / 1000f))
                        ),
                        start,
                        endFloat
                    )
                valueUpdateListener?.onValueUpdated(
                    evaluatedValue
                )
            } else {
                currentTime = 0L
                cycleEndListener?.onCycleEnd()
            }
        }

    private var currentTime = 0L
    private var start: Float = 0f
    private var endFloat: Float = 0f
    var cycleEndListener: CycleEndListener? = null
    var valueUpdateListener: ValueUpdateListener? = null

    var animDuration = 500L
    var animInterpolator: TimeInterpolator = LinearInterpolator()

    fun setValues(start: Float, endFloat: Float) {
        this.start = start
        this.endFloat = endFloat
    }

    interface CycleEndListener {
        fun onCycleEnd()
    }

    interface ValueUpdateListener {
        fun onValueUpdated(value: Float)
    }

    override fun start() {
        setTimeListener(timeListener)
        super.start()
    }
}