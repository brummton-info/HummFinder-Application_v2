package hummfinderapp.alpha.calibration

import android.Manifest
import androidx.lifecycle.ViewModel
import android.graphics.Bitmap
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jtransforms.fft.FloatFFT_1D
import kotlin.math.abs

class calibrationViewModel: ViewModel() {
    var TAG = "Calibration Activity"
    val SAMPLE_RATE = 44100

    //Size of a "chunk" of recorded samples that the window is updated with at each iteration
    // 16384 8192 32768
    val CHUNK_SIZE = 8192

    //Size of a window that gets fourier transformed
    val WINDOW_SIZE = 65536

    //Buffer size of the AudioRecord instance
    val BUFFER_SIZE = 2 * WINDOW_SIZE

    //The fourier transformed array is the same size as the input. Calculating the
    //absolutes about halves that size.
    val FREQUENCY_CUTOFF_UPPER = 300

    val WIDTH = FREQUENCY_CUTOFF_UPPER
    val HEIGHT = 200
    val BIN_SIZE = ((WINDOW_SIZE / 2) + 1) / WIDTH
    var audioRecord: AudioRecord? = null

    private val spectrogramColorArray = MutableLiveData<IntArray>(IntArray(WIDTH * HEIGHT))
    val generatedBitmap: LiveData<Bitmap> = Transformations.map(spectrogramColorArray) {
        bitmapFromData(it)
    }

    //init
    init {
        initializeRecording()
    }

    //onCleared()
    override fun onCleared() {
        super.onCleared()
        if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
            audioRecord?.stop()
            audioRecord?.release()
            Log.d(TAG,"Stop recording")
        }
    }

    //initializeRecording()
    private fun initializeRecording() {
        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG,"AudioRecord could not be initialized")
            return
        }

        audioRecord?.startRecording()
        Log.d(TAG,
            "Recording State should be AudioRecord.RECORDSTATE_RECORDING.Currently it is: ${audioRecord?.recordingState}"
        )
    }

    private val recordedSamples = ShortArray(WINDOW_SIZE)

    //startSpectrogram()
    fun startSpectrogram() {
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
                    audioRecord?.read(recordedSamples, WINDOW_SIZE - CHUNK_SIZE, CHUNK_SIZE)


                    val floatArray = FloatArray(recordedSamples.size) {
                        recordedSamples[it].toFloat()
                    }

                    fft.realForward(floatArray)
                    val absolutes = absoluteFromComplex(floatArray)

                    val histogram = absolutes.copyOf(FREQUENCY_CUTOFF_UPPER)

                    val histogramInDecibel = decibelFromAbsolute(histogram)

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

    private fun bitmapFromData(colorArray: IntArray): Bitmap {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565)

        bitmap.setPixels(colorArray, 0, WIDTH, 0, 0, WIDTH, HEIGHT)

        return bitmap
    }

    private fun addHistogramRow(newRow: IntArray) {
        if (newRow.size != WIDTH) throw IllegalArgumentException(
            "The size of the new row must be: $WIDTH, instead it is ${newRow.size}"
        )

        val spectrogramArray = spectrogramColorArray.value ?: return

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

}