package com.divyanshu.draw.tool

import com.divyanshu.draw.widget.MyPath


/**
 * Created by shashank@fueled.com on 06/09/20.
 */

sealed class DrawingPath {
    abstract val path: Any

    data class PenPath(override val path: MyPath) : DrawingPath()
    data class MarkerPath(override val path: List<Float>) : DrawingPath()
}