package com.divyanshu.draw.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.divyanshu.draw.brush.Brushes
import com.divyanshu.draw.tool.DrawingPath
import java.util.LinkedHashMap

open class DrawView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_MARKER_OPACITY = 150
        private const val MARKER_WIDTH_FACTOR = 100F
    }

    private var mPaths = LinkedHashMap<DrawingPath, PaintOptions>()

    private var mLastPaths = LinkedHashMap<DrawingPath, PaintOptions>()
    private var mUndonePaths = LinkedHashMap<DrawingPath, PaintOptions>()

    private var brushPaint = Paint()
    private var markerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPaintOptions = PaintOptions()

    private var mIsSaving = false
    private var mIsStrokeWidthBarEnabled = false

    var listener: DrawListener? = null

    var isEraserOn = false
        private set

    val isCleared: Boolean
        get() = mPaths.isEmpty()

    var markerOpacity: Int = DEFAULT_MARKER_OPACITY

    private val drawingHandler = DrawingHandler(brushPaint, markerPaint, markerOpacity, context)

    fun setCurrentBrush(brush: Brushes) {
        drawingHandler.currentBrush = brush
    }

    init {
        brushPaint.apply {
            color = mPaintOptions.color
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mPaintOptions.strokeWidth
            isAntiAlias = true
        }
    }

    fun undo() {
        if (mPaths.isEmpty() && mLastPaths.isNotEmpty()) {
            mPaths = mLastPaths.clone() as LinkedHashMap<DrawingPath, PaintOptions>
            mLastPaths.clear()
            invalidate()
            return
        }
        if (mPaths.isEmpty()) {
            return
        }
        val lastPath = mPaths.values.lastOrNull()
        val lastKey = mPaths.keys.lastOrNull()

        mPaths.remove(lastKey)
        if (lastPath != null && lastKey != null) {
            mUndonePaths[lastKey] = lastPath
            listener?.onUndo(lastKey)
        }
        invalidate()
    }

    fun redo() {
        if (mUndonePaths.keys.isEmpty()) {
            return
        }

        val lastKey = mUndonePaths.keys.last()
        mPaths[lastKey] = mUndonePaths.values.last()
        listener?.onPathDrawn(lastKey to mUndonePaths.values.last())
        mUndonePaths.remove(lastKey)
        invalidate()
    }

    fun setColor(newColor: Int) {
        @ColorInt
        val alphaColor = ColorUtils.setAlphaComponent(newColor, mPaintOptions.alpha)
        mPaintOptions.color = alphaColor
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }
    }

    fun setAlpha(newAlpha: Int) {
        val alpha = (newAlpha * 255) / 100
        mPaintOptions.alpha = alpha
        setColor(mPaintOptions.color)
    }

    fun setStrokeWidth(newStrokeWidth: Float) {
        mPaintOptions.strokeWidth = newStrokeWidth
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }
    }

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        mIsSaving = true
        draw(canvas)
        mIsSaving = false
        return bitmap
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for ((key, value) in mPaths) {
            changePaint(value)
            drawingHandler.draw(canvas, key)
        }

        changePaint(mPaintOptions)
        drawingHandler.draw(canvas)
    }

    private fun changePaint(paintOptions: PaintOptions) {
        brushPaint.color = if (paintOptions.isEraserOn) Color.WHITE else paintOptions.color
        brushPaint.strokeWidth = paintOptions.strokeWidth
        markerPaint.color = paintOptions.color
        markerPaint.strokeWidth = paintOptions.strokeWidth / MARKER_WIDTH_FACTOR
    }

    fun reset() {
        mPaths.clear()
        mLastPaths.clear()
        drawingHandler.clear()
        invalidate()
    }

    fun renderPaths(paths: LinkedHashMap<DrawingPath, PaintOptions>) {
        paths.keys.forEach { key ->
            mPaths[key] = paths[key]!!
        }
        invalidate()
    }

    fun clearCanvas() {
        mLastPaths = mPaths.clone() as LinkedHashMap<DrawingPath, PaintOptions>
        mPaths.clear()
        drawingHandler.clear()
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawingHandler.actionDown(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                val result = drawingHandler.actionMove(x, y)
                if (result) invalidate()
            }
            MotionEvent.ACTION_UP -> {
                val result = drawingHandler.actionUp(x, y)
                mPaths[result] = mPaintOptions
                listener?.onPathDrawn(result to mPaintOptions)
                mPaintOptions = PaintOptions(mPaintOptions.color, mPaintOptions.strokeWidth, mPaintOptions.alpha, mPaintOptions.isEraserOn)
                invalidate()
            }
        }
        return true
    }

    fun toggleEraser() {
        isEraserOn = !isEraserOn
        mPaintOptions.isEraserOn = isEraserOn
        invalidate()
    }

}

interface DrawListener {
    fun onPathDrawn(path: Pair<DrawingPath, PaintOptions>)
    fun onUndo(path: DrawingPath)
}