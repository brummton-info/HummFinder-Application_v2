package hummfinderapp.alpha.lineChart

open class LineChartAdapter(private var yData: FloatArray = floatArrayOf()) : ChartAdapter() {

    override val count: Int
        get() = yData.size

    open fun setData(yData: FloatArray) {
        this.yData = yData
        notifyDataSetChanged()
    }

    override fun hasBaseLine(): Boolean {
        for (value in yData) {
            if (value < 0)
                return true
        }
        return false
    }

    override fun getItem(index: Int): Any =
        yData[index]

    override fun getY(index: Int): Float =
        yData[index]

    //clears data
    fun clearData(){
        setData(floatArrayOf())
    }
}