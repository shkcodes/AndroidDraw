package com.divyanshu.draw.tool

import android.graphics.Canvas
import android.graphics.Paint
import com.divyanshu.draw.brush.Marker
import com.divyanshu.draw.tool.DrawingPath.MarkerPath
import com.divyanshu.draw.widget.TouchHandler
import kotlin.math.absoluteValue


/**
 * Created by shashank@fueled.com on 04/09/20.
 */
class MarkerTool(private val brush: Marker) : TouchHandler {
    private var mLastX = 0F
    private var mLastY = 0F

    private val positions = mutableListOf<Float>()

    fun getPath() = positions

    override fun actionDown(x: Float, y: Float) {
        mLastX = x
        mLastY = y
        positions.add(x)
        positions.add(y)
    }

    override fun actionMove(x: Float, y: Float): Boolean {
        val isValidPosition = (x - mLastX).absoluteValue > 20 || (y - mLastY).absoluteValue > 20
        if (isValidPosition) {
            positions.add(x)
            positions.add(y)
            mLastX = x
            mLastY = y
        }
        return isValidPosition
    }

    override fun actionUp(x: Float, y: Float): MarkerPath {
        val result = positions.toList()
        positions.clear()
        return MarkerPath(result)
    }

    fun draw(canvas: Canvas, pathData: List<Float>, paint: Paint) = brush.draw(canvas, pathData, paint)

}