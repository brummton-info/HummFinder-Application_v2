package hummfinderapp.alpha.calibration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import java.security.KeyStore

class Spectrum(context: Context?, attrs: AttributeSet?): View(context, attrs) {

    private var paint = Paint()
    private var m = floatArrayOf(70f, 220f, 157f, 400f, 30f)
    var path = Path()

    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.isAntiAlias
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        println(height)
        println(width)

        //x-axis
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), paint)
        //y-axis
        canvas.drawLine(0f, height.toFloat(), 0f, 0f, paint)

        path.moveTo(0f, height.toFloat() - 0f)

        canvas.drawPath(path, paint)
    }

    fun arrayIndex(arr: FloatArray){
        invalidate()
        for (i in arr.indices) {
            path.lineTo(i.toFloat(), height.toFloat() - arr[i])
        }
    }

}