package com.divyanshu.draw.brush

import android.graphics.Canvas
import android.graphics.Paint


/**
 * Created by shashank@fueled.com on 03/09/20.
 */
interface Brush<T> {
    fun draw(canvas: Canvas, pathData: T, paint: Paint)
}

enum class Brushes {
    PEN, MARKER
}