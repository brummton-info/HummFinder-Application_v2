package hummfinderapp.beta.calibration

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
    companion object{

        const val SAMPLE_RATE = 44100

        /** Size of a "chunk" of recorded samples that the window is updated with at each
         *  iteration. ->
         */
        const val CHUNK_SIZE = 8192
        // 16384 8192 32768

        /** Size of a window that gets fourier transformed */
        const val WINDOW_SIZE = 65536

        /** Buffer size of the AudioRecord instance */
        const val BUFFER_SIZE = 2 * WINDOW_SIZE

        const val FREQUENCY_CUTOFF_UPPER = 300
        const val WIDTH = FREQUENCY_CUTOFF_UPPER
        const val HEIGHT = 200
        const val BIN_SIZE = ((WINDOW_SIZE / 2) + 1) / WIDTH
    }

    val TAG = "Calibration Activity"
    private val spectrogramColorArray = MutableLiveData<IntArray>(IntArray(WIDTH * HEIGHT))
    val generatedBitmap: LiveData<Bitmap> = Transformations.map(spectrogramColorArray) {bitmapFromData(it)}

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