package com.darkrockstudios.apps.cameralevel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View

class LevelView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val lineNotLevelPaint = Paint()
    private val lineLevelPaint = Paint()

    private val tolerence = 0.5f

    private var roll: Float = 0f
    private var pitch: Float = 0f
    private var yaw: Float = 0f

    init {
        lineNotLevelPaint.apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.MITER
            strokeCap = Paint.Cap.SQUARE
            color = Color.RED
            strokeWidth = 16f
            alpha = 255
        }

        lineLevelPaint.apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.MITER
            strokeCap = Paint.Cap.SQUARE
            color = Color.GREEN
            strokeWidth = 16f
            alpha = 255
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paint = if(roll in -tolerence..tolerence) lineLevelPaint else lineNotLevelPaint

        canvas?.apply {
            val centerY = (height / 2).toFloat()
            val centerX = (width / 2).toFloat()

            rotate(roll, centerX, centerY)

            drawLine(0f, centerY, width.toFloat(), centerY, paint)
        }
    }

    fun updateOrientation(roll: Float, pitch: Float, yaw: Float) {
        Log.d("test", "roll $roll pitch $pitch yaw $yaw")

        this.roll = roll
        this.pitch = pitch
        this.yaw = yaw

        invalidate()
    }
}