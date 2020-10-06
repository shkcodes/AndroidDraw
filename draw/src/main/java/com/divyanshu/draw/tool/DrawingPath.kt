package com.divyanshu.draw.tool

import com.divyanshu.draw.widget.MyPath


/**
 * Created by shashank@fueled.com on 06/09/20.
 */

sealed class DrawingPath {
    data class PenPath(val path: MyPath) : DrawingPath()
    data class MarkerPath(val path: List<Float>) : DrawingPath()
}