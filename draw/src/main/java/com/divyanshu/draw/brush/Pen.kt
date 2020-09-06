package com.divyanshu.draw.brush

import android.graphics.Canvas
import android.graphics.Paint
import com.divyanshu.draw.widget.MyPath


/**
 * Created by shashank@fueled.com on 03/09/20.
 */
class Pen : Brush<MyPath> {

    override fun draw(canvas: Canvas, pathData: MyPath, paint: Paint) {
        canvas.drawPath(pathData, paint)
    }
}