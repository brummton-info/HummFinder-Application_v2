package hummfinderapp.alpha.lineChart

import android.content.Context
import android.content.res.TypedArray
import android.database.DataSetObserver
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import hummfinderapp.alpha.R


import java.util.*
import kotlin.math.log10

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.LineChartStyle,
    defStyleRes: Int = R.style.LineChartView
) : View(context, attrs, defStyleAttr){

    var lineWidth: Float = 3.0f
    var adapter: ChartAdapter? = null
        set(value) {
            field?.unregisterDataSetObserver(dataSetObserver)
            field = value
            field?.registerDataSetObserver(dataSetObserver)
            populatePath()
        }

    private val linePath = Path()
    private val renderPath = Path()
    private var scaleHelper: ScaleHelper? = null
    private val contentRect = RectF()
    private val contentRectMain = RectF()
    private val contentRectAdapter = RectF()

    private val dataSetObserver = object : DataSetObserver() {

        override fun onChanged() {
            super.onChanged()
            populatePath()
        }

        override fun onInvalidated() {
            super.onInvalidated()
            clearData()
        }
    }

    init {
    }

    private var linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#37A1D1")
        strokeWidth = lineWidth
        strokeMiter = 2f
        strokeCap = Paint.Cap.SQUARE
        pathEffect = CornerPathEffect(2f)
    }

    private var axesPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#BBBBBB")
        strokeWidth = lineWidth
        strokeMiter = 2f
        strokeCap = Paint.Cap.SQUARE
        pathEffect = CornerPathEffect(0f)
    }

    private var contentRectMainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#BBBBBB")
        strokeWidth = lineWidth
        strokeMiter = 2f
        strokeCap = Paint.Cap.SQUARE
        pathEffect = CornerPathEffect(0f)
    }

    private var axesLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
        textSize = 18.0f
    }

    private var axesTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
        textSize = 25.0f
    }

    private fun populatePath() {
        if (adapter == null || width == 0 || height == 0)
            return

        if (adapter!!.count < 2) {
            clearData()
            return
        }

        scaleHelper = ScaleHelper(adapter!!, contentRect, lineWidth)
        linePath.reset()

        for (i in 0 until adapter!!.count) {
            if (i < adapter!!.count) {
                val x = scaleHelper!!.getX(adapter!!.getX(i))
                val y = scaleHelper!!.getY(adapter!!.getY(i))

                if (i == 0)
                    linePath.moveTo(x, y)
                else
                    linePath.lineTo(x, y)
            }
        }
        //render axes and tics if possible
        renderPath.reset()
        renderPath.addPath(linePath)
        invalidate()
    }

    /*private fun getYByIndex(index: Int) = scaleHelper!!.getY(adapter!!.getY(index))*/

    private fun clearData() {
        scaleHelper = null
        renderPath.reset()
        linePath.reset()
        invalidate()
    }

    /*
    private fun updateContentRect() {
        contentRect.set(
            paddingStart.toFloat(),
            paddingTop.toFloat(),
            (width - paddingEnd).toFloat(),
            (height - paddingBottom).toFloat()
        )
    }*/

    private fun updateContentRectMain(){
        contentRectMain.set(
            paddingStart.toFloat(),
            paddingTop.toFloat(),
            (width - paddingEnd).toFloat(),
            (height - paddingBottom).toFloat()
        )
    }

    private fun updateContentRectAdapter(){
        contentRectAdapter.set(
            contentRectMain.left + 120.0f,
            contentRectMain.top + 30.0f,
            contentRectMain.right - 30.0f,
            contentRectMain.bottom - 100.0f
        )
    }
    private fun updateContentRect() {
        contentRect.set(
            contentRectAdapter.left,
            contentRectAdapter.top,
            contentRectAdapter.right,
            contentRectAdapter.bottom
        )
    }

    //Guidelines Spacing: TODO: Taking out calculation responsibilities from onDraw method
    //private fun gridLineSpacing(contentRectF: RectF, gridLineXisTrue: Boolean):FloatArray{
    private fun gridLineSpacing(gridLineXisTrue: Boolean):FloatArray{
        if (gridLineXisTrue){
            //200Hz: 1 Tick: 10 (200/20)
            //val gridLineSpacingX: Float = contentRectF.width()/2.3f //for Log scale

            /* LOG SCALE */
            /*
            println("gridLineSpacingX: $gridLineSpacingX")
            var spacingXinit: FloatArray = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f,
                20.0f, 30.0f, 40.0f, 50.0f, 60.0f, 70.0f, 80.0f, 90.0f, 100.0f, 200.0f, 300.0f, 400.0f)
            for (i in spacingXinit){
                spacingX += contentRect.left + (log10(i) * gridLineSpacingX)
            }
            */

            /* LINEAR SCALE */
            val gridLineSpacingX: Float = contentRect.width()/20.0f //for Linear scale
            var spacingX = FloatArray(19)
            for (j in 1..19){
                spacingX += contentRect.left + j * gridLineSpacingX
            }
            return spacingX
        }
        else{
            var gridLineSpacingY: Float = contentRect.height()/14f
            var spacingY = FloatArray(13)
            for (i in 1..13){
                spacingY += contentRect.top + i * gridLineSpacingY
            }
            return spacingY
        }
    }

    //Axis Labels
    private fun getXAxisLabels():FloatArray {
        var xLabelsMeasure = FloatArray(11)
        //val axisLineSpacingX: Float = contentRectS.width()/20.0f
        val axisLineSpacingX: Float = contentRect.width()/10.0f
        //for Linear scale
        for (j in 1..10){
            xLabelsMeasure += j * axisLineSpacingX
        }
        return xLabelsMeasure
    }

    private fun getxAxisLabelText(): FloatArray{
        var labelText = FloatArray(10)
        for (m in 0..200 step 20){
            labelText += m.toFloat()
        }
        return labelText
    }

    private fun getYAxisLabels(): FloatArray {
        var yLabelsMeasure = FloatArray(6)
        val axisLineSpacingX: Float = contentRect.height()/7f   //linear scale
        for (j in 0..7){
            yLabelsMeasure += j * axisLineSpacingX
        }
        return yLabelsMeasure
    }

    private fun getyAxisLabelText(): FloatArray{
        var labelText = FloatArray(6)
        for (n in 0 downTo -140 step 20){
            labelText += n.toFloat()
        }
        return labelText
    }

    private fun axesTitle(xory: Boolean, width_height: Float): FloatArray {
        var x_y = FloatArray(2)
        if (xory){

            return x_y
        }
        else {
            return x_y
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        updateContentRectMain()
        updateContentRectAdapter()
        updateContentRect()
        populatePath()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        updateContentRectMain()
        updateContentRectAdapter()
        updateContentRect()
        populatePath()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(renderPath, linePaint)
        canvas.drawRect(contentRect, axesPaint)
        canvas.drawRect(contentRectMain, contentRectMainPaint)
        //Grid y-axis
        val guideLineSpacingY: FloatArray = gridLineSpacing(false)
        for (i in guideLineSpacingY){
            canvas.drawLine(contentRect.left, i, contentRect.right, i, axesPaint)
        }
        //grid x-axis
        val guideLineSpacingX: FloatArray = gridLineSpacing(true)
        for (j in guideLineSpacingX){
            canvas.drawLine(j, contentRect.bottom, j, contentRect.top, axesPaint)
        }

        val getxAxisLabels: FloatArray = getXAxisLabels()
        val getxAxisLabelText: FloatArray = getxAxisLabelText()
        getxAxisLabels.forEachIndexed { index, fl -> //i , arr[i]
            canvas.drawText(getxAxisLabelText[index].toString(), contentRect.left + fl, contentRectMain.bottom - 60.0f, axesLabelPaint)
            //canvas.drawText(fl.toString(), contentRect.left + fl, contentRectMain.bottom - 20.0f, axesLabelPaint)
        }

        val getyAxisLabels: FloatArray = getYAxisLabels()
        val getyAxisLabelText: FloatArray = getyAxisLabelText()
        getyAxisLabels.forEachIndexed { index, fl ->
            canvas.drawText(getyAxisLabelText[index].toString(), contentRectMain.left + 80.0f, contentRect.top + fl + 8.0f, axesLabelPaint)
        }

        //canvas.drawText("Decibel (dB)", contentRectMain.left + 20.0f, contentRect.height()/2.0f, axesLabelPaint)
        canvas.save()
        canvas.rotate(-90.0f,contentRectMain.left + 40.0f,contentRect.height()/2 )
        canvas.drawText("Decibel (dB)", contentRectMain.left + 40.0f, contentRect.height()/2, axesTitlePaint)
        canvas.restore()
        canvas.drawText("Hertz (Hz)", contentRectMain.width()/2, contentRectMain.bottom - 20.0f, axesTitlePaint)
    }

}