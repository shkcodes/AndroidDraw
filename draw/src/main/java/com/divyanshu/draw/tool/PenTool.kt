package com.divyanshu.draw.tool

import android.graphics.Canvas
import android.graphics.Paint
import com.divyanshu.draw.brush.Pen
import com.divyanshu.draw.tool.DrawingPath.PenPath
import com.divyanshu.draw.widget.MyPath
import com.divyanshu.draw.widget.TouchHandler


/**
 * Created by shashank@fueled.com on 04/09/20.
 */
class PenTool(private val brush: Pen) : TouchHandler {
    private var mCurX = 0f
    private var mCurY = 0f
    private var mStartX = 0f
    private var mStartY = 0f

    private var path = MyPath()

    fun getPath() = path

    override fun actionDown(x: Float, y: Float) {
        mStartX = x
        mStartY = y
        path.reset()
        path.moveTo(x, y)
        mCurX = x
        mCurY = y
    }

    override fun actionMove(x: Float, y: Float): Boolean {
        path.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2)
        mCurX = x
        mCurY = y
        return true
    }

    override fun actionUp(x: Float, y: Float): PenPath {
        path.lineTo(mCurX, mCurY)

        if (mStartX == mCurX && mStartY == mCurY) {
            path.lineTo(mCurX, mCurY + 2)
            path.lineTo(mCurX + 1, mCurY + 2)
            path.lineTo(mCurX + 1, mCurY)
        }
        val result = PenPath(path)
        path = MyPath()
        return result
    }

    fun reset() {
        path.reset()
    }

    fun draw(canvas: Canvas, pathData: MyPath, paint: Paint) = brush.draw(canvas, pathData, paint)

}