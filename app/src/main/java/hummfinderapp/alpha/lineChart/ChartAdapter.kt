package hummfinderapp.alpha.lineChart

import android.database.DataSetObservable
import android.database.DataSetObserver
import android.graphics.RectF

abstract class ChartAdapter {
    private val observable = DataSetObservable() //to notify object change, we will use LiveData
    //private var dataObs = MutableLiveData<ArrayList<Float>>()
    open val baseLine: Float = 0.0f

    //can be overridden but not compulsory
    open val dataBounds: RectF
        get() {
            val count = count
            val hasBaseLine = hasBaseLine()
            var minY =
                if (hasBaseLine) baseLine else Float.MAX_VALUE
            var maxY = if (hasBaseLine) minY else -Float.MAX_VALUE
            var minX = Float.MAX_VALUE
            var maxX = -Float.MAX_VALUE
            for (i in 0 until count) {
                val x = getX(i)
                minX = minX.coerceAtMost(x)
                maxX = maxX.coerceAtLeast(x)
                val y = getY(i)
                minY = minY.coerceAtMost(y)
                maxY = maxY.coerceAtLeast(y)
            }

            return createRectF(minX, minY, maxX, maxY)
        }

    private fun createRectF(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ): RectF = RectF(left, top, right, bottom)

    //abstract methods needs to be overridden by sub-class or child
    abstract val count : Int

    abstract fun getItem(index: Int): Any

    abstract fun getY(index: Int): Float

    fun getX(index: Int): Float =
        index.toFloat()

    open fun hasBaseLine(): Boolean =
        false


    //functions related to dataobservable, we can change to livedata
    fun notifyDataSetChanged() {
        observable.notifyChanged()
    }

    fun notifyDataSetInvalidated() {
        observable.notifyInvalidated()
    }

    fun registerDataSetObserver(observer: DataSetObserver) {
        observable.registerObserver(observer)
    }

    fun unregisterDataSetObserver(observer: DataSetObserver) {
        observable.unregisterObserver(observer)
    }
}