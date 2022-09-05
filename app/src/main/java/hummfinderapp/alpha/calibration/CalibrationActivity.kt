package hummfinderapp.alpha.calibration

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import hummfinderapp.alpha.R

val REQUEST_CODE = 200
class CalibrationActivity : AppCompatActivity() {
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration)

        //permission
        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)

        //viewModel provider
        val viewModel = ViewModelProvider(this).get(calibrationViewModel::class.java)
        val imageView = findViewById<ImageView>(R.id.calibrationspektrum)

        //playbutton
        val play = findViewById<ImageButton>(R.id.calMuteButton)
        play.setOnClickListener {
            if(permissionGranted){
                val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.DEFAULT,
                    viewModel.SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    viewModel.BUFFER_SIZE
                )
                viewModel.audioRecord = audioRecord
                viewModel.startSpectrogram()
                viewModel.generatedBitmap.observe(this, {imageView.setImageBitmap(it)})
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE){
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun startRecording(){
        Toast.makeText(this,"Audio variable initialized", Toast.LENGTH_SHORT).show()
    }
}