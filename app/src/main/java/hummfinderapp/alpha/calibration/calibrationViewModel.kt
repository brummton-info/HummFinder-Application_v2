package hummfinderapp.alpha.calibration

import android.app.Application
import android.graphics.Bitmap
import android.media.AudioRecord
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import hummfinderapp.alpha.matching.ToneGenerator
import hummfinderapp.alpha.repository.DataStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jtransforms.fft.FloatFFT_1D
import kotlin.math.abs

class calibrationViewModel(application: Application): AndroidViewModel(application) {

    val CHUNK_SIZE = 8192
    val WINDOW_SIZE = 65536
    val FREQUENCY_CUTOFF_UPPER = 300
    val WIDTH = FREQUENCY_CUTOFF_UPPER
    val HEIGHT = 200
    val BIN_SIZE = ((WINDOW_SIZE / 2) + 1) / WIDTH

    private var isCalibrating = false

    //DATA FROM DATASTORE
    private val repository = DataStoreRepository(application)
    val readFromDataStoreFrequency = repository.readFromDataStoreFrequency.asLiveData()
    val readFromDataStoreLevel = repository.readFromDataStoreLevel.asLiveData()

    fun saveToDataStore(frequency: String, level:String) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveToDataStore(frequency,level)
    }

    //TONE GENERATOR
    private val TONEGENERATOR = ToneGenerator()
    private val TAG = "CALIBRATION VIEWMODEL"

    //MUTABLE LIVE DATA LEVEL
    private var _level = MutableLiveData<Int>()
    fun level(): LiveData<Int>{
        return _level
    }

    //MUTABLE LIVE DATA SPECTROGRAM
    private val spectrogramColorArray = MutableLiveData<IntArray>(IntArray(WIDTH * HEIGHT))
    val generatedBitmap: LiveData<Bitmap> = Transformations.map(spectrogramColorArray) {
        bitmapFromData(it)
    }

    //MUTABLE LIVE DATA DECIBEL VALUES
    private var _decibelValuesToSpectrum = MutableLiveData<FloatArray>(FloatArray(WIDTH))
    fun decibelValuesToSpectrum(): LiveData<FloatArray>{
        return _decibelValuesToSpectrum
    }

    var TGFrequency: Double = 150.0
        set(frequency){
            field = frequency
            TONEGENERATOR.frequency = frequency
        }

    var TGLevel: Double = 0.3
        set(level) {
            field = level
            TONEGENERATOR.level = level
            _level.value = level.toInt()
        }

    //START TONE GENERATOR
    fun startToneGenerator(){
        TONEGENERATOR.start()
        isCalibrating = true
        Log.d(TAG,"Tone generator started")
    }

    //STOP TONE GENERATOR
    fun stopToneGenerator(){
        TONEGENERATOR.stop()
        Log.d(TAG, "Tone Generator Stopped > btn")
    }

    //INCREASE LEVEL BY ONE
    fun increaseLevelByOne(){
        val base = 10
        val exponent = 0.05
        TGLevel += Math.pow(base.toDouble(),exponent)
    }

    //DECREASE LEVEL BY ONE
    fun decreaseLevelByOne(){
        val base = 10
        val exponent = 0.05
        TGLevel -= Math.pow(base.toDouble(),exponent)
    }

    //SEEKBAR
    fun onSeekBarProgressChanged(progress:Float){
        TGLevel = progress.toDouble()
    }

    //AUDIO AND SPECTRUM
    fun audioToSpectrum(audioRecord: AudioRecord){
        Log.d(TAG,"AUDIORECORD TRANSFERED to VIEWMODEL with recording state: ${audioRecord.state} AND ${audioRecord.recordingState} ")
        val recordedSamples = ShortArray(WINDOW_SIZE)
        viewModelScope.launch {
            val fft = FloatFFT_1D(recordedSamples.size.toLong())

            while (true) {
                addHistogramRow(withContext(Dispatchers.Default) {

                    // Shift recordedSamples by one chunk
                    System.arraycopy(
                        recordedSamples,
                        CHUNK_SIZE,
                        recordedSamples,
                        0,
                        WINDOW_SIZE - CHUNK_SIZE
                    )

                    // Read one chunk into recordedSamples
                    audioRecord.read(recordedSamples, WINDOW_SIZE - CHUNK_SIZE, CHUNK_SIZE)
                    val floatArray = FloatArray(recordedSamples.size) {
                        recordedSamples[it].toFloat()
                    }

                    fft.realForward(floatArray)
                    val absolutes = absoluteFromComplex(floatArray)

                    val histogram = absolutes.copyOf(FREQUENCY_CUTOFF_UPPER)

                    val histogramInDecibel = decibelFromAbsolute(histogram)
                    val histogramInDecibelReverse = decibelFromAbsoluteReverse(histogram)
                    //val incomingAudioFloat = DoubletoFloatFun(histogramInDecibelReverse)
                    _decibelValuesToSpectrum.postValue(histogramInDecibel)
                    //_decibelValuesToSpectrum.postValue(histogramInDecibel)
                    println("Magnitudes Reverse: ${histogramInDecibelReverse.joinToString(",")}")

                    val minDecibel = histogramInDecibel.minOrNull()!!

                    val histogramNormalizedForColormap = IntArray(WIDTH) {
                        ((1f - abs(histogramInDecibel[it] / minDecibel)) * COLORMAP_MAX_INDEX).toInt()
                    }
                    val histogramInColors = IntArray(WIDTH) {
                        PARULA_COLORMAP[histogramNormalizedForColormap[it]]
                    }
                    histogramInColors
                })
            }
        }
    }

    //VIKTOR > BITMAP FROM DATA
    private fun bitmapFromData(colorArray: IntArray): Bitmap {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565)

        bitmap.setPixels(colorArray, 0, WIDTH, 0, 0, WIDTH, HEIGHT)
        return bitmap
    }

    //VIKTOR > ADD HISTOGRAM ROW
    private fun addHistogramRow(newRow: IntArray) {
        if (newRow.size != WIDTH) throw IllegalArgumentException(
            "The size of the new row must be: $WIDTH, instead it is ${newRow.size}"
        )
        val spectrogramArray = spectrogramColorArray.value?: return
        // Shift array forward by one Row
        spectrogramArray.copyInto(
            spectrogramArray,
            WIDTH,
            0,
            spectrogramArray.size - WIDTH
        )
        newRow.copyInto(spectrogramArray)
        spectrogramColorArray.value = spectrogramColorArray.value
    }

    //ON CLEARED
    override fun onCleared() {
        super.onCleared()

        //STOP TONE GENERATOR
        TONEGENERATOR.stop()

        //STOP SPECTRUM
    }
}