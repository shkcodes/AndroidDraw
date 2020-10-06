package com.divyanshu.draw.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import com.divyanshu.draw.brush.Brushes.MARKER
import com.divyanshu.draw.brush.Brushes.PEN
import com.divyanshu.draw.brush.Marker
import com.divyanshu.draw.brush.Pen
import com.divyanshu.draw.tool.DrawingPath
import com.divyanshu.draw.tool.DrawingPath.MarkerPath
import com.divyanshu.draw.tool.DrawingPath.PenPath
import com.divyanshu.draw.tool.MarkerTool
import com.divyanshu.draw.tool.PenTool


/**
 * Created by shashank@fueled.com on 04/09/20.
 */
class DrawingHandler(
    private val penPaint: Paint,
    private val markerPaint: Paint,
    markerAlpha: Int,
    context: Context
) {
    private val penTool = PenTool(Pen())
    private val markerTool = MarkerTool(Marker(context, markerAlpha))

    private val brushToolMap = mapOf(
        PEN to penTool,
        MARKER to markerTool
    )

    var currentBrush = MARKER

    fun actionDown(x: Float, y: Float) {
        brushToolMap.getValue(currentBrush).actionDown(x, y)
    }

    fun actionMove(x: Float, y: Float): Boolean {
        return brushToolMap.getValue(currentBrush).actionMove(x, y)
    }

    fun actionUp(x: Float, y: Float): DrawingPath {
        return brushToolMap.getValue(currentBrush).actionUp(x, y)
    }

    fun draw(canvas: Canvas, path: DrawingPath) {
        when (path) {
            is PenPath -> {
                penTool.draw(canvas, path.path, penPaint)
            }
            is MarkerPath -> {
                markerTool.draw(canvas, path.path, markerPaint)
            }
        }
    }

    fun draw(canvas: Canvas) {
        when (currentBrush) {
            PEN -> {
                penTool.draw(canvas, penTool.getPath(), penPaint)
            }
            MARKER -> {
                markerTool.draw(canvas, markerTool.getPath(), markerPaint)
            }
        }
    }

    fun clear() {
        penTool.reset()
    }
}