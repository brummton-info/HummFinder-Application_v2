package hummfinderapp.alpha.matching

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.Runnable
import kotlin.math.PI
import kotlin.math.sin

class ToneGenerator(): Runnable{

    //variable initialization
    private var mute = false
    var frequency = 150.0
    private var level = 0.3

    //(java.lang) > for kotlin create a thread func
    private var thread: Thread? = null

    override fun run() {
        playTone()
    }

    //start
    fun start(){
        thread = Thread(this, "ToneGenerator")
        thread!!.start()
    }

    //stop
    fun stop(){
        val t = thread
        thread = null

        // Wait for the thread to exit
        while (t != null && t.isAlive) Thread.yield()
    }

    //playtone runnable
    private fun playTone(){

        val rate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC)
        val minSize =AudioTrack.getMinBufferSize(
            rate, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        // Find a suitable buffer size
        val sizes = intArrayOf(1024, 2048, 4096, 8192, 16384, 32768)
        var size = 0
        for (s in sizes) {
            if (s > minSize) {
                size = s
                break
            }
        }

        // Our Android api is set to 19. But the alternatives to this deprecated constructor demand
        // API levels of 21 and 23, so we can't use them and need to suppress the warning
        @Suppress("DEPRECATION")
        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC, rate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            size, AudioTrack.MODE_STREAM
        )

        val state = audioTrack.state
        if (state != AudioTrack.STATE_INITIALIZED) {
            audioTrack.release()
            return
        }
        audioTrack.play()

        val buffer = ShortArray(size)
        var f = frequency
        var l = 0.0
        var q = 0.0

        while (thread != null) {
            // Fill the current buffer
            for (i in buffer.indices) {
                f += (frequency - f) / 4096.0
                l += ((if (mute) 0.0 else level) * 16384.0 - l) / 4096.0
                q += if (q < 1) f * 2.0 / rate else f * 2.0 / rate - 2.0
                buffer[i] = (sin(PI * q) * l).toInt().toShort()
            }
            audioTrack.write(buffer, 0, buffer.size)
        }

        audioTrack.stop()
        audioTrack.release()
    }

}