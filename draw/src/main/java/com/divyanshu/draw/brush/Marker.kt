package com.divyanshu.draw.brush

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.divyanshu.draw.R
import kotlin.math.sqrt


/**
 * Created by shashank@fueled.com on 03/09/20.
 */
class Marker(context: Context, private val markerOpacity: Int) : Brush<List<Float>> {

    companion object {
        private const val FREQUENCY = 20
        private const val HEIGHT_FACTOR = 8
        private const val WIDTH_FACTOR = 2
        private const val ROTATION = 45F
    }

    private val minSize = context.resources.getDimension(R.dimen.marker_min_stroke_size)
    private val maxSize = context.resources.getDimension(R.dimen.marker_max_stroke_size)

    private val rect = RectF()

    override fun draw(canvas: Canvas, pathData: List<Float>, paint: Paint) {
        canvas.saveLayerAlpha(0F, 0F, canvas.width.toFloat(), canvas.height.toFloat(), markerOpacity)
        val size = paint.strokeWidth
        val sizeInPixel = (minSize + size * (maxSize - minSize))
        val step = sizeInPixel / FREQUENCY
        val halfHeight = sizeInPixel / HEIGHT_FACTOR
        val halfWidth = sizeInPixel / WIDTH_FACTOR
        var lastX = -1F
        var lastY = -1F

        for (i in pathData.indices step 2) {
            if (lastX == -1F) {
                lastX = pathData[i]
                lastY = pathData[i + 1]
                if (pathData.size == 2) {
                    canvas.rotate(-ROTATION, lastX, lastY)
                    rect.left = lastX - halfWidth
                    rect.top = lastY - halfHeight
                    rect.right = lastX + halfWidth
                    rect.bottom = lastY + halfHeight
                    canvas.drawOval(rect, paint)
                    canvas.rotate(ROTATION, lastX, lastY)
                }
            } else {
                val dx = pathData[i] - lastX
                val dy = pathData[i + 1] - lastY
                val distance = sqrt(dx * dx + dy * dy)
                if (distance < step) return
                val steppingFactor = step / distance
                var i = 0f

                while (i <= 1) {
                    val xCenter = lastX + i * dx
                    val yCenter = lastY + i * dy
                    canvas.rotate(-ROTATION, xCenter, yCenter)
                    rect.left = xCenter - halfWidth
                    rect.top = yCenter - halfHeight
                    rect.right = xCenter + halfWidth
                    rect.bottom = yCenter + halfHeight
                    canvas.drawOval(rect, paint)
                    canvas.rotate(ROTATION, xCenter, yCenter)
                    i += steppingFactor
                }
                lastX += i * dx
                lastY += i * dy
            }
        }
        canvas.restore()
    }
}