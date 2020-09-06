package com.divyanshu.draw.widget

import com.divyanshu.draw.tool.DrawingPath


/**
 * Created by shashank@fueled.com on 04/09/20.
 */
interface TouchHandler {
    fun actionDown(x: Float, y: Float)
    fun actionMove(x: Float, y: Float): Boolean
    fun actionUp(x: Float, y: Float): DrawingPath
}
