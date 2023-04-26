package hummfinderapp.alpha.unassigned

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import hummfinderapp.alpha.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class DialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val pointPosition: PointF = PointF(0.0f, 0.0f)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }



    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    //change of tic distance or axis lines based on screen distance
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

    private fun PointF.computeXYForSpeed() {

    }

}
